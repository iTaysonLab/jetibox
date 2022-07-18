package com.xbox.httpclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClientRequest {
    private static final byte[] NO_BODY = new byte[0];
    private static final OkHttpClient OK_CLIENT = new OkHttpClient.Builder().retryOnConnectionFailure(false).build();
    private Request.Builder requestBuilder = new Request.Builder();

    public native void OnRequestCompleted(long j2, HttpClientResponse httpClientResponse);

    public native void OnRequestFailed(long j2, String str, String str2, boolean z);

    public void doRequestAsync(final long j2) {
        OK_CLIENT.newCall(this.requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException iOException) {
                StringWriter stringWriter = new StringWriter();
                iOException.printStackTrace(new PrintWriter(stringWriter));
                HttpClientRequest.this.OnRequestFailed(j2, iOException.getClass().getCanonicalName(), stringWriter.toString(), iOException instanceof UnknownHostException);
            }

            @Override
            public void onResponse(Call call, Response response) {
                HttpClientRequest httpClientRequest = HttpClientRequest.this;
                httpClientRequest.OnRequestCompleted(j2, new HttpClientResponse(j2, response));
            }
        });
    }

    public void setHttpHeader(String str, String str2) {
        this.requestBuilder = this.requestBuilder.addHeader(str, str2);
    }

    public void setHttpMethodAndBody(String str, long j2, String str2, long j3) {
        MediaType mediaType = null;
        RequestBody httpClientRequestBody = null;
        if (j3 == 0) {
            if ("POST".equals(str) || "PUT".equals(str)) {
                if (str2 != null) {
                    mediaType = MediaType.parse(str2);
                }
                httpClientRequestBody = RequestBody.create(NO_BODY, mediaType);
            }
        } else {
            httpClientRequestBody = new HttpClientRequestBody(j2, str2, j3);
        }
        this.requestBuilder.method(str, httpClientRequestBody);
    }

    public void setHttpUrl(String str) {
        this.requestBuilder = this.requestBuilder.url(str);
    }
}