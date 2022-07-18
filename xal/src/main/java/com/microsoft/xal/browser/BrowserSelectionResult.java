package com.microsoft.xal.browser;

import java.util.Locale;

/* loaded from: classes2.dex */
public class BrowserSelectionResult {
    private final BrowserInfo m_defaultBrowserInfo;
    private final String m_notes;
    private final boolean m_useCustomTabs;

    /* loaded from: classes2.dex */
    static class BrowserInfo {
        public final String packageName;
        public final int versionCode;
        public final String versionName;

        /* JADX INFO: Access modifiers changed from: package-private */
        public BrowserInfo(String str, int i2, String str2) {
            this.packageName = str;
            this.versionCode = i2;
            this.versionName = str2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BrowserSelectionResult(BrowserInfo browserInfo, String str, boolean z) {
        this.m_defaultBrowserInfo = browserInfo;
        this.m_notes = str;
        this.m_useCustomTabs = z;
    }

    public String packageName() {
        if (this.m_useCustomTabs) {
            return this.m_defaultBrowserInfo.packageName;
        }
        return null;
    }

    public String toString() {
        Locale locale = Locale.US;
        Object[] objArr = new Object[4];
        objArr[0] = this.m_useCustomTabs ? "CT" : "WK";
        BrowserInfo browserInfo = this.m_defaultBrowserInfo;
        objArr[1] = browserInfo.packageName;
        objArr[2] = this.m_notes;
        objArr[3] = browserInfo.versionName;
        return String.format(locale, "%s-%s-%s::%s", objArr);
    }
}