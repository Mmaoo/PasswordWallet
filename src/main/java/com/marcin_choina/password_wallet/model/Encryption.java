package com.marcin_choina.password_wallet.model;

import net.bytebuddy.utility.RandomString;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.Base64;

@Configuration
public class Encryption{

    @Resource
    public Environment environment;

    private static Encryption encryption = null;

    public Encryption(){
        encryption = this;
    }

    public static Encryption getInstance(){
        if(encryption == null) encryption = new Encryption();
        return encryption;
    }

    // Generate random Salt
    public String generateSalt(int len){
        return RandomString.make(len);
    }

    public String getSha(String pass, String salt){
        String saltedPass = pass+salt;
        return SHAenc.encode(saltedPass);
    }

    public String getEncPass(String pass, String salt, boolean hmac){
//        System.out.println("getEncPass: pass="+pass+", salt="+salt);
        pass = getSha(pass,salt);
//        System.out.println("getEncPass: shaPass="+pass);
        if(hmac) return getHmacPass(pass);
        else return getAesPass(pass);
    }

    private String getAesPass(String pass){
        return AESenc.encrypt(pass,getPepper());
    }

    private String getHmacPass(String pass){
        return HMACenc.encode(pass,getPepper());
    }

    private String getPepper(){
        String p = Base64.getEncoder().encodeToString(MD5Enc.calculateMD5(environment.getProperty("pepper")));
//        System.out.println("getPepper: pepper="+p);
        return p;
    }

}
