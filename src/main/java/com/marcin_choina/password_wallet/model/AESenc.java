package com.marcin_choina.password_wallet.model;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

public class AESenc {

    private static final String ALGO = "AES";

    //encrypts string and returns encrypted string
    public static String encrypt(String data, Key key) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encVal);
    }
    //decrypts string and returns plain text
    public static String decrypt(String encryptedData, Key key) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decValue = c.doFinal(decodedValue);
        return new String(decValue);
    }
    // Generate a new encryption key.
    public static Key generateKey(byte[] keyValue) throws Exception {
        return new SecretKeySpec(keyValue, ALGO);
    }

    // Encrypt with String key
    public static String encrypt(String data, String key) {
        try {
//            System.out.println("AESenc encrypt: data="+data+", key="+key);
            Key genKey = generateKey(key.getBytes(StandardCharsets.UTF_8));
            String e = encrypt(data,genKey);
//            System.out.println("AESenc encrypt: e = "+e);
            return e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Decrypt with String key
    public static String decrypt(String encryptedData, String key) {
        try {
            Key genKey = generateKey(key.getBytes(StandardCharsets.UTF_8));
            return decrypt(encryptedData,genKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
