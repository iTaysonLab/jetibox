package com.microsoft.xalwrapper;

import android.util.Log;

public class DebugLogger {
    public static void Log(String str, String str2) {
        Log.d("XAL:Debug/" + str, str2);
    }
}