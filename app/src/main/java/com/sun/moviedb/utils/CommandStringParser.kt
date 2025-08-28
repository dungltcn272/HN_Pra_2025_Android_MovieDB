package com.sun.moviedb.utils

object CommandStringParser {
    private const val DELIMITER = "|"
    private const val PARTS_EXPECTED_MIN = 3
    private const val PARTS_EXPECTED_WITH_VALUE = 4


    data class ParsedCommand(
        val type: String,
        val senderId: String,
        val clientTimestamp: Long,
        val value: String?
    )

    fun createCommandString(type: String, senderId: String, value: String? = null): String {
        val clientTimestamp = System.currentTimeMillis()
        return "$type$DELIMITER$senderId$DELIMITER$clientTimestamp$DELIMITER${value ?: ""}"
    }

    fun parseCommandString(commandStr: String?): ParsedCommand? {
        if (commandStr.isNullOrBlank()) {
            return null
        }
        val parts = commandStr.split(DELIMITER)
        if (parts.size < PARTS_EXPECTED_MIN) {
            return null
        }
        return try {
            ParsedCommand(
                type = parts[0],
                senderId = parts[1],
                clientTimestamp = parts[2].toLong(),
                value = if (parts.size >= PARTS_EXPECTED_WITH_VALUE) parts[3] else null
            )
        } catch (e: Exception) {
            null
        }
    }
}

object CommandType {
    const val PLAY = "PLAY"
    const val PAUSE = "PAUSE"
    const val SEEK = "SEEK"
}
