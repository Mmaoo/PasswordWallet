package com.marcin_choina.password_wallet.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Enc {
    public static byte[] calculateMD5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(text.getBytes());
            return messageDigest;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
