package be.bluexin.raidingorganizer.webserver

import be.bluexin.raidingorganizer.database.DbGame
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Routing
import org.jetbrains.exposed.sql.transactions.transaction

fun Routing.installGameEndpoints() {
    get<GameEndpoint> {
        if (it.slug == null) {
            call.respond(HttpStatusCode.OK, FreeMarkerContent("gameindex.ftl", mapOf(
                    "user" to getUser(),
                    "games" to transaction { DbGame.all().toList() }
            )))
        } else {
            val target = it.getDbTarget()
            call.respond(target.httpStatusCode, FreeMarkerContent("game.ftl", mapOf(
                    "user" to getUser(),
                    "target" to target
            )))
        }
    }
}