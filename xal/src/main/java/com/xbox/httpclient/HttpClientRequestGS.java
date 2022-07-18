package com.xbox.httpclient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClientRequestGS {
    private static final byte[] NO_BODY = new byte[0];
    private static final OkHttpClient OK_CLIENT = new OkHttpClient.Builder().retryOnConnectionFailure(false).build();
    private Request okHttpRequest;
    private Request.Builder requestBuilder = new Request.Builder();

    public native void OnRequestCompleted(long j2, HttpClientResponseGS httpClientResponseGS);

    public native void OnRequestFailed(long j2, String str);

    public static HttpClientRequestGS createClientRequest() {
        return new HttpClientRequestGS();
    }

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void doRequestAsync(final long j2) {
        OK_CLIENT.newCall(this.requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException iOException) {
                HttpClientRequestGS.this.OnRequestFailed(j2, iOException.getClass().getCanonicalName());
            }

            @Override
            public void onResponse(Call call, Response response) {
                HttpClientRequestGS.this.OnRequestCompleted(j2, new HttpClientResponseGS(response));
            }
        });
    }

    public void setHttpHeader(String str, String str2) {
        this.requestBuilder = this.requestBuilder.addHeader(str, str2);
    }

    public void setHttpMethodAndBody(String str, String str2, byte[] bArr) {
        if (bArr != null && bArr.length != 0) {
            this.requestBuilder = this.requestBuilder.method(str, RequestBody.create(MediaType.parse(str2), bArr));
        } else if (!"POST".equals(str) && !"PUT".equals(str)) {
            this.requestBuilder = this.requestBuilder.method(str, null);
        } else {
            this.requestBuilder = this.requestBuilder.method(str, RequestBody.create((MediaType) null, NO_BODY));
        }
    }

    public void setHttpUrl(String str) {
        this.requestBuilder = this.requestBuilder.url(str);
    }
}