package com.microsoft.xal.browser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.microsoft.xal.browser.BrowserLaunchActivity;
import com.microsoft.xal.logging.XalLogger;
import java.util.HashMap;

public class WebKitWebViewController extends Activity {
    public static final String END_URL = "END_URL";
    public static final String REQUEST_HEADER_KEYS = "REQUEST_HEADER_KEYS";
    public static final String REQUEST_HEADER_VALUES = "REQUEST_HEADER_VALUES";
    public static final String RESPONSE_KEY = "RESPONSE";
    public static final int RESULT_FAILED = 8054;
    public static final String SHOW_TYPE = "SHOW_TYPE";
    public static final String START_URL = "START_URL";
    private final XalLogger m_logger = new XalLogger("WebKitWebViewController");

    private void deleteCookies(String str, boolean z) {
        CookieManager cookieManager = CookieManager.getInstance();
        String sb2 = (z ? "https://" : "http://") + str;
        String cookie = cookieManager.getCookie(sb2);
        boolean z2 = false;
        if (cookie != null) {
            String[] split = cookie.split(";");
            for (String str2 : split) {
                String trim = str2.split("=")[0].trim();
                String str3 = trim + "=;";
                if (trim.startsWith("__Secure-")) {
                    str3 = str3 + "Secure;Domain=" + str + ";Path=/";
                }
                cookieManager.setCookie(sb2, trim.startsWith("__Host-") ? str3 + "Secure;Path=/" : str3 + "Domain=" + str + ";Path=/");
            }
            if (split.length > 0) {
                z2 = true;
            }
        }
        if (z2) {
            this.m_logger.Information("deleteCookies() Deleted cookies for " + str);
        } else {
            this.m_logger.Information("deleteCookies() Found no cookies for " + str);
        }
        cookieManager.flush();
    }

    @Override // android.app.Activity
    @SuppressLint({"SetJavaScriptEnabled"})
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            this.m_logger.Error("onCreate() Called with no extras.");
            this.m_logger.Flush();
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        String string = extras.getString(START_URL, "");
        final String string2 = extras.getString(END_URL, "");
        if (!string.isEmpty() && !string2.isEmpty()) {
            String[] stringArray = extras.getStringArray(REQUEST_HEADER_KEYS);
            String[] stringArray2 = extras.getStringArray(REQUEST_HEADER_VALUES);
            if (stringArray.length != stringArray2.length) {
                this.m_logger.Error("onCreate() Received request header and key arrays of different lengths.");
                this.m_logger.Flush();
                setResult(RESULT_FAILED);
                finish();
                return;
            }
            BrowserLaunchActivity.ShowUrlType showUrlType = (BrowserLaunchActivity.ShowUrlType) extras.get(SHOW_TYPE);
            if (showUrlType != BrowserLaunchActivity.ShowUrlType.CookieRemoval_DEPRECATED && showUrlType != BrowserLaunchActivity.ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
                HashMap<String, String> hashMap = new HashMap<>(stringArray.length);
                for (int i2 = 0; i2 < stringArray.length; i2++) {
                    if (stringArray[i2] != null && !stringArray[i2].isEmpty() && stringArray2[i2] != null && !stringArray2[i2].isEmpty()) {
                        hashMap.put(stringArray[i2], stringArray2[i2]);
                    } else {
                        this.m_logger.Error("onCreate() Received null or empty request field.");
                        this.m_logger.Flush();
                        setResult(RESULT_FAILED);
                        finish();
                        return;
                    }
                }
                WebView webView = new WebView(this);
                setContentView(webView);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebChromeClient(new WebChromeClient() { // from class: com.microsoft.xal.browser.WebKitWebViewController.1
                    @Override // android.webkit.WebChromeClient
                    public void onProgressChanged(WebView webView2, int i3) {
                        WebKitWebViewController.this.setProgress(i3 * 100);
                    }
                });
                webView.setWebViewClient(new WebViewClient() { // from class: com.microsoft.xal.browser.WebKitWebViewController.2
                    @Override // android.webkit.WebViewClient
                    public void onPageFinished(WebView webView2, String str) {
                        super.onPageFinished(webView2, str);
                        webView2.requestFocus(130);
                        webView2.sendAccessibilityEvent(8);
                        webView2.evaluateJavascript("if (typeof window.__xal__performAccessibilityFocus === \"function\") { window.__xal__performAccessibilityFocus(); }", null);
                    }

                    @Override // android.webkit.WebViewClient
                    public boolean shouldOverrideUrlLoading(WebView webView2, String str) {
                        if (str.startsWith(string2)) {
                            WebKitWebViewController.this.m_logger.Important("WebKitWebViewController found end URL. Ending UI flow.");
                            WebKitWebViewController.this.m_logger.Flush();
                            Intent intent = new Intent();
                            intent.putExtra(WebKitWebViewController.RESPONSE_KEY, str);
                            WebKitWebViewController.this.setResult(-1, intent);
                            WebKitWebViewController.this.finish();
                            return true;
                        }
                        return false;
                    }
                });
                webView.loadUrl(string, hashMap);
                return;
            }
            this.m_logger.Important("onCreate() WebView invoked for cookie removal. Deleting cookies and finishing.");
            if (stringArray.length > 0) {
                this.m_logger.Warning("onCreate() WebView invoked for cookie removal with requestHeaders.");
            }
            deleteCookies("login.live.com", true);
            deleteCookies("account.live.com", true);
            deleteCookies("live.com", true);
            deleteCookies("xboxlive.com", true);
            deleteCookies("sisu.xboxlive.com", true);
            this.m_logger.Flush();
            Intent intent = new Intent();
            intent.putExtra(RESPONSE_KEY, string2);
            setResult(-1, intent);
            finish();
            return;
        }
        this.m_logger.Error("onCreate() Received invalid start or end URL.");
        this.m_logger.Flush();
        setResult(RESULT_FAILED);
        finish();
    }
}