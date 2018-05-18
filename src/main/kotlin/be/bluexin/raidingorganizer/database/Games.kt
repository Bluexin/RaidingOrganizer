package be.bluexin.raidingorganizer.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

class DbGame(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DbGame>(GamesTable) {
        fun findBySlug(slug: String) = find { GamesTable.slug eq slug }.firstOrNull()
    }

    var name by GamesTable.name
    var slug by GamesTable.slug
    var background by GamesTable.background
    var description by GamesTable.description
    var url by GamesTable.url
}

object GamesTable : IntIdTable() {
    val slug = varchar("slug", 255).uniqueIndex()
    val name = varchar("name", 255)
    val background = text("background").nullable()
    val description = text("description").nullable()
    val url = text("url").nullable()
}