package be.bluexin.raidingorganizer.database

import be.bluexin.raidingorganizer.webserver.escapeHTML
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IdTable

class DBDiscordUser(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, DBDiscordUser>(DiscordUserTable)

    var username
        get() = _username
        set(value) {
            _username = value.escapeHTML()
        }
    private var _username by DiscordUserTable.username
    var discriminator by DiscordUserTable.discriminator
    var avatar by DiscordUserTable.avatar
    var email by DiscordUserTable.email
    var token by DiscordUserTable.token
    var refreshtoken by DiscordUserTable.refreshToken

    val handle get() = "$username#$discriminator"
}

object DiscordUserTable : IdTable<String>("") {
    override val id = varchar("id", 255).primaryKey().entityId()

    val username = varchar("username", 255)
    val discriminator = varchar("discriminator", 255) // Shouldn't exceed 4 digits, but we never know when they may change that
    val avatar = varchar("avatar", 255).nullable()
    val email = varchar("email", 255).nullable()
    val token = varchar("token", 255)
    val refreshToken = varchar("refreshToken", 255)
}