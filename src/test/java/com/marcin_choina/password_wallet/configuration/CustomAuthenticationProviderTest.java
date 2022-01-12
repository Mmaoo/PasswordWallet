package com.marcin_choina.password_wallet.configuration;

import com.marcin_choina.password_wallet.entities.Login;
import com.marcin_choina.password_wallet.entities.Login_ip;
import com.marcin_choina.password_wallet.entities.User;
import org.testng.annotations.ExpectedExceptions;
import org.testng.annotations.Test;
import org.thymeleaf.util.DateUtils;

import java.util.Date;

import static org.testng.Assert.*;

public class CustomAuthenticationProviderTest {

    @Test
    public void testCheckBlocade_nothing_ifUserIsNotBlocked(){
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider();
        Login_ip login_ip = new Login_ip();
        login_ip.setPermanent_blockade(false);
        login_ip.setBlockade_date(null);
        provider.checkBlocade(login_ip);
        assertTrue(true);
    }

    @Test(expectedExceptions = CustomAuthenticationProvider.UserBlockedException.class)
    public void testCheckBlocade_UserBlockedException_ifUserIsPermanentBlocked(){
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider();
        Login_ip login_ip = new Login_ip();
        login_ip.setPermanent_blockade(true);
        login_ip.setBlockade_date(new Date());
        provider.checkBlocade(login_ip);
        assertTrue(true);
    }

    @Test(expectedExceptions = CustomAuthenticationProvider.UserTempBlockedException.class)
    public void testCheckBlocade_UserBlockedException_ifUserIsTemporaryBlocked(){
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider();
        Login_ip login_ip = new Login_ip();
        login_ip.setPermanent_blockade(false);
        login_ip.setBlockade_date(new Date((new Date()).getTime()+(1000*60)));
        provider.checkBlocade(login_ip);
        assertTrue(true);
    }

    @Test
    public void testSetUpRegisterUserLogin_true_ifProperLoginAttributes() {
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider();
        User user = new User(1);
        user.setLogin("test");
        boolean success = true;
        String ip = "192.168.0.1";
        Login login = provider.setUpRegisterUserLogin(user,success,ip);
        assertEquals(user,login.getUser());
        assertEquals(success,login.isSuccess());
        assertEquals(ip,login.getIp());
        assertNotNull(login.getDate());
    }

    @Test
    public void testSetUpRegisterUserLoginIp_true_ifSetNewLoginIpWithSuccessLogin() {
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider();
        User user = new User(1);
        user.setLogin("test");
        boolean success = true;
        String ip = "192.168.0.1";
        Login_ip result = provider.setUpRegisterUserLoginIp(user,success,ip,null);
        assertEquals(result.getUser(),user);
        assertEquals(result.getIp(),ip);
        assertNull(result.getBlockade_date());
        assertEquals(result.getLast_failed_logins(),0);
        assertEquals(result.getSuccessful_logins(),1);
        assertEquals(result.getFailed_logins(),0);
        assertFalse(result.isPermanent_blockade());
    }

    @Test
    public void testSetUpRegisterUserLoginIp_true_ifSetNewLoginIpWithFailLogin() {
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider();
        User user = new User(1);
        user.setLogin("test");
        boolean success = false;
        String ip = "192.168.0.1";
        Login_ip result = provider.setUpRegisterUserLoginIp(user,success,ip,null);
        assertEquals(result.getUser(),user);
        assertEquals(result.getIp(),ip);
        assertNotNull(result.getBlockade_date());
        assertEquals(result.getLast_failed_logins(),1);
        assertEquals(result.getSuccessful_logins(),0);
        assertEquals(result.getFailed_logins(),1);
        assertFalse(result.isPermanent_blockade());
    }

    @Test
    public void testSetUpRegisterUserLoginIp_true_ifSetExistedLoginIpWithSuccessLogin() {
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider();
        User user = new User(1);
        user.setLogin("test");
        boolean success = true;
        String ip = "192.168.0.1";
        Login_ip login_ip = new Login_ip(user);
        login_ip.setId(1);
        login_ip.setIp(ip);
        login_ip.setLast_failed_logins(2);
        login_ip.setSuccessful_logins(10);
        login_ip.setFailed_logins(10);
        Login_ip result = provider.setUpRegisterUserLoginIp(user,success,ip,login_ip);
        assertEquals(result.getUser(),user);
        assertEquals(result.getIp(),ip);
        assertNull(result.getBlockade_date());
        assertEquals(result.getLast_failed_logins(),0);
        assertEquals(result.getSuccessful_logins(),11);
        assertEquals(result.getFailed_logins(),10);
        assertFalse(result.isPermanent_blockade());
    }

    @Test
    public void testSetUpRegisterUserLoginIp_true_ifSetExistedLoginIpWithFailLogin() {
        CustomAuthenticationProvider provider = new CustomAuthenticationProvider();
        User user = new User(1);
        user.setLogin("test");
        boolean success = false;
        String ip = "192.168.0.1";
        Login_ip login_ip = new Login_ip(user);
        login_ip.setId(1);
        login_ip.setIp(ip);
        login_ip.setLast_failed_logins(4);
        login_ip.setSuccessful_logins(10);
        login_ip.setFailed_logins(10);
        Login_ip result = provider.setUpRegisterUserLoginIp(user,success,ip,login_ip);
        assertEquals(result.getUser(),user);
        assertEquals(result.getIp(),ip);
        assertNotNull(result.getBlockade_date());
        assertEquals(result.getLast_failed_logins(),5);
        assertEquals(result.getSuccessful_logins(),10);
        assertEquals(result.getFailed_logins(),11);
        assertTrue(result.isPermanent_blockade());
    }
}