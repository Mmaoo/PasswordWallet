package com.marcin_choina.password_wallet.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class AESencTest {

    @ParameterizedTest
    @DisplayName("Encrypt tekst with key")
    @CsvSource({"qwerty,keykeykeykeykeyk,3lRgw19QzMmH0w/uZ2ab/w=="})
    void encryptDataWithKeyTest(String data, String key, String expResult) {
        assertArrayEquals(expResult.toCharArray(),AESenc.encrypt(data,key).toCharArray());
    }

    @ParameterizedTest
    @DisplayName("Decrypt tekst with key")
    @CsvSource({"3lRgw19QzMmH0w/uZ2ab/w==,keykeykeykeykeyk,qwerty"})
    void decryptDataWithKeyTest(String encData, String key, String expResult) {
        assertArrayEquals(expResult.toCharArray(),AESenc.decrypt(encData,key).toCharArray());
    }

    @ParameterizedTest
    @DisplayName("Is AES Encrypt Reversible")
    @CsvSource({"strong_password,keykeykeykeykeyk",
                "qwertyuiop,1234567891012131"})
    void isAESEncryptionReversible(String data, String key) {
        String encData = AESenc.encrypt(data,key);
        String resultData = AESenc.decrypt(encData,key);
        assertArrayEquals(data.toCharArray(),resultData.toCharArray());
    }
}