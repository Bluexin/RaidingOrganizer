package be.bluexin.raidingorganizer.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.and

class DbGame(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DbGame>(GamesTable) {
        fun findBySlug(slug: String) = find { GamesTable.slug eq slug }.firstOrNull()
    }

    var name by GamesTable.name
    var slug by GamesTable.slug
    var background by GamesTable.background
    var description by GamesTable.description
    var url by GamesTable.url

    val instances by DbInstance referrersOn InstancesTable.game
    fun findInstanceBySlug(slug: String) = DbInstance.find { InstancesTable.slug eq slug and (InstancesTable.game eq id) }.firstOrNull()
}

object GamesTable : IntIdTable() {
    val slug = varchar("slug", 255).uniqueIndex()
    val name = varchar("name", 255)
    val background = text("background").nullable()
    val description = text("description").nullable()
    val url = text("url").nullable()
}

class DbInstance(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DbInstance>(InstancesTable) {
        fun findBySlug(slug: String) = find { InstancesTable.slug eq slug }.firstOrNull()
    }

    var name by InstancesTable.name
    var altname by InstancesTable.altname
    var slug by InstancesTable.slug
    var background by InstancesTable.background
    var description by InstancesTable.description
    var url by InstancesTable.url
    var game by DbGame referencedOn InstancesTable.game
    var spots by InstancesTable.spots

    val bosses by DbBoss referrersOn BossesTable.instance
    fun findBossByNum(num: Int) = DbBoss.find { BossesTable.num eq num and (BossesTable.instance eq id) }.firstOrNull()
}

object InstancesTable : IntIdTable() {
    val slug = varchar("slug", 255)
    val name = varchar("name", 255)
    val altname = varchar("altname", 255)
    val background = text("background").nullable()
    val description = text("description").nullable()
    val url = text("url").nullable()
    val game = reference("game", GamesTable)
    val spots = integer("spots")

    init {
        uniqueIndex(slug, game)
    }
}

class DbBoss(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DbBoss>(BossesTable)

    var name by BossesTable.name
    var altname by BossesTable.altname
    var background by BossesTable.background
    var description by BossesTable.description
    var url by BossesTable.url
    var instance by DbInstance referencedOn BossesTable.instance
    var num by BossesTable.num

    val loot by DbLoot referrersOn LootTable.boss
}

object BossesTable : IntIdTable() {
    val name = varchar("name", 255)
    val altname = varchar("altname", 255)
    val background = text("background").nullable()
    val description = text("description").nullable()
    val url = text("url").nullable()
    val instance = reference("instance", InstancesTable)
    val num = integer("num")
}

class DbLoot(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DbLoot>(LootTable)

    var boss by DbBoss referencedOn LootTable.boss
    var item by DbItem referencedOn LootTable.item
}

object LootTable : IntIdTable() {
    val boss = reference("boss", BossesTable)
    val item = reference("item", ItemsTable)
}

class DbItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DbItem>(ItemsTable)

    var name by ItemsTable.name
    var image by ItemsTable.image
    var type by ItemType referencedOn ItemsTable.type
    var rank by ItemsTable.rank

    val loots by DbLoot referrersOn LootTable.item
}

object ItemsTable : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex()
    val image = text("image").nullable()
    val type = reference("type", TypesTable)
    val rank = integer("rank").nullable()
}

class ItemType(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ItemType>(TypesTable)

    var name by TypesTable.name

    val items by DbItem referrersOn ItemsTable.type
}

object TypesTable : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex()
}