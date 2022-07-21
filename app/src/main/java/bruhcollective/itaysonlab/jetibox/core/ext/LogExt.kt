package bruhcollective.itaysonlab.jetibox.core.ext

import android.util.Log
import bruhcollective.itaysonlab.jetibox.BuildConfig

fun debugLog(tag: String, msg: String) {
    if (BuildConfig.DEBUG) Log.d(tag, msg)
}