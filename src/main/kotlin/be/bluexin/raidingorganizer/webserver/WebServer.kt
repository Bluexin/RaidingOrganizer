package be.bluexin.raidingorganizer.webserver

import be.bluexin.raidingorganizer.database.SessionStorageDatabase
import be.bluexin.raidingorganizer.database.UsersTable
import be.bluexin.raidingorganizer.database.createIfAbsent
import be.bluexin.raidingorganizer.database.createOrUpdate
import be.bluexin.raidingorganizer.jacksonMapper
import be.bluexin.raidingorganizer.restclient.DiscordSession
import be.bluexin.raidingorganizer.restclient.UserSession
import be.bluexin.raidingorganizer.settings
import freemarker.cache.ClassTemplateLoader
import freemarker.template.TemplateExceptionHandler
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.content.files
import io.ktor.content.resources
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.jackson.JacksonConverter
import io.ktor.locations.*
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.*
import kotlinx.html.ScriptType
import kotlinx.html.body
import kotlinx.html.script
import kotlinx.html.unsafe
import org.jetbrains.exposed.sql.transactions.transaction

fun startWebserver() {
    embeddedServer(
            Netty,
            watchPaths = listOf("RaidingOrganizer"),
            port = settings.port,
            module = Application::main
    ).apply {
        start(wait = true)
    }
}

val discordLogin = OAuthServerSettings.OAuth2ServerSettings(
        name = "discord",
        authorizeUrl = "https://discordapp.com/api/oauth2/authorize",
        accessTokenUrl = "https://discordapp.com/api/oauth2/token",
        requestMethod = HttpMethod.Post,
        clientId = settings.discord_id,
        clientSecret = settings.discord_secret,
        defaultScopes = listOf("identify", "email")
)

private fun Application.installs() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
    install(Compression)
    install(AutoHeadResponse)
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(Index::class.java.classLoader, "templates")
        templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        defaultEncoding = "UTF-8"
    }
    install(StatusPages) {
        status(*HttpStatusCode.allStatusCodes.filter { !it.isSuccess() }.toTypedArray()) {
            call.respond(it, FreeMarkerContent("error.ftl", mapOf("user" to getUser(), "error" to it)))
        }
//        statusFile(HttpStatusCode.NotFound, filePattern = "static/errors/error#.html")
    }
    install(Sessions) {
        cookie<UserSession>("SESSION", storage = SessionStorageDatabase()) {
            cookie.path = "/"
        }
    }

    val client = HttpClient(Apache)
    environment.monitor.subscribe(ApplicationStopping) {
        client.close()
    }

    install(Authentication) {
        oauth {
            this@oauth.client = client
            providerLookup = { discordLogin }
            urlProvider = { redirectUrl(Login(), false) } // TODO: redirect back to where user was
        }
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(jacksonMapper))
    }
}

fun Application.main() {
    installs()

    install(Routing) {
        location<Static> {
            resources("static")
        }
        location<Static.Avatar> {
            files("upload/avatar")
        }

        get<Index> {
            call.respond(FreeMarkerContent("index.ftl", mapOf("user" to call.sessions.get<UserSession>()?.getUser())))
        }

        login()

        post<Logout> {
            call.sessions.clear<UserSession>()
            call.respond(HttpStatusCode.OK)
        }

        installApi()
        installUserEndpoints()
        installUploads()
        installEmbeds()
        installGameEndpoints()
    }
}

private fun Routing.login() {
    location<Login> {
        authenticate {
            handle {
                if (call.sessions.get<UserSession>() != null) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    val principal = call.principal<OAuthAccessTokenResponse.OAuth2>()
                    if (principal != null) {
                        val dSession = DiscordSession(principal.accessToken, principal.expiresIn, principal.refreshToken
                                ?: "none")
                        val dUser = dSession.getUser()
                        if (dUser != null) {
                            val dbDUser = transaction {
                                be.bluexin.raidingorganizer.database.DBDiscordUser.createOrUpdate(dUser.id) {
                                    username = dUser.username
                                    discriminator = dUser.discriminator
                                    avatar = dUser.avatar
                                    email = dUser.email
                                    token = dSession.accessToken
                                    refreshtoken = dSession.refreshToken
                                }
                            }
                            val user = transaction {
                                be.bluexin.raidingorganizer.database.User.createIfAbsent(find = { UsersTable.discordUser eq dbDUser.id }, create = {
                                    name = dbDUser.handle
                                    discordUser = dbDUser
                                    avatar = "discord"
                                    theme = Themes.DARKLY
                                })
                            }
                            call.sessions.set(UserSession(user.id.value))
                            call.respondHtml {
                                body {
                                    script(type = ScriptType.textJavaScript) {
                                        unsafe {
                                            raw("""
                                                window.localStorage.setItem('logged_in', Date.now().toString());
                                                window.close();
                                            """)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun <T : Any> ApplicationCall.redirectUrl(t: T, secure: Boolean = true): String {
    val hostPort = request.host()!! + request.port().let { port -> if (port == 80) "" else ":$port" }
    val protocol = when {
        secure -> "https"
        else -> "http"
    }
    return "$protocol://$hostPort${application.locations.href(t)}"
}
