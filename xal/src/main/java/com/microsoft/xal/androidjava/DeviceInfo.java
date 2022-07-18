package com.microsoft.xal.androidjava;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class DeviceInfo {
    public static String GetDeviceId(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "android_id");
        int[] iArr = {8, 4, 4, 4, 12};
        int i2 = 0;
        if (string.length() < 32) {
            string = String.format("%0" + (32 - string.length()) + "d", 0) + string;
        }
        StringBuilder sb2 = new StringBuilder();
        int i3 = 0;
        while (i2 < 5) {
            if (i2 != 0) {
                sb2.append("-");
            }
            int i4 = iArr[i2] + i3;
            sb2.append(string.substring(i3, i4));
            i2++;
            i3 = i4;
        }
        return sb2.toString();
    }

    public static String GetOsVersion() {
        return Build.VERSION.RELEASE;
    }
}