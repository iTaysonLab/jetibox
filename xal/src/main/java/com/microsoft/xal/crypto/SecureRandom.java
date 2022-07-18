package com.microsoft.xal.crypto;

/* loaded from: classes2.dex */
public class SecureRandom {
    public static byte[] GenerateRandomBytes(int i2) {
        byte[] bArr = new byte[i2];
        new java.security.SecureRandom().nextBytes(bArr);
        return bArr;
    }
}