package be.bluexin.raidingorganizer.restclient

import be.bluexin.raidingorganizer.database.User
import org.jetbrains.exposed.sql.transactions.transaction

data class UserSession(val id: Long) {
    fun getUser() = transaction { User.findById(id) }
}
