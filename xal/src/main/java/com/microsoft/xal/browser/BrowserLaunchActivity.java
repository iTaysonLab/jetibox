package com.microsoft.xal.browser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.browser.customtabs.CustomTabsIntent;

import com.microsoft.xal.logging.XalLogger;

/* loaded from: classes2.dex */
public class BrowserLaunchActivity extends Activity {
    private static final String BROWSER_INFO_STATE_KEY = "BROWSER_INFO_STATE";
    private static final String CUSTOM_TABS_IN_PROGRESS_STATE_KEY = "CUSTOM_TABS_IN_PROGRESS_STATE";
    public static final String END_URL = "END_URL";
    public static final String IN_PROC_BROWSER = "IN_PROC_BROWSER";
    public static final String OPERATION_ID = "OPERATION_ID";
    private static final String OPERATION_ID_STATE_KEY = "OPERATION_ID_STATE";
    public static final String REQUEST_HEADER_KEYS = "REQUEST_HEADER_KEYS";
    public static final String REQUEST_HEADER_VALUES = "REQUEST_HEADER_VALUES";
    public static final int RESULT_FAILED = 8052;
    private static final String SHARED_BROWSER_USED_STATE_KEY = "SHARED_BROWSER_USED_STATE";
    public static final String SHOW_TYPE = "SHOW_TYPE";
    public static final String START_URL = "START_URL";
    public static final int WEB_KIT_WEB_VIEW_REQUEST = 8053;
    private final XalLogger m_logger = new XalLogger("BrowserLaunchActivity");
    private BrowserLaunchParameters m_launchParameters = null;
    private long m_operationId = 0;
    private boolean m_customTabsInProgress = false;
    private boolean m_sharedBrowserUsed = false;
    private String m_browserInfo = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.microsoft.xal.browser.BrowserLaunchActivity$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xal$browser$BrowserLaunchActivity$ShowUrlType;
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xal$browser$BrowserLaunchActivity$WebResult;

        static {
            int[] iArr = new int[WebResult.values().length];
            $SwitchMap$com$microsoft$xal$browser$BrowserLaunchActivity$WebResult = iArr;
            try {
                iArr[WebResult.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$microsoft$xal$browser$BrowserLaunchActivity$WebResult[WebResult.CANCEL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$microsoft$xal$browser$BrowserLaunchActivity$WebResult[WebResult.FAIL.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            int[] iArr2 = new int[ShowUrlType.values().length];
            $SwitchMap$com$microsoft$xal$browser$BrowserLaunchActivity$ShowUrlType = iArr2;
            try {
                iArr2[ShowUrlType.Normal.ordinal()] = 1;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$microsoft$xal$browser$BrowserLaunchActivity$ShowUrlType[ShowUrlType.CookieRemoval_DEPRECATED.ordinal()] = 2;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$microsoft$xal$browser$BrowserLaunchActivity$ShowUrlType[ShowUrlType.CookieRemovalSkipIfSharedCredentials.ordinal()] = 3;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$microsoft$xal$browser$BrowserLaunchActivity$ShowUrlType[ShowUrlType.NonAuthFlow.ordinal()] = 4;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class BrowserLaunchParameters {
        public final String EndUrl;
        public final String[] RequestHeaderKeys;
        public final String[] RequestHeaderValues;
        public final ShowUrlType ShowType;
        public final String StartUrl;
        public boolean UseInProcBrowser;

        private BrowserLaunchParameters(String str, String str2, String[] strArr, String[] strArr2, ShowUrlType showUrlType, boolean z) {
            XalLogger xalLogger = new XalLogger("BrowserLaunchActivity.BrowserLaunchParameters");
            try {
                this.StartUrl = str;
                this.EndUrl = str2;
                this.RequestHeaderKeys = strArr;
                this.RequestHeaderValues = strArr2;
                this.ShowType = showUrlType;
                if (showUrlType == ShowUrlType.NonAuthFlow) {
                    xalLogger.Important("BrowserLaunchParameters() Forcing inProc browser because flow is marked non-auth.");
                } else {
                    if (strArr.length > 0) {
                        xalLogger.Important("BrowserLaunchParameters() Forcing inProc browser because request headers were found.");
                    }
                    this.UseInProcBrowser = z;
                    xalLogger.close();
                }
                z = true;
                this.UseInProcBrowser = z;
                xalLogger.close();
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

        public static BrowserLaunchParameters FromArgs(Bundle bundle) {
            String string = bundle.getString("START_URL");
            String string2 = bundle.getString("END_URL");
            String[] stringArray = bundle.getStringArray("REQUEST_HEADER_KEYS");
            String[] stringArray2 = bundle.getStringArray("REQUEST_HEADER_VALUES");
            ShowUrlType showUrlType = (ShowUrlType) bundle.get("SHOW_TYPE");
            boolean z = bundle.getBoolean(BrowserLaunchActivity.IN_PROC_BROWSER);
            if (string == null || string2 == null || stringArray == null || stringArray2 == null || stringArray.length != stringArray2.length) {
                return null;
            }
            return new BrowserLaunchParameters(string, string2, stringArray, stringArray2, showUrlType, z);
        }
    }

    /* loaded from: classes2.dex */
    public enum ShowUrlType {
        Normal,
        CookieRemoval_DEPRECATED,
        CookieRemovalSkipIfSharedCredentials,
        NonAuthFlow;

        public static ShowUrlType fromInt(int i2) {
            XalLogger xalLogger = new XalLogger("BrowserLaunchActivity.ShowUrlType");
            try {
                if (i2 == 0) {
                    ShowUrlType showUrlType = Normal;
                    xalLogger.close();
                    return showUrlType;
                } else if (i2 == 1) {
                    xalLogger.Warning("Encountered unexpected show type, mapped to deprecated case CookieRemoval_DEPRECATED.");
                    ShowUrlType showUrlType2 = CookieRemoval_DEPRECATED;
                    xalLogger.close();
                    return showUrlType2;
                } else if (i2 == 2) {
                    ShowUrlType showUrlType3 = CookieRemovalSkipIfSharedCredentials;
                    xalLogger.close();
                    return showUrlType3;
                } else if (i2 != 3) {
                    xalLogger.Warning("Encountered unexpected show type int value: " + i2);
                    xalLogger.close();
                    return null;
                } else {
                    ShowUrlType showUrlType4 = NonAuthFlow;
                    xalLogger.close();
                    return showUrlType4;
                }
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

        @Override // java.lang.Enum
        public String toString() {
            int i2 = AnonymousClass1.$SwitchMap$com$microsoft$xal$browser$BrowserLaunchActivity$ShowUrlType[ordinal()];
            return i2 != 1 ? i2 != 2 ? i2 != 3 ? i2 != 4 ? "Unknown" : "NonAuthFlow" : "CookieRemovalSkipIfSharedCredentials" : "CookieRemoval_DEPRECATED" : "Normal";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum WebResult {
        SUCCESS,
        FAIL,
        CANCEL
    }

    private static native void checkIsLoaded();

    private boolean checkNativeCodeLoaded() {
        try {
            checkIsLoaded();
            return true;
        } catch (UnsatisfiedLinkError unused) {
            this.m_logger.Error("checkNativeCodeLoaded() Caught UnsatisfiedLinkError, native code not loaded");
            return false;
        }
    }

    private void finishOperation(WebResult webResult, String str) {
        long j2 = this.m_operationId;
        this.m_operationId = 0L;
        finish();
        if (j2 == 0) {
            this.m_logger.Error("finishOperation() No operation ID to complete.");
            this.m_logger.Flush();
            return;
        }
        this.m_logger.Flush();
        int i2 = AnonymousClass1.$SwitchMap$com$microsoft$xal$browser$BrowserLaunchActivity$WebResult[webResult.ordinal()];
        if (i2 == 1) {
            urlOperationSucceeded(j2, str, this.m_sharedBrowserUsed, this.m_browserInfo);
        } else if (i2 == 2) {
            urlOperationCanceled(j2, this.m_sharedBrowserUsed, this.m_browserInfo);
        } else if (i2 != 3) {
        } else {
            urlOperationFailed(j2, this.m_sharedBrowserUsed, this.m_browserInfo);
        }
    }

    public static void showUrl(long j2, Context context, String str, String str2, int i2, String[] strArr, String[] strArr2, boolean z) {
        XalLogger xalLogger = new XalLogger("BrowserLaunchActivity.showUrl()");
        try {
            xalLogger.Important("JNI call received.");
            if (!str.isEmpty() && !str2.isEmpty()) {
                ShowUrlType fromInt = ShowUrlType.fromInt(i2);
                if (fromInt == null) {
                    xalLogger.Error("Unrecognized show type received: " + i2);
                    urlOperationFailed(j2, false, null);
                    xalLogger.close();
                    return;
                } else if (strArr.length != strArr2.length) {
                    xalLogger.Error("requestHeaderKeys different length than requestHeaderValues.");
                    urlOperationFailed(j2, false, null);
                    xalLogger.close();
                    return;
                } else {
                    Intent intent = new Intent(context, BrowserLaunchActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putLong(OPERATION_ID, j2);
                    bundle.putString("START_URL", str);
                    bundle.putString("END_URL", str2);
                    bundle.putSerializable("SHOW_TYPE", fromInt);
                    bundle.putStringArray("REQUEST_HEADER_KEYS", strArr);
                    bundle.putStringArray("REQUEST_HEADER_VALUES", strArr2);
                    bundle.putBoolean(IN_PROC_BROWSER, z);
                    intent.putExtras(bundle);
                    intent.setFlags(268435456);
                    context.startActivity(intent);
                    xalLogger.close();
                    return;
                }
            }
            xalLogger.Error("Received invalid start or end URL.");
            urlOperationFailed(j2, false, null);
            xalLogger.close();
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

    private void startAuthSession(BrowserLaunchParameters browserLaunchParameters) {
        BrowserSelectionResult selectBrowser = BrowserSelector.selectBrowser(getApplicationContext(), browserLaunchParameters.UseInProcBrowser);
        this.m_browserInfo = selectBrowser.toString();
        XalLogger xalLogger = this.m_logger;
        xalLogger.Important("startAuthSession() Set browser info: " + this.m_browserInfo);
        XalLogger xalLogger2 = this.m_logger;
        xalLogger2.Important("startAuthSession() Starting auth session for ShowUrlType: " + browserLaunchParameters.ShowType.toString());
        String packageName = selectBrowser.packageName();
        if (packageName == null) {
            this.m_logger.Important("startAuthSession() BrowserSelector returned null package name. Choosing WebKit strategy.");
            startWebView(browserLaunchParameters.StartUrl, browserLaunchParameters.EndUrl, browserLaunchParameters.ShowType, browserLaunchParameters.RequestHeaderKeys, browserLaunchParameters.RequestHeaderValues);
            return;
        }
        this.m_logger.Important("startAuthSession() BrowserSelector returned non-null package name. Choosing CustomTabs strategy.");
        startCustomTabsInBrowser(packageName, browserLaunchParameters.StartUrl, browserLaunchParameters.EndUrl, browserLaunchParameters.ShowType);
    }

    private void startCustomTabsInBrowser(String str, String str2, String str3, ShowUrlType showUrlType) {
        if (showUrlType == ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            finishOperation(WebResult.SUCCESS, str3);
            return;
        }
        this.m_customTabsInProgress = true;
        this.m_sharedBrowserUsed = true;
        CustomTabsIntent.Builder aVar = new CustomTabsIntent.Builder();
        aVar.setShowTitle(true);
        CustomTabsIntent b2 = aVar.build();
        b2.intent.setData(Uri.parse(str2));
        b2.intent.setPackage(str);
        startActivity(b2.intent);
    }

    private void startWebView(String str, String str2, ShowUrlType showUrlType, String[] strArr, String[] strArr2) {
        this.m_sharedBrowserUsed = false;
        Intent intent = new Intent(getApplicationContext(), WebKitWebViewController.class);
        Bundle bundle = new Bundle();
        bundle.putString("START_URL", str);
        bundle.putString("END_URL", str2);
        bundle.putSerializable("SHOW_TYPE", showUrlType);
        bundle.putStringArray("REQUEST_HEADER_KEYS", strArr);
        bundle.putStringArray("REQUEST_HEADER_VALUES", strArr2);
        intent.putExtras(bundle);
        startActivityForResult(intent, WEB_KIT_WEB_VIEW_REQUEST);
    }

    private static native void urlOperationCanceled(long j2, boolean z, String str);

    private static native void urlOperationFailed(long j2, boolean z, String str);

    private static native void urlOperationSucceeded(long j2, String str, boolean z, String str2);

    @Override // android.app.Activity
    public void onActivityResult(int i2, int i3, Intent intent) {
        this.m_logger.Important("onActivityResult() Result received.");
        if (i2 == 8053) {
            if (i3 == -1) {
                String string = intent.getExtras().getString(WebKitWebViewController.RESPONSE_KEY, "");
                if (string.isEmpty()) {
                    this.m_logger.Error("onActivityResult() Invalid final URL received from web view.");
                } else {
                    finishOperation(WebResult.SUCCESS, string);
                    return;
                }
            } else if (i3 == 0) {
                finishOperation(WebResult.CANCEL, null);
                return;
            } else if (i3 != 8054) {
                XalLogger xalLogger = this.m_logger;
                xalLogger.Warning("onActivityResult() Unrecognized result code received from web view:" + i3);
            }
            finishOperation(WebResult.FAIL, null);
            return;
        }
        this.m_logger.Warning("onActivityResult() Result received from unrecognized request.");
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.m_logger.Important("onCreate()");
        Bundle extras = getIntent().getExtras();
        if (!checkNativeCodeLoaded()) {
            this.m_logger.Warning("onCreate() Called while XAL not loaded. Dropping flow and starting app's main activity.");
            this.m_logger.Flush();
            startActivity(getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName()));
            finish();
        } else if (bundle != null) {
            this.m_logger.Important("onCreate() Recreating with saved state.");
            this.m_operationId = bundle.getLong(OPERATION_ID_STATE_KEY);
            this.m_customTabsInProgress = bundle.getBoolean(CUSTOM_TABS_IN_PROGRESS_STATE_KEY);
            this.m_sharedBrowserUsed = bundle.getBoolean(SHARED_BROWSER_USED_STATE_KEY);
            this.m_browserInfo = bundle.getString(BROWSER_INFO_STATE_KEY);
        } else if (extras != null) {
            this.m_logger.Important("onCreate() Created with intent args. Starting auth session.");
            this.m_operationId = extras.getLong(OPERATION_ID, 0L);
            BrowserLaunchParameters FromArgs = BrowserLaunchParameters.FromArgs(extras);
            this.m_launchParameters = FromArgs;
            if (FromArgs != null && this.m_operationId != 0) {
                return;
            }
            this.m_logger.Error("onCreate() Found invalid args, failing operation.");
            finishOperation(WebResult.FAIL, null);
        } else if (getIntent().getData() != null) {
            this.m_logger.Error("onCreate() Unexpectedly created with intent data. Finishing with failure.");
            setResult(RESULT_FAILED);
            finishOperation(WebResult.FAIL, null);
        } else {
            this.m_logger.Error("onCreate() Unexpectedly created, reason unknown. Finishing with failure.");
            setResult(RESULT_FAILED);
            finishOperation(WebResult.FAIL, null);
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.m_logger.Important("onDestroy()");
        if (!isFinishing() || this.m_operationId == 0) {
            return;
        }
        this.m_logger.Warning("onDestroy() Activity is finishing with operation in progress, canceling.");
        finishOperation(WebResult.CANCEL, null);
    }

    @Override // android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.m_logger.Important("onNewIntent() Received intent.");
        setIntent(intent);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        this.m_logger.Important("onResume()");
        boolean z = this.m_customTabsInProgress;
        if (!z && this.m_launchParameters != null) {
            this.m_logger.Important("onResume() Resumed with launch parameters. Starting auth session.");
            BrowserLaunchParameters browserLaunchParameters = this.m_launchParameters;
            this.m_launchParameters = null;
            startAuthSession(browserLaunchParameters);
        } else if (z) {
            this.m_customTabsInProgress = false;
            Uri data = getIntent().getData();
            if (data != null) {
                this.m_logger.Important("onResume() Resumed with intent data. Finishing operation successfully.");
                finishOperation(WebResult.SUCCESS, data.toString());
                return;
            }
            this.m_logger.Warning("onResume() Resumed with no intent data. Canceling operation.");
            finishOperation(WebResult.CANCEL, null);
        } else {
            this.m_logger.Warning("onResume() No action to take. This shouldn't happen.");
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.m_logger.Important("onSaveInstanceState() Preserving state.");
        bundle.putLong(OPERATION_ID_STATE_KEY, this.m_operationId);
        bundle.putBoolean(CUSTOM_TABS_IN_PROGRESS_STATE_KEY, this.m_customTabsInProgress);
        bundle.putBoolean(SHARED_BROWSER_USED_STATE_KEY, this.m_sharedBrowserUsed);
        bundle.putString(BROWSER_INFO_STATE_KEY, this.m_browserInfo);
    }
}