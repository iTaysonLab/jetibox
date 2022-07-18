package com.xbox.httpclient;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

/* loaded from: classes2.dex */
public final class HttpClientRequestBody extends RequestBody {
    private final long callHandle;
    private final long contentLength;
    private final MediaType contentType;

    public HttpClientRequestBody(long j2, String str, long j3) {
        this.callHandle = j2;
        this.contentType = str != null ? MediaType.parse(str) : null;
        this.contentLength = j3;
    }

    @Override // okhttp3.RequestBody
    public long contentLength() {
        return this.contentLength;
    }

    @Override // okhttp3.RequestBody
    public MediaType contentType() {
        return this.contentType;
    }

    @Override
    public void writeTo(@NonNull BufferedSink bufferedSink) throws IOException {
        bufferedSink.writeAll(Okio.source(new NativeInputStream(this.callHandle)));
    }

    /* loaded from: classes2.dex */
    private final class NativeInputStream extends InputStream {
        private final long callHandle;
        private long offset = 0;

        public NativeInputStream(long j2) {
            this.callHandle = j2;
        }

        private native int nativeRead(long j2, long j3, byte[] bArr, long j4, long j5);

        @Override // java.io.InputStream
        public int read() {
            byte[] bArr = new byte[1];
            read(bArr);
            return bArr[0];
        }

        @Override // java.io.InputStream
        public long skip(long j2) {
            this.offset += j2;
            return j2;
        }

        @Override // java.io.InputStream
        public int read(byte[] bArr) {
            return read(bArr, 0, bArr.length);
        }

        @Override // java.io.InputStream
        public int read(byte[] bArr, int i2, int i3) {
            Objects.requireNonNull(bArr);
            if (i2 < 0 || i3 < 0 || i2 + i3 > bArr.length) {
                throw new IndexOutOfBoundsException();
            }
            if (i3 == 0) {
                return 0;
            }
            int nativeRead = nativeRead(this.callHandle, this.offset, bArr, i2, i3);
            if (nativeRead == -1) {
                return -1;
            }
            this.offset += nativeRead;
            return nativeRead;
        }
    }
}