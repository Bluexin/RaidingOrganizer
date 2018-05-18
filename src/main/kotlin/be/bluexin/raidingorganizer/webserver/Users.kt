package be.bluexin.raidingorganizer.webserver

import be.bluexin.raidingorganizer.database.createOrUpdate
import be.bluexin.raidingorganizer.restclient.DiscordSession
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun Routing.installUserEndpoints() {
    get<GetUser> {
        val target = it.getTarget()
        call.respond(target.httpStatusCode, FreeMarkerContent("user.ftl", mapOf(
                "user" to getUser(),
                "target" to target
        ), "e"))
    }

    post<GetUser> {
        val user = getUser()
        val post = call.receive<GetUser.Post>()
        if (user?.id?.value == it.id) {
            val validAvatar = !post.avatar.isNullOrBlank() && (post.avatar == "discord" || File("upload/${Upload.Target.AVATAR}", post.avatar).exists())
            transaction {
                if (!post.username.isNullOrBlank()) user.name = post.username!!
                if (validAvatar) user.avatar = post.avatar
                if (post.theme != null) user.theme = post.theme
            }
            call.respond(HttpStatusCode.Accepted)
        } else call.respond(HttpStatusCode.Forbidden)
    }

    post<GetUser.SyncDiscord> {
        val user = getUser()
        if (user?.id?.value == it.user.id) {
            val dUser = DiscordSession(user.discordUser.token, 0, user.discordUser.refreshtoken).getUser()
            if (dUser != null) transaction {
                be.bluexin.raidingorganizer.database.DBDiscordUser.createOrUpdate(dUser.id) {
                    username = dUser.username
                    discriminator = dUser.discriminator
                    avatar = dUser.avatar
                    email = dUser.email
                }
            }
            call.respond(HttpStatusCode.Accepted)
        } else call.respond(HttpStatusCode.Forbidden)
    }
}