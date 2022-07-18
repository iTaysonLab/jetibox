package com.microsoft.xal.androidjava;

import android.content.Context;

public class Storage {
    public static String getStoragePath(Context context) {
        return context.getFilesDir().getPath();
    }
}