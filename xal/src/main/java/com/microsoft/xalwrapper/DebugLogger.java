package com.microsoft.xalwrapper;

import android.util.Log;

import bruhcollective.itaysonlab.xal.BuildConfig;

public class DebugLogger {
    public static void Log(String str, String str2) {
        if (BuildConfig.DEBUG) {
            Log.d("XAL:Debug/" + str, str2);
        }
    }
}