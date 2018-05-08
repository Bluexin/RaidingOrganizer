package be.bluexin.raidingorganizer

import be.bluexin.raidingorganizer.database.setupDB
import be.bluexin.raidingorganizer.webserver.startWebserver

fun main(args: Array<String>) {
    setupDB()
    startWebserver()
}
