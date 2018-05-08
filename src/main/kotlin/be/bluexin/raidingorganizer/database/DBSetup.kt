package be.bluexin.raidingorganizer.database

import be.bluexin.raidingorganizer.settings
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException
import kotlin.system.exitProcess

fun setupDB() {
    println("Setting up db connection...")
    val db = if (settings.dbpassword == null) Database.connect(settings.dburl, "com.mysql.cj.jdbc.Driver", user = settings.dbuser)
    else Database.connect(settings.dburl, "org.mariadb.jdbc.Driver", user = settings.dbuser, password = settings.dbpassword!!)
    try {
        println("Connected using ${db.vendor} database on version ${db.version}")
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                    GamesTable,
                    SessionStorageDatabase.UserSessionsTable,
                    UsersTable,
                    DiscordUserTable
            )
        }
    } catch (e: SQLException) {
        println("Couldn't connect to database.")
        e.printStackTrace()
        exitProcess(1)
    }
}