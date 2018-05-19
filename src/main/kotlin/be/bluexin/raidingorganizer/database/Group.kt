@file:Suppress("unused")

package be.bluexin.raidingorganizer.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import java.time.LocalDateTime

class DbRaidGroup(id: EntityID<Int>) : IntEntity(id)

object RaidGroupsTable : IntIdTable() {
    val name = text("name")
    val icon = text("icon").nullable()

}

private abstract class RaidGroup {
    abstract val members: List<Character>
    abstract val applicants: List<Character>
    abstract val invited: List<Character>
    abstract val public: Boolean
    abstract val lineups: List<Lineup>
    // DKP? or should these be handled per lineup?
}

private abstract class Lineup {
    abstract val members: List<Character>
    abstract val applicants: List<Character>
    abstract val invited: List<Character>
    abstract val rollcalls: List<Rollcall>
    abstract val public: Boolean
    // DKP? or should these be handled per raid group?
}

private abstract class Rollcall {
    abstract val instance: DbInstance
    abstract val date_time: LocalDateTime
    abstract val presences: List<Presence>
    abstract val bids: List<Bid>
    abstract val precise_distribution: Boolean
    abstract val over: Boolean
    // Entry fee for buyers?

    abstract class Presence {
        abstract val character: Character
        abstract val signed_up: Boolean
        abstract val selected: Boolean
        abstract val loot_share: Boolean
        abstract val bosses: List<DbBoss>

    }

    abstract class Bid {
        abstract val character: Character
        // DKP? this can actually handle both
        abstract val price: Int
        abstract val loot: DbLoot
        abstract val amount: Int
    }
}

class DbRollcall(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DbRollcall>(RollcallsTable)

    var instance by RollcallsTable.instance
    var datetime by RollcallsTable.datetime
    var precise by RollcallsTable.precise
    var over by RollcallsTable.over
}

object RollcallsTable : IntIdTable() {
    val instance = reference("instance", InstancesTable)
    val datetime = datetime("datetime")
    val precise = bool("precise_distribution")
    val over = bool("over")
}

private abstract class Character // abstract? per game?

class DbCharacter(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DbCharacter>(CharactersTable)

    var name by CharactersTable.name
    var user by CharactersTable.user
}

object CharactersTable : IntIdTable() {
    val name = text("name")
    val user = reference("user", UsersTable).nullable()
}