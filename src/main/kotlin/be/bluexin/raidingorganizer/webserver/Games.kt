package be.bluexin.raidingorganizer.webserver

import be.bluexin.raidingorganizer.database.DbGame
import be.bluexin.raidingorganizer.database.DbInstance
import be.bluexin.raidingorganizer.logger
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
            val target = it.getTarget()
            call.respond(target.httpStatusCode, FreeMarkerContent("game.ftl", mapOf(
                    "user" to getUser(),
                    "target" to target
            )))
        }
    }

    get<GameEndpoint.InstanceEndpoint> {
        logger.debug("Instance: $it")
        if (it.islug == null) {
            call.respond(HttpStatusCode.OK, FreeMarkerContent("instanceindex.ftl", mapOf(
                    "user" to getUser(),
                    "instances" to transaction { DbInstance.all().toList() },
                    "game" to it.game.getTarget()
            )))
        } else {
            val target = it.getTarget()
            call.respond(target.httpStatusCode, FreeMarkerContent("instance.ftl", mapOf(
                    "user" to getUser(),
                    "target" to target,
                    "game" to it.game.getTarget()
            )))
        }
    }
}