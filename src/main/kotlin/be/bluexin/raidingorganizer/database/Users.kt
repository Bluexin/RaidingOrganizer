package be.bluexin.raidingorganizer.database

import be.bluexin.raidingorganizer.webserver.Themes
import be.bluexin.raidingorganizer.webserver.escapeHTML
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongIdTable

class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : EagerEntityClass<Long, User>(UsersTable)

    var name
        get() = _name
        set(value) {
            _name = value.escapeHTML()
        }
    private var _name by UsersTable.name
    var discordUser by DBDiscordUser.referencedOn(UsersTable.discordUser).cached(this)
    var avatar by UsersTable.avatar
    var theme by UsersTable.theme

    val avatarUrl
        get() = if (avatar == "discord") {
            if (discordUser.avatar != null) "https://cdn.discordapp.com/avatars/${discordUser.id.value}/${discordUser.avatar}.png?size=512"
            else "https://cdn.discordapp.com/avatars/${discordUser.discriminator.toInt() % 5}.png?size=512"
        } else "/static/avatar/$avatar"
}

object UsersTable : LongIdTable() {
    val name = varchar("name", 255).uniqueIndex()
    val discordUser = reference("discordUser", DiscordUserTable).uniqueIndex()
    val avatar = varchar("avatar", 255).nullable()
    val theme = enumerationByName("theme", 255, Themes::class.java)
}
