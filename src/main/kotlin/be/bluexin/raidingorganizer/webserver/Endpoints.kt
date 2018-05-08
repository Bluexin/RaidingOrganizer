package be.bluexin.raidingorganizer.webserver

import io.ktor.locations.Location

@Location("")
class Index

@Location("login")
class Login

@Location("logout")
class Logout

@Location("user/{id}")
data class GetUser(val id: Long) {
    data class Post(val username: String?, val avatar: String?, val theme: Themes?)
    @Location("sync")
    data class SyncDiscord(val user: GetUser)
}

@Location("upload/{target}")
data class Upload(val target: Target) {
    enum class Target {
        AVATAR, avatar /* tmp workaround */;

        override fun toString(): String {
            return super.toString().toLowerCase()
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

@Location(path = "game/{slug}")
data class GameEndpoint(val slug: String)
