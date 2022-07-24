package bruhcollective.itaysonlab.jetibox.core.config

import bruhcollective.itaysonlab.jetibox.core.ext.debugLog
import com.tencent.mmkv.MMKV
import java.util.*
import kotlin.reflect.KProperty

class ConfigService {
    private val instance: MMKV = MMKV.defaultMMKV()

    fun has(what: String) = instance.containsKey(what)

    fun string(of: String, def: String) = instance.getString(of, def)!!
    fun boolean(of: String, def: Boolean) = instance.getBoolean(of, def)
    fun int(of: String, def: Int) = instance.getInt(of, def)
    fun long(of: String, def: Long) = instance.getLong(of, def)
    fun bytes(of: String, def: ByteArray) = instance.getBytes(of, def)!!

    fun put(to: String, what: Any) {
        debugLog("MMKVService", "[put] $what -> $to [in: ${instance.allKeys()?.joinToString()}]")
        when (what) {
            is String -> instance.putString(to, what)
            is Int -> instance.putInt(to, what)
            is Long -> instance.putLong(to, what)
            is Boolean -> instance.putBoolean(to, what)
            is ByteArray -> instance.putBytes(to, what)
            else -> error("Not supported type")
        }
    }

    // Implementations

    var userColor: String by StringCfg("xbl.user.color", "")

    var marketCountry: String by StringCfg("xbl.market", "US")
    var marketLanguage: String by StringCfg("xbl.lang", Locale.getDefault().language)

    // Abstracts

    private inner class StringCfg (private val key: String, private val default: String) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>) = string(key, default)
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) = put(key, value)
    }
}