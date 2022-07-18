package com.microsoft.xal.browser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.util.Base64;
import com.microsoft.xal.logging.XalLogger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class BrowserSelector {
    private static final Map<String, String> customTabsAllowedBrowsers = new HashMap<>();

    static {
        customTabsAllowedBrowsers.put("com.android.chrome", "OJGKRT0HGZNU+LGa8F7GViztV4g=");
        customTabsAllowedBrowsers.put("org.mozilla.firefox", "kg9Idqale0pqL0zK9l99Kc4m/yw=");
        customTabsAllowedBrowsers.put("com.microsoft.emmx", "P2QOJ59jvOpxCCrn6MfvotoBTK0=");
        customTabsAllowedBrowsers.put("com.sec.android.app.sbrowser", "nKUXDzgZGd/gRG/NqxixmhQ7MWM=");
    }

    private static boolean browserAllowedForCustomTabs(Context context, XalLogger xalLogger, String str) {
        PackageInfo packageInfo;
        String str2 = customTabsAllowedBrowsers.get(str);
        if (str2 == null) {
            return false;
        }
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, 64);
            if (packageInfo == null) {
                xalLogger.Important("No package info found for package: " + str);
                return false;
            }
            for (Signature signature : packageInfo.signatures) {
                if (hashFromSignature(signature).equals(str2)) {
                    return true;
                }
            }
        } catch (PackageManager.NameNotFoundException e2) {
            xalLogger.Error("browserAllowedForCustomTabs() Error in getPackageInfo(): " + e2);
        } catch (NoSuchAlgorithmException e3) {
            xalLogger.Error("browserAllowedForCustomTabs() Error in hashFromSignature(): " + e3);
        }

        return false;
    }

    private static boolean browserInfoImpliesNoUserDefault(BrowserSelectionResult.BrowserInfo browserInfo) {
        return browserInfo.versionCode == 0 && browserInfo.versionName.equals("none");
    }

    private static boolean browserSupportsCustomTabs(Context context, String str) {
        for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentServices(new Intent("android.support.customtabs.action.CustomTabsService"), 0)) {
            if (resolveInfo.serviceInfo.packageName.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private static String hashFromSignature(Signature signature) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(signature.toByteArray());
        return Base64.encodeToString(messageDigest.digest(), 2);
    }

    public static BrowserSelectionResult selectBrowser(Context context, boolean z) {
        String str;
        XalLogger xalLogger = new XalLogger("BrowserSelector");
        try {
            BrowserSelectionResult.BrowserInfo userDefaultBrowserInfo = userDefaultBrowserInfo(context, xalLogger);
            boolean z2 = false;
            if (z) {
                str = "inProcRequested";
            } else if (browserInfoImpliesNoUserDefault(userDefaultBrowserInfo)) {
                str = "noDefault";
            } else {
                String str2 = userDefaultBrowserInfo.packageName;
                if (!browserSupportsCustomTabs(context, str2)) {
                    xalLogger.Important("selectBrowser() Default browser does not support custom tabs.");
                    str = "CTNotSupported";
                } else if (!browserAllowedForCustomTabs(context, xalLogger, str2)) {
                    xalLogger.Important("selectBrowser() Default browser supports custom tabs, but is not allowed.");
                    str = "CTSupportedButNotAllowed";
                } else {
                    xalLogger.Important("selectBrowser() Default browser supports custom tabs and is allowed.");
                    str = "CTSupportedAndAllowed";
                    z2 = true;
                }
            }
            BrowserSelectionResult browserSelectionResult = new BrowserSelectionResult(userDefaultBrowserInfo, str, z2);
            xalLogger.close();
            return browserSelectionResult;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                try {
                    xalLogger.close();
                } catch (Throwable th3) {
                    th.addSuppressed(th3);
                }
                throw th2;
            }
        }
    }

    private static BrowserSelectionResult.BrowserInfo userDefaultBrowserInfo(Context context, XalLogger xalLogger) {
        String str;
        ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://microsoft.com")), 65536);
        String str2 = resolveActivity == null ? null : resolveActivity.activityInfo.packageName;
        if (str2 == null) {
            xalLogger.Important("userDefaultBrowserInfo() No default browser resolved.");
            return new BrowserSelectionResult.BrowserInfo("none", 0, "none");
        } else if (str2.equals("android")) {
            xalLogger.Important("userDefaultBrowserInfo() System resolved as default browser.");
            return new BrowserSelectionResult.BrowserInfo("android", 0, "none");
        } else {
            int i2 = -1;
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(str2, 0);
                i2 = packageInfo.versionCode;
                str = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e2) {
                xalLogger.Error("userDefaultBrowserInfo() Error in getPackageInfo(): " + e2);
                str = "unknown";
            }
            xalLogger.Important("userDefaultBrowserInfo() Found " + str2 + " as user's default browser.");
            return new BrowserSelectionResult.BrowserInfo(str2, i2, str);
        }
    }
}