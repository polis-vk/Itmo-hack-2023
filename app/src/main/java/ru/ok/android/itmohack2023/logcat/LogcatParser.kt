package ru.ok.android.itmohack2023.logcat

import java.lang.RuntimeException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LogcatParser {
    private val timeRegex = Regex("\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}")
    private val identificationRegex = Regex("\\d+ \\d+")
    private val messRegex = Regex("D|I|E|A|W")
    private val senderRegex = Regex("\\w+")
    private val typeRegex = Regex("GET|POST|DELETE")

    private fun process(match: MatchResult?): String {
        if (match == null) {
            throw RuntimeException("Log hasn't matched")
        }
        return match.value
    }

    fun parse(input: String): LogcatInfo? {
        try {
            var log = input
            val time = "2023-" + process(timeRegex.find(log))
            log = log.replaceFirst(timeRegex, "")

            val identification = process(identificationRegex.find(log))
            log = log.replaceFirst(identificationRegex, "")

            log = log.replaceFirst(messRegex, "").trim()

            val sender = process(senderRegex.find(log))
            log = log.replaceFirst(senderRegex, "")
            val logBody = log.replaceFirst(":", "").trim()

            return LogcatInfo(
                time = LocalDateTime.parse(
                    time,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                ),
                identification = identification,
                sender = sender,
                logBody = logBody
            )
        } catch (e: Exception) {
            return null
        }
    }

    fun processAnswer(logs: List<LogcatInfo>) : LogcatRequest? {
        try {
            val start = logs.filter { it.logBody.startsWith("-->") }[0]
            val response = logs.filter { it.logBody.startsWith("<--") }[0]
            val finish = logs.filter { it.logBody.startsWith("<-- END HTTP") }[0]
            val type = process(typeRegex.find(start.logBody))
            val bytes = process(Regex("\\d+").find(finish.logBody)).toLong()
            return LogcatRequest(startTimestamp = start.time,
                finishTimestamp = finish.time,
                request = start.logBody.replaceFirst("--> ", ""),
                response = response.logBody.replaceFirst("<-- ", ""),
                bytesCount = bytes,
                type = type
            )
        } catch (e: Exception) {
            return null
        }
    }
}
