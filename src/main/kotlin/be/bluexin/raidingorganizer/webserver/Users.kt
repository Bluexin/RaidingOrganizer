package be.bluexin.raidingorganizer.webserver

import be.bluexin.raidingorganizer.database.User
import be.bluexin.raidingorganizer.database.createOrUpdate
import be.bluexin.raidingorganizer.logger
import be.bluexin.raidingorganizer.restclient.DiscordSession
import be.bluexin.raidingorganizer.restclient.UserSession
import io.ktor.application.call
import io.ktor.content.PartData
import io.ktor.content.forEachPart
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.BadContentTypeFormatException
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.network.util.ioCoroutineDispatcher
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.withContext
import kotlinx.coroutines.experimental.yield
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.swing.ImageIcon

fun Routing.installUserEndpoints() {

    get<GetUser> {
        val target = transaction { User.findById(it.id) }
        call.respond(if (target != null) HttpStatusCode.OK else HttpStatusCode.NotFound, FreeMarkerContent("user.ftl", mapOf(
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
        val user = call.sessions.get<UserSession>()?.getUser()
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

    post<Upload> {
        val user = getUser()
        if (user != null) {
            val multipart = call.receiveMultipart()
            var filename = "missing.png"
            var file: File? = null
            var ok = true
            multipart.forEachPart { part ->
                if (!ok) return@forEachPart
                logger.debug("${part.name} -> ${part.contentType}")

                when (part) {
                    is PartData.FileItem -> {
                        logger.debug(part.originalFileName.toString())
                    }
                    is PartData.FormItem -> {
                        logger.debug(part.value)
                    }
                }

                when (part) {
                    is PartData.FormItem -> if (part.name == "type" && !part.value.startsWith("image")) ok = false
                    is PartData.FileItem -> if (part.contentType?.contentType == "image") {
                        val ext = File(part.originalFileName).extension
                        filename = "${user.id}.$ext"
                        file = File("upload/${it.target}", filename)
                        part.streamProvider().use { its ->
                            file!!.outputStream().buffered().use {
                                try {
                                    its.copyToSuspend(it, headerCheck = imageHeaders.values, formatName = imageHeaders.keys.joinToString(prefix = "Any of "))
                                } catch (_: BadContentTypeFormatException) {
                                }
                            }
                        }
                    }
                }

                part.dispose()
            }

            logger.debug("Width: ${ImageIcon(file?.toURI()?.toURL()).image?.getWidth(null).toString()}")

            if (file == null || ImageIcon(file!!.toURI().toURL()).iconWidth < 1) {
                file?.delete()
                call.respond(HttpStatusCode.UnsupportedMediaType)
            } else call.respond(mapOf(
                    "url" to "${call.request.uri.substringBeforeLast("/upload")}/static/$${it.target}/$filename",
                    "file" to filename,
                    "user" to user.id.value
            ))
        } else call.respond(HttpStatusCode.Forbidden)
    }
}

private suspend fun InputStream.copyToSuspend(
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        yieldSize: Int = 4 * 1024 * 1024,
        dispatcher: CoroutineDispatcher = ioCoroutineDispatcher,
        headerCheck: Collection<ByteArray> = emptyList(),
        formatName: String = "unknown"
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            if (bytesCopied == 0L) {
                val isImage = headerCheck.isEmpty() || headerCheck.any {
                    if (buffer.size < it.size) return@any false
                    for (i in it.indices) {
                        if (buffer[i] != it[i]) return@any false
                    }
                    true
                }

                if (!isImage) throw BadContentTypeFormatException("unknown (expected $formatName)")
            }
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}