/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.marcin_choina.password_wallet.model;

import java.security.Key;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Marcin
 */
public class AESencNGTest {
    
    public AESencNGTest() {
    }

    @Test
    public void encrypt_true_ifProperEcryptString() {
        System.out.println("encrypt");
        String data = "qwerty";
        String key = "keykeykeykeykeyk";
        String expResult = "3lRgw19QzMmH0w/uZ2ab/w==";
        String result = AESenc.encrypt(data, key);
        assertEquals(result, expResult);
    }

    @Test
    public void decrypt_true_ifProperDecryptString() {
        System.out.println("decrypt");
        String encryptedData = "3lRgw19QzMmH0w/uZ2ab/w==";
        String key = "keykeykeykeykeyk";
        String expResult = "qwerty";
        String result = AESenc.decrypt(encryptedData, key);
        assertEquals(result, expResult);
    }
    
    @Test
    void isAESEncryptionReversible_true_ifEncryptedAndDecryptedDataIsEqualToInputData() {
        String data = "qwerty";
        String key = "keykeykeykeykeyk";
        String encData = AESenc.encrypt(data,key);
        String resultData = AESenc.decrypt(encData,key);
        assertEquals(data.toCharArray(),resultData.toCharArray());
    }
    
}
