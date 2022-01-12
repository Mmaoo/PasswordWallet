/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.marcin_choina.password_wallet.model;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author Marcin
 */
public class SHAencNGTest {
    
    public SHAencNGTest() {
    }

    @Test
    public void testEncode() {
        System.out.println("encode");
        CharSequence charSequence = "strong_password";
        String expResult = "e7fca264bc39d9a39a1b89e6ed819f9e28290be64c265c361df1967441462a4e";
        String result = SHAenc.encode(charSequence);
        assertEquals(result, expResult);
    }
    
}
