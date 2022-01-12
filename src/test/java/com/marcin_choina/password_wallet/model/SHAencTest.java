package com.marcin_choina.password_wallet.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class SHAencTest {

    @ParameterizedTest
    @DisplayName("Hash data")
    @CsvSource({"strong_password,e7fca264bc39d9a39a1b89e6ed819f9e28290be64c265c361df1967441462a4e",
                "qwerty,65e84be33532fb784c48129675f9eff3a682b27168c0ea744b2cf58ee02337c5"})
    void encodeData(String data, String expResult) {
        assertArrayEquals(expResult.toCharArray(),SHAenc.encode(data).toCharArray());
    }
}