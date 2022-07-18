package com.microsoft.xal.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* loaded from: classes2.dex */
public class ShaHasher {
    private final MessageDigest md = getInstance("SHA-256");

    public void AddBytes(byte[] bArr) {
        this.md.update(bArr);
    }

    public byte[] SignHash() {
        return this.md.digest();
    }

    private MessageDigest getInstance(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}