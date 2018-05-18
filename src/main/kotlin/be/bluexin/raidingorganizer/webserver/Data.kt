package be.bluexin.raidingorganizer.webserver

data class Game(
        val slug: String,
        val name: String,
        val background: String?,
        val description: String?,
        val url: String?
)