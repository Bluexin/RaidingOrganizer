package be.bluexin.raidingorganizer.webserver

import be.bluexin.raidingorganizer.restclient.UserSession
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.content.OutgoingContent
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.pipeline.PipelineContext
import io.ktor.response.respond
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import kotlin.reflect.KClass

/**
 * Sends a [message] as a response with status automatically set (404 for null, 200 for anything else)
 */
@Suppress("NOTHING_TO_INLINE")
suspend inline fun ApplicationCall.respondAutoStatus(message: Any?) {
    respond(if (message == null) HttpStatusCode.NotFound else HttpStatusCode.OK, mapOf(
            "status" to if (message == null) 404 else 200,
            "result" to message
    ))
}

class JacksonSerializer(private val mapper: ObjectMapper) : JsonSerializer {

    override suspend fun read(type: KClass<*>, response: HttpResponse): Any {
        if (!response.status.isSuccess()) throw IllegalArgumentException("Received status ${response.status.value}: ${response.status.description}")
        val text = response.readText()
        return mapper.readValue(text, type.java)
    }

    override fun write(data: Any): OutgoingContent = TextContent(mapper.writeValueAsString(data), ContentType.Application.Json)
}

fun PipelineContext<*, ApplicationCall>.getUser() = call.sessions.get<UserSession>()?.getUser()

fun String.escapeHTML(): String {
    val text = this@escapeHTML
    if (text.isEmpty()) return text

    return buildString(length) {
        for (idx in 0 until text.length) {
            val ch = text[idx]
            when (ch) {
                '\"' -> append("&quot")
                '&' -> append("&amp;")
                '<' -> append("&lt;")
                '>' -> append("&gt;")
                ' ' -> append("&#x20;")
                '!' -> append("&#x21;")
                '$' -> append("&#x24;")
                '%' -> append("&#x25;")
                '\'' -> append("&#x27;")
                '/' -> append("&#x2F;")
                '+' -> append("&#x2b;")
                ',' -> append("&#x2c;")
                '=' -> append("&#x3d;")
                '@' -> append("&#x40;")
                '[' -> append("&#x5b;")
                ']' -> append("&#x5d;")
                '`' -> append("&#x60;")
                '{' -> append("&#x7b;")
                '}' -> append("&#x7d;")
                else -> append(ch)
            }
        }
    }
}

val imageHeaders = mapOf(
        "bmp" to byteArrayOf(0x4D, 0x42),
        "jpeg" to byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte()),
        "png" to byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A),
        "ico" to byteArrayOf(0x00, 0x00, 0x01, 0x00),
        "gif" to byteArrayOf(0x47, 0x49, 0x46, 0x38)/*,
        "svg" to byteArrayOf(0x3C, 0x73, 0x76, 0x67)*/
)