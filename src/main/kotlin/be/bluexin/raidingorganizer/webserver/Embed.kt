package be.bluexin.raidingorganizer.webserver

import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.route

fun Routing.installEmbeds() {
    route("embed") {
        get<GetUser> {
            val target = it.getTarget()
            call.respond(target.httpStatusCode, FreeMarkerContent("embed/user.ftl",
                    mapOf("user" to getUser(), "target" to target)))
        }

        get<GameEndpoint> {
            val target = it.getDbTarget()
            call.respond(target.httpStatusCode, FreeMarkerContent("embed/game.ftl",
                    mapOf("user" to getUser(), "target" to target)))
        }
    }
}