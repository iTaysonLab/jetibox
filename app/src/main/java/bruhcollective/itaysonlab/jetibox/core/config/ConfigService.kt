package bruhcollective.itaysonlab.jetibox.core.config

import android.util.Log
import com.tencent.mmkv.MMKV
import javax.inject.Singleton

class ConfigService {
    private val instance: MMKV = MMKV.defaultMMKV()

    fun has(what: String) = instance.containsKey(what)

    fun string(of: String, def: String) = instance.getString(of, def)!!
    fun boolean(of: String, def: Boolean) = instance.getBoolean(of, def)
    fun int(of: String, def: Int) = instance.getInt(of, def)
    fun long(of: String, def: Long) = instance.getLong(of, def)
    fun bytes(of: String, def: ByteArray) = instance.getBytes(of, def)!!

    fun put(to: String, what: Any) {
        Log.d("MMKVService", "[put] $what -> $to [in: ${instance.allKeys()?.joinToString()}]")
        when (what) {
            is String -> instance.putString(to, what)
            is Int -> instance.putInt(to, what)
            is Long -> instance.putLong(to, what)
            is Boolean -> instance.putBoolean(to, what)
            is ByteArray -> instance.putBytes(to, what)
            else -> error("Not supported type")
        }
    }
}