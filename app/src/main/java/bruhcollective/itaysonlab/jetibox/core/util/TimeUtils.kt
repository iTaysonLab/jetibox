package bruhcollective.itaysonlab.jetibox.core.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    private val msFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT)

    fun msDateToLocal(src: String?): String {
        if (src.isNullOrEmpty()) return ""
        val localFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        return localFormat.format(msFormat.parse(src)!!)
    }
}