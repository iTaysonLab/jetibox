package com.xbox.httpclient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import okhttp3.Response;
import okio.Okio;

public class HttpClientResponse {
    private final long callHandle;
    private final Response response;

    private final class NativeOutputStream extends OutputStream {
        private final long callHandle;

        public NativeOutputStream(long j2) {
            this.callHandle = j2;
        }

        private native void nativeWrite(long j2, byte[] bArr, int i2, int i3);

        @Override
        public void write(byte[] bArr) {
            write(bArr, 0, bArr.length);
        }

        @Override
        public void write(byte[] bArr, int i2, int i3) {
            Objects.requireNonNull(bArr);
            if (i2 >= 0 && i3 >= 0 && i2 + i3 <= bArr.length) {
                nativeWrite(this.callHandle, bArr, i2, i3);
                return;
            }
            throw new IndexOutOfBoundsException();
        }

        @Override
        public void write(int i2) {
            write(new byte[]{(byte) i2});
        }
    }

    public HttpClientResponse(long j2, Response response) {
        this.callHandle = j2;
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

    public void getResponseBodyBytes() {
        try {
            this.response.body().source().readAll(Okio.sink(new NativeOutputStream(this.callHandle)));
        } catch (IOException ignored) {

        } finally {
            this.response.close();
        }
    }

    public int getResponseCode() {
        return this.response.code();
    }
}