package be.bluexin.raidingorganizer.webserver

import be.bluexin.raidingorganizer.database.DbGame
import be.bluexin.raidingorganizer.database.User
import io.ktor.locations.Location
import org.jetbrains.exposed.sql.transactions.transaction

@Location("")
class Index

@Location("login")
class Login

@Location("logout")
class Logout

@Location("user/{id}")
data class GetUser(val id: Long) {

    fun getTarget() = transaction { User.findById(id) }

    data class Post(val username: String?, val avatar: String?, val theme: Themes?)
    @Location("sync")
    data class SyncDiscord(val user: GetUser)
}

interface CustomMapping<T> {
    fun encode(what: T): String = what.toString()
    fun decode(encoded: String): T?
}

@Location("upload/{target}")
data class Upload(val target: Target) {
    enum class Target {
        AVATAR, avatar /* tmp workaround */;

        override fun toString() = super.toString().toLowerCase()

        companion object : CustomMapping<Target> {
            override fun decode(encoded: String): Target? {
                return Target.values().firstOrNull { encode(it) == encoded }
            }
        }
    }
}

@Location("static")
class Static {
    @Location("avatar")
    class Avatar
}

@Location("api")
class Api

@Location(path = "game/{slug?}")
data class GameEndpoint(val slug: String? = null) {
    fun getTarget() = if (slug == null) null else transaction { DbGame.findBySlug(slug) }

    @Location(path = "instance/{islug?}")
    data class InstanceEndpoint(val islug: String? = null, val game: GameEndpoint) {
        fun getTarget() = if (islug == null) null else transaction { game.getTarget()?.findInstanceBySlug(islug) }
    }
}
