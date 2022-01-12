package com.marcin_choina.password_wallet;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.thymeleaf.util.DateUtils;

import java.util.Date;
import java.util.Locale;

public class DateTest {

    @Test
    public void dateTest(){
        Date date = DateUtils.create(1997,3,19,12,0,0,0).getTime();
        System.out.println(DateUtils.format(date, "YYYY-MM-dd HH:mm:ss:SS", Locale.ENGLISH));
        date.setTime(date.getTime()+(1000*60));
        System.out.println(DateUtils.format(date, "YYYY-MM-dd HH:mm:ss:SS", Locale.ENGLISH));
    }
}
