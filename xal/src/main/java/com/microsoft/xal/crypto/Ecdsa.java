package com.microsoft.xal.crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/* loaded from: classes2.dex */
public class Ecdsa {
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String ECDSA_SIGNATURE_NAME = "NONEwithECDSA";
    private static final String EC_ALGORITHM_NAME = "secp256r1";
    private static final String KEY_ALIAS_PREFIX = "xal_";
    private KeyPair keyPair;
    private String uniqueId;

    static {
        Security.removeProvider("BC");
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    private static String getBase64StringFromBytes(byte[] bArr) {
        return Base64.encodeToString(bArr, 0, bArr.length, 11);
    }

    private static byte[] getBytesFromBase64String(String str) {
        return Base64.decode(str, 11);
    }

    private static String getKeyAlias(String str) {
        return KEY_ALIAS_PREFIX + str;
    }

    public static Ecdsa restoreKeyAndId(Context context) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.microsoft.xal.crypto", 0);
        if (sharedPreferences.contains("id") && sharedPreferences.contains("public") && sharedPreferences.contains("private")) {
            String string = sharedPreferences.getString("public", "");
            String string2 = sharedPreferences.getString("private", "");
            String string3 = sharedPreferences.getString("id", "");
            if (!string.isEmpty() && !string2.isEmpty() && !string3.isEmpty()) {
                byte[] bytesFromBase64String = getBytesFromBase64String(string);
                byte[] bytesFromBase64String2 = getBytesFromBase64String(string2);
                KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
                Ecdsa ecdsa = new Ecdsa();
                ecdsa.uniqueId = string3;
                ecdsa.keyPair = new KeyPair((ECPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(bytesFromBase64String)), (ECPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(bytesFromBase64String2)));
                return ecdsa;
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.clear();
            edit.commit();
            return null;
        }
        SharedPreferences.Editor edit2 = sharedPreferences.edit();
        edit2.clear();
        edit2.commit();
        return null;
    }

    private byte[] toP1363SignedBuffer(byte[] bArr) {
        byte b2 = bArr[3];
        int i2 = 4 + b2 + 1;
        int i3 = i2 + 1;
        byte b3 = bArr[i2];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writeAdjustedHalfOfAsn1ToP1363(bArr, 4, b2, byteArrayOutputStream);
        writeAdjustedHalfOfAsn1ToP1363(bArr, i3, b3, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void writeAdjustedHalfOfAsn1ToP1363(byte[] bArr, int i2, int i3, ByteArrayOutputStream byteArrayOutputStream) {
        if (i3 > 32) {
            byteArrayOutputStream.write(bArr, i2 + (i3 - 32), 32);
        } else if (i3 < 32) {
            int i4 = 32 - i3;
            byteArrayOutputStream.write(new byte[i4], 0, i4);
            byteArrayOutputStream.write(bArr, i2, i3);
        } else {
            byteArrayOutputStream.write(bArr, i2, i3);
        }
    }

    public void generateKey(String str) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
        keyPairGenerator.initialize(new ECGenParameterSpec(EC_ALGORITHM_NAME));
        this.uniqueId = str;
        this.keyPair = keyPairGenerator.generateKeyPair();
    }

    public EccPubKey getPublicKey() {
        return new EccPubKey((ECPublicKey) this.keyPair.getPublic());
    }

    public String getUniqueId() {
        return this.uniqueId;
    }

    public byte[] hashAndSign(byte[] bArr) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        ShaHasher shaHasher = new ShaHasher();
        shaHasher.AddBytes(bArr);
        return sign(shaHasher.SignHash());
    }

    public byte[] sign(byte[] bArr) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(ECDSA_SIGNATURE_NAME);
        signature.initSign(this.keyPair.getPrivate());
        signature.update(bArr);
        return toP1363SignedBuffer(signature.sign());
    }

    public boolean storeKeyPairAndId(Context context, String str) {
        SharedPreferences.Editor edit = context.getSharedPreferences("com.microsoft.xal.crypto", 0).edit();
        edit.putString("id", str);
        edit.putString("public", getBase64StringFromBytes(this.keyPair.getPublic().getEncoded()));
        edit.putString("private", getBase64StringFromBytes(this.keyPair.getPrivate().getEncoded()));
        return edit.commit();
    }
}