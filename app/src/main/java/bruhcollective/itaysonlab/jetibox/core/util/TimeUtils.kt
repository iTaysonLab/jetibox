package bruhcollective.itaysonlab.jetibox.core.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object TimeUtils {
    fun msDateToLocal(timestamp: String, withTime: Boolean = false) = timestamp.asDateTime().let { datetime ->
        val date = datetime.formatDate(" ")
        val time = datetime.formatTime(":")

        return@let if (withTime) {
            "$time, $date"
        } else {
            date
        }
    }

    fun msDateToFilename(timestamp: String) = timestamp.asDateTime().let { datetime ->
        "${datetime.formatDate(".")}_${datetime.formatTime("-")}"
    }

    fun msDateToUnix(timestamp: String) = Instant.parse(timestamp).epochSeconds

    private fun String.asDateTime() = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())

    private fun LocalDateTime.formatDate(separator: String) = "${date.dayOfMonth.fillUp(2)}${separator}${date.monthNumber.fillUp(2)}${separator}${date.year}"
    private fun LocalDateTime.formatTime(separator: String) = "${time.hour.fillUp(2)}${separator}${time.minute.fillUp(2)}${separator}${time.second.fillUp(2)}"

    private fun Int.fillUp(digits: Int) = "%0${digits}d".format(this)
}