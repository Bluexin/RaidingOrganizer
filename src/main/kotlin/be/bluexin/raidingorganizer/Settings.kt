package be.bluexin.raidingorganizer

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess

val jacksonMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .enable(SerializationFeature.INDENT_OUTPUT)!!

val settings: Settings by lazy {
    val settingsFile = File("settings.json")
    if (!settingsFile.exists()) {
        jacksonMapper.writerWithDefaultPrettyPrinter().writeValue(settingsFile, Settings(
                dburl = "database url (in the form of jdbc:mysql://ip:port/database)",
                dbuser = "database username",
                dbpassword = "database password",
                hostemail = "host e-mail",
                port = 8080,
                discord_id = "discord client ID",
                discord_secret = "discord client secret"
        ))
        println("Default config file was generated. Please edit with the correct info.")
        exitProcess(0)
    } else {
        try {
            jacksonMapper.readValue<Settings>(settingsFile)
        } catch (e: Exception) {
            println("Config file couldn't be parsed. Please consider deleting it to regenerate it.")
            exitProcess(1)
        }
    }
}

val logger by lazy {
    LoggerFactory.getLogger("RaidingOrganizer")!!
}

data class Settings(
        val dburl: String,
        val dbuser: String,
        val dbpassword: String? = null,
        val hostemail: String,
        val port: Int,
        val discord_id: String,
        val discord_secret: String
)