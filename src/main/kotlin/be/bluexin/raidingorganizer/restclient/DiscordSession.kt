package be.bluexin.raidingorganizer.restclient

import be.bluexin.raidingorganizer.jacksonMapper
import be.bluexin.raidingorganizer.logger
import be.bluexin.raidingorganizer.webserver.JacksonSerializer
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.engine.config
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.util.AttributeKey
import org.apache.http.message.BasicHeader

const val base = "https://discordapp.com/api/v6/"

data class DiscordSession(val accessToken: String, val expiresIn: Long, val refreshToken: String) {
    suspend fun getUser(): DiscordUser? {
        val client = HttpClient(Apache.config {
            followRedirects = true
            customizeClient {
                setDefaultHeaders(listOf(
                        BasicHeader("DiscordUser-Agent", "Test Website Connection (localhost)"),
                        BasicHeader("Authorization", "Bearer $accessToken")
                ))
            }
        }) {
            install(JsonFeature) {
                serializer = JacksonSerializer(jacksonMapper)
            }
        }
        return try {
            client.get<DiscordUser>("$base/users/@me") {
                setAttributes {
                    put(AttributeKey("token"), accessToken)
                }
            }
        } catch (e: Exception) {
            logger.warn("Catched exception when fetching Discord user.", e)
            null
        }
    }

    suspend fun refresh() {
        logger.debug("Should refresh token. TODO") // TODO
    }
}

data class DiscordUser(
        val id: String,
        val username: String,
        val discriminator: String,
        val avatar: String?,
        val bot: Boolean = false,
        val mfa_enabled: Boolean = false,
        val verified: Boolean = false,
        val email: String? = null
) {
    val handle = "$username#$discriminator"
}