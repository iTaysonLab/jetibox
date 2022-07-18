package com.xbox.httpclient;

import java.io.IOException;
import okhttp3.Response;

public class HttpClientResponseGS {
    private final Response response;

    public HttpClientResponseGS(Response response) {
        this.response = response;
    }

    public String getHeaderNameAtIndex(int i2) {
        if (i2 < 0 || i2 >= this.response.headers().size()) {
            return null;
        }
        return this.response.headers().name(i2);
    }

    public String getHeaderValueAtIndex(int i2) {
        if (i2 < 0 || i2 >= this.response.headers().size()) {
            return null;
        }
        return this.response.headers().value(i2);
    }

    public int getNumHeaders() {
        return this.response.headers().size();
    }

    public byte[] getResponseBodyBytes() {
        try {
            return this.response.body().bytes();
        } catch (IOException unused) {
            return null;
        }
    }

    public int getResponseCode() {
        return this.response.code();
    }
}