package com.microsoft.xalwrapper;

import android.content.Context;

import com.google.auto.value.AutoValue;
import com.microsoft.xalwrapper.models.LCEObservable;
import com.microsoft.xalwrapper.models.LogEvent;
import com.microsoft.xalwrapper.models.XalPrivilegeCheckDenyReason;
import com.microsoft.xalwrapper.models.XalUser;
import com.microsoft.xalwrapper.models.XalWebAccountParameter;
import java.util.ArrayList;
import java.util.Iterator;

public class XalApplication {
    private static final int EVENT_LOG_BUFFER_MAX_SIZE = 100;
    private static final int E_XAL_UIREQUIRED = -1994174200;
    private static final int E_XAL_USERSIGNEDOUT = -1994174204;
    private static XalApplication INSTANCE = null;
    private static final String TAG = "XalApplication";
    private String m_localStoragePath;
    private boolean m_isInitialized = false;
    private int m_xalLogLevel = LogLevel.OFF.getValue();
    private int m_maxLogBufferSize = 100;
    private final LCEObservable<XalInitializeContent> m_xalInitializeOperation = new LCEObservable<>();
    private final LCEObservable<XalUser> m_addUserOperation = new LCEObservable<>();
    private IXalLogger m_logger = null;
    private final ArrayList<LogEvent> m_eventLogBuffer = new ArrayList<>();

    public enum LogLevel {
        OFF(0),
        ERROR(1),
        WARNING(2),
        IMPORTANT(3),
        INFORMATION(4),
        VERBOSE(5);
        
        private final int value;

        LogLevel(int i2) {
            this.value = i2;
        }

        public int getValue() {
            return this.value;
        }
    }

    public interface XalGetMsaForAdditionalScopeCallback {
        void onError(int i2, String str);

        void onSuccess(String str);
    }

    public interface XalGetTokenCallback {
        void onError(int i2, String str);

        void onSuccess(String str, String str2);
    }

    public interface XalInitializeCallback {
        void onError(int i2, String str);

        void onSuccess(String str);
    }

    @AutoValue
    public static abstract class XalInitializeContent {
        public static XalInitializeContent with(String str) {
            return new AutoValue_XalApplication_XalInitializeContent(str);
        }

        public abstract String baseCorrelationVector();
    }

    public interface XalRefreshTokenCallback {
        void onCompleted(int i2, String str);
    }

    public interface XalSignInCallback {
        void onError(int i2, String str);

        void onSuccess(long xuid, String gamertag, String uniqueModernGamertag, int ageGroup, String webAccountId);
    }

    public interface XalSignOutCallback {
        void onCompleted(int i2, String str);
    }

    static {
        System.loadLibrary("xal_jni");
    }

    private XalApplication() {
    }

    private native void AddFirstUserSilent(XalSignInCallback xalSignInCallback);

    private native void AddFirstUserWithUi(XalSignInCallback xalSignInCallback);

    private synchronized void AddLogToBuffer(LogEvent logEvent) {
        this.m_eventLogBuffer.add(logEvent);
    }

    private native int CheckUserPrivilege(int i2);

    private native void CleanupXal();

    private void EmitLog(LogEvent logEvent) {
        this.m_logger.TraceMessage(logEvent.areaName(), logEvent.traceLevel(), logEvent.message());
    }

    private native void GetAnonymousTokenAndSignature(String str, boolean z, XalGetTokenCallback xalGetTokenCallback);

    private native void GetMsaForAdditionalScope(String str, XalWebAccountParameter[] xalWebAccountParameterArr, boolean z, XalGetMsaForAdditionalScopeCallback xalGetMsaForAdditionalScopeCallback);

    private native void GetTokenAndSignature(String str, boolean z, XalGetTokenCallback xalGetTokenCallback);

    private native void InitializeApp(Context context);

    private native void InitializeXal(boolean z, boolean z2, boolean z3, boolean z4, long j2, String str, String str2, String str3, int i2, String str4, XalInitializeCallback xalInitializeCallback);

    private native void ResolveTokenIssue(String str, XalRefreshTokenCallback xalRefreshTokenCallback);

    private boolean ShouldIncludeXalTraceMessage(int i2) {
        return i2 <= this.m_xalLogLevel;
    }

    private native void SignOutUser(XalSignOutCallback xalSignOutCallback);

    private void XalAddUserWithUiInternal(final XalSignInCallback xalSignInCallback) {
        this.m_addUserOperation.setLoadingState();
        DebugLogger.Log(TAG, "Calling XalAddUserWithUi");
        AddFirstUserWithUi(new XalSignInCallback() {
            @Override // com.microsoft.xalwrapper.XalApplication.XalSignInCallback
            public void onError(int i2, String str) {
                XalApplication.this.m_addUserOperation.setErrorState(i2, str);
                DebugLogger.Log(XalApplication.TAG, "XalAddUserWithUi onError: " + XalApplication.this.m_addUserOperation.getErrorState().toString());
                xalSignInCallback.onError(i2, str);
            }

            @Override // com.microsoft.xalwrapper.XalApplication.XalSignInCallback
            public void onSuccess(long xuid, String gamertag, String uniqueModernGamertag, int ageGroup, String webAccountId) {
                XalUser withContent = XalUser.withContent(xuid, ageGroup, gamertag, uniqueModernGamertag, webAccountId);
                DebugLogger.Log(XalApplication.TAG, "XalAddUserWithUi onSuccess: " + withContent);
                XalApplication.this.m_addUserOperation.setContentState(withContent);
                xalSignInCallback.onSuccess(xuid, gamertag, uniqueModernGamertag, ageGroup, webAccountId);
            }
        });
    }

    public static XalApplication getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new XalApplication();
        }
        return INSTANCE;
    }

    private boolean isSignedIn() {
        return this.m_addUserOperation.isContent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: lambda$XalAddUserWithUi$2 */
    public /* synthetic */ void a(XalSignInCallback xalSignInCallback) {
        if (this.m_addUserOperation.isContent()) {
            XalUser content = this.m_addUserOperation.getContent();
            xalSignInCallback.onSuccess(content.xuid(), content.gamertag(), content.uniqueModernGamertag(), content.ageGroup(), content.webAccountId());
        } else if (!this.m_addUserOperation.isError() || this.m_addUserOperation.getErrorState() == null) {
        } else {
            if (this.m_addUserOperation.getErrorState().errorCode() == E_XAL_UIREQUIRED) {
                DebugLogger.Log(TAG, "XalAddUserWithUi got UIRequired due to a pending silent sign in, retrying UI operation");
                XalAddUserWithUiInternal(xalSignInCallback);
                return;
            }
            xalSignInCallback.onError(this.m_addUserOperation.getErrorState().errorCode(), this.m_addUserOperation.getErrorState().errorMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: lambda$XalInitialize$0 */
    public /* synthetic */ void b(XalInitializeCallback xalInitializeCallback) {
        if (this.m_xalInitializeOperation.isContent() && this.m_xalInitializeOperation.getContent() != null) {
            xalInitializeCallback.onSuccess(this.m_xalInitializeOperation.getContent().baseCorrelationVector());
        } else if (!this.m_xalInitializeOperation.isError() || this.m_xalInitializeOperation.getErrorState() == null) {
        } else {
            xalInitializeCallback.onError(this.m_xalInitializeOperation.getErrorState().errorCode(), this.m_xalInitializeOperation.getErrorState().errorMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: lambda$XalSignOutUser$3 */
    public /* synthetic */ void c(XalSignOutCallback xalSignOutCallback, int i2, String str) {
        this.m_addUserOperation.setNotRequestedState();
        DebugLogger.Log(TAG, "XalSignOutUser complete");
        xalSignOutCallback.onCompleted(i2, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: lambda$XalTryAddFirstUserSilently$1 */
    public /* synthetic */ void d(XalSignInCallback xalSignInCallback) {
        if (this.m_addUserOperation.isContent() && this.m_addUserOperation.getContent() != null) {
            XalUser content = this.m_addUserOperation.getContent();
            xalSignInCallback.onSuccess(content.xuid(), content.gamertag(), content.uniqueModernGamertag(), content.ageGroup(), content.webAccountId());
        } else if (!this.m_addUserOperation.isError() || this.m_addUserOperation.getErrorState() == null) {
        } else {
            xalSignInCallback.onError(this.m_addUserOperation.getErrorState().errorCode(), this.m_addUserOperation.getErrorState().errorMessage());
        }
    }

    public String GetLocalStoragePath() {
        return this.m_localStoragePath;
    }

    public boolean HasLogger() {
        return this.m_logger != null;
    }

    public synchronized void Initialize(Context context, String str) {
        String str2 = TAG;
        DebugLogger.Log(str2, "Initialize");
        this.m_localStoragePath = str;
        if (!this.m_isInitialized) {
            InitializeApp(context);
            DebugLogger.Log(str2, "Initialize complete");
            this.m_isInitialized = true;
        }
    }

    public void SetLogBufferSize(int i2) {
        DebugLogger.Log(TAG, String.format("SetLogLevel: %d", Integer.valueOf(i2)));
        this.m_maxLogBufferSize = i2;
    }

    public synchronized void SetLogger(IXalLogger iXalLogger) {
        DebugLogger.Log(TAG, "SetLogger");
        this.m_logger = iXalLogger;
        Iterator<LogEvent> it = this.m_eventLogBuffer.iterator();
        while (it.hasNext()) {
            EmitLog(it.next());
        }
        this.m_eventLogBuffer.clear();
    }

    public void TraceMessage(String str, int i2, long j2, long j3, String str2) {
        if (ShouldIncludeXalTraceMessage(i2)) {
            LogEvent with = LogEvent.with(str, str2, i2, j2, j3);
            DebugLogger.Log(TAG, with.toString());
            if (this.m_logger != null) {
                EmitLog(with);
            } else if (this.m_eventLogBuffer.size() >= this.m_maxLogBufferSize) {
            } else {
                AddLogToBuffer(with);
            }
        }
    }

    public synchronized void XalAddUserWithUi(final XalSignInCallback xalSignInCallback) {
        DebugLogger.Log(TAG, "XalAddUserWithUi");
        if (!this.m_addUserOperation.isNotRequested() && !this.m_addUserOperation.isError()) {
            this.m_addUserOperation.registerListener(new Runnable() { // from class: com.microsoft.xalwrapper.c
                @Override // java.lang.Runnable
                public final void run() {
                    XalApplication.this.a(xalSignInCallback);
                }
            });
        }
        XalAddUserWithUiInternal(xalSignInCallback);
    }

    public XalPrivilegeCheckDenyReason XalCheckUserPrivilege(int i2) {
        return XalPrivilegeCheckDenyReason.valueOf(CheckUserPrivilege(i2));
    }

    public void XalCleanup() {
        DebugLogger.Log(TAG, "XalCleanup");
        if (this.m_xalInitializeOperation.isContent()) {
            CleanupXal();
            this.m_xalInitializeOperation.setNotRequestedState();
            this.m_addUserOperation.setNotRequestedState();
        }
    }

    public void XalGetAnonymousTokenAndSignatureSilently(String str, boolean z, XalGetTokenCallback xalGetTokenCallback) {
        DebugLogger.Log(TAG, "XalGetAnonymousTokenAndSignatureSilently");
        GetAnonymousTokenAndSignature(str, z, xalGetTokenCallback);
    }

    public void XalGetMsaTicketForAdditionalScope(String str, XalWebAccountParameter[] xalWebAccountParameterArr, XalGetMsaForAdditionalScopeCallback xalGetMsaForAdditionalScopeCallback) {
        DebugLogger.Log(TAG, "XalGetMsaTicketForAdditionalScope");
        if (isSignedIn()) {
            GetMsaForAdditionalScope(str, xalWebAccountParameterArr, true, xalGetMsaForAdditionalScopeCallback);
        } else {
            xalGetMsaForAdditionalScopeCallback.onError(E_XAL_USERSIGNEDOUT, "Cannot get msa ticket for a signed out user.");
        }
    }

    public synchronized void XalInitialize(boolean z, boolean z2, boolean z3, boolean z4, long j2, String str, String str2, String str3, int i2, int i3, String str4, final XalInitializeCallback xalInitializeCallback) {
        String str5 = TAG;
        DebugLogger.Log(str5, "XalInitialize");
        this.m_xalLogLevel = i3;
        if (this.m_xalInitializeOperation.isNotRequested()) {
            this.m_xalInitializeOperation.setLoadingState();
            DebugLogger.Log(str5, "Calling InitializeXal");
            InitializeXal(z, z2, z3, z4, j2, str, str2, str3, i2, str4, new XalInitializeCallback() { // from class: com.microsoft.xalwrapper.XalApplication.1
                @Override // com.microsoft.xalwrapper.XalApplication.XalInitializeCallback
                public void onError(int i4, String str6) {
                    XalApplication.this.m_xalInitializeOperation.setErrorState(i4, str6);
                    String str7 = XalApplication.TAG;
                    DebugLogger.Log(str7, "InitializeXal onError: " + XalApplication.this.m_xalInitializeOperation.getErrorState().toString());
                    xalInitializeCallback.onError(i4, str6);
                }

                @Override // com.microsoft.xalwrapper.XalApplication.XalInitializeCallback
                public void onSuccess(String str6) {
                    DebugLogger.Log(XalApplication.TAG, "InitializeXal onSuccess");
                    XalApplication.this.m_xalInitializeOperation.setContentState(XalInitializeContent.with(str6));
                    xalInitializeCallback.onSuccess(str6);
                }
            });
        } else {
            this.m_xalInitializeOperation.registerListener(() -> XalApplication.this.b(xalInitializeCallback));
        }
    }

    public void XalSignOutUser(final XalSignOutCallback xalSignOutCallback) {
        DebugLogger.Log(TAG, "XalSignOutUser");
        if (isSignedIn()) {
            SignOutUser(new XalSignOutCallback() { // from class: com.microsoft.xalwrapper.a
                @Override // com.microsoft.xalwrapper.XalApplication.XalSignOutCallback
                public final void onCompleted(int i2, String str) {
                    XalApplication.this.c(xalSignOutCallback, i2, str);
                }
            });
        } else {
            xalSignOutCallback.onCompleted(E_XAL_USERSIGNEDOUT, "Cannot sign out a user that is already signed out.");
        }
    }

    public synchronized void XalTryAddFirstUserSilently(final XalSignInCallback xalSignInCallback) {
        String str = TAG;
        DebugLogger.Log(str, "XalTryAddFirstUserSilently");
        if (!this.m_addUserOperation.isNotRequested() && !this.m_addUserOperation.isError()) {
            this.m_addUserOperation.registerListener(new Runnable() { // from class: com.microsoft.xalwrapper.b
                @Override // java.lang.Runnable
                public final void run() {
                    XalApplication.this.d(xalSignInCallback);
                }
            });
        }
        this.m_addUserOperation.setLoadingState();
        DebugLogger.Log(str, "Calling AddFirstUserSilent");
        AddFirstUserSilent(new XalSignInCallback() { // from class: com.microsoft.xalwrapper.XalApplication.2
            @Override // com.microsoft.xalwrapper.XalApplication.XalSignInCallback
            public void onError(int i2, String str2) {
                XalApplication.this.m_addUserOperation.setErrorState(i2, str2);
                DebugLogger.Log(XalApplication.TAG, "XalTryAddFirstUserSilently onError: " + XalApplication.this.m_addUserOperation.getErrorState().toString());
                xalSignInCallback.onError(i2, str2);
            }

            @Override // com.microsoft.xalwrapper.XalApplication.XalSignInCallback
            public void onSuccess(long xuid, String str2, String str3, int ageGroup, String str4) {
                XalUser withContent = XalUser.withContent(xuid, ageGroup, str2, str3, str4);
                DebugLogger.Log(XalApplication.TAG, "XalTryAddFirstUserSilently onSuccess: " + withContent);
                XalApplication.this.m_addUserOperation.setContentState(withContent);
                xalSignInCallback.onSuccess(xuid, str2, str3, ageGroup, str4);
            }
        });
    }

    public void XalUserGetTokenAndSignatureSilently(String str, boolean z, XalGetTokenCallback xalGetTokenCallback) {
        DebugLogger.Log(TAG, "XalUserGetTokenAndSignatureSilently");
        if (isSignedIn()) {
            GetTokenAndSignature(str, z, xalGetTokenCallback);
        } else {
            xalGetTokenCallback.onError(E_XAL_USERSIGNEDOUT, "Cannot get token and signature for signed out user.");
        }
    }

    public void XalUserGetWebAccountTokenWithUiAsync(String str, XalWebAccountParameter[] xalWebAccountParameterArr, XalGetMsaForAdditionalScopeCallback xalGetMsaForAdditionalScopeCallback) {
        DebugLogger.Log(TAG, "XalUserGetWebAccountTokenWithUiAsync");
        if (isSignedIn()) {
            GetMsaForAdditionalScope(str, xalWebAccountParameterArr, false, xalGetMsaForAdditionalScopeCallback);
        } else {
            xalGetMsaForAdditionalScopeCallback.onError(E_XAL_USERSIGNEDOUT, "Cannot get msa ticket for a signed out user.");
        }
    }

    public void XalUserResolveIssueWithUiAsync(String str, XalRefreshTokenCallback xalRefreshTokenCallback) {
        DebugLogger.Log(TAG, "XalUserResolveIssueWithUiAsync");
        ResolveTokenIssue(str, xalRefreshTokenCallback);
    }
}