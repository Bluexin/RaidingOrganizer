package be.bluexin.raidingorganizer.webserver

import be.bluexin.raidingorganizer.database.DbGame
import be.bluexin.raidingorganizer.database.GamesTable
import be.bluexin.raidingorganizer.database.model
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.locations.get
import io.ktor.locations.location
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

fun Routing.api() {
    location<Api> {
        install(StatusPages) {
            status(*HttpStatusCode.allStatusCodes.filter { !it.isSuccess() }.toTypedArray()) {
                call.respond(it, mapOf(
                        "status" to it.value,
                        "result" to it.description
                )) // FIXME: this doesn't override the default one :x
            }
        }

        get("") {
            call.respondAutoStatus(mapOf(
                    "status" to "online",
                    "database" to if (TransactionManager.isInitialized()) "online" else "offline",
                    "version" to "0.1-SNAPSHOT"
            ))
        }

        get<GameEndpoint> {
            val g = transaction { DbGame.find { GamesTable.name like it.slug }.firstOrNull().model }
            call.respondAutoStatus(g)
        }
    }
}