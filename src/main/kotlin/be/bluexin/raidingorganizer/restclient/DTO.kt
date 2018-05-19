package be.bluexin.raidingorganizer.restclient

import java.time.LocalDateTime

data class Equipment(
        val name: String,
        val img: String,
        val type: String,
        val firstAdded: LocalDateTime,
        val rank: Int? = -1
)