package bruhcollective.itaysonlab.jetibox.core.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    private val msFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT)
    private val msFormatWithMs = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'", Locale.ROOT)

    fun msDateToLocal(src: String?, msTimeWithMs: Boolean = false, withTime: Boolean = false): String {
        if (src.isNullOrEmpty()) return ""
        val localFormat = SimpleDateFormat(if (withTime) "HH:mm:ss', 'd MMMM yyyy" else "d MMMM yyyy", Locale.getDefault())
        return localFormat.format((if (msTimeWithMs) msFormatWithMs else msFormat).parse(src)!!)
    }

    fun msDateToUnix(src: String?, countMs: Boolean): Long {
        if (src.isNullOrEmpty()) return 0
        return (if (countMs) msFormatWithMs else msFormat).parse(src)?.time ?: 0
    }

    fun msDateToFilename(src: String?): String {
        if (src.isNullOrEmpty()) return ""
        val localFormat = SimpleDateFormat("dd.MM.yyyy'_'HH-mm-ss", Locale.getDefault())
        return localFormat.format(msFormatWithMs.parse(src)!!)
    }
}