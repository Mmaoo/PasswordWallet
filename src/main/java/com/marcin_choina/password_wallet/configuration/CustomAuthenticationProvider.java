package com.marcin_choina.password_wallet.configuration;

import com.marcin_choina.password_wallet.entities.Login;
import com.marcin_choina.password_wallet.entities.Login_ip;
import com.marcin_choina.password_wallet.entities.User;
import com.marcin_choina.password_wallet.model.Encryption;
import com.marcin_choina.password_wallet.repositories.LoginRepository;
import com.marcin_choina.password_wallet.repositories.Login_ipRepository;
import com.marcin_choina.password_wallet.repositories.UserRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@Component
public class CustomAuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoginRepository loginRepository;

    @Autowired
    Login_ipRepository loginIpRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        String ip = ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        System.out.println("WebAuthenticationDetails remoteAddress: "+ip);

        User user = userRepository.findByLogin(username);
        if(user != null){
            Login_ip login_ip = loginIpRepository.findByUserAndIp(user,ip);
            checkBlocade(login_ip);

            String passwordHash = Encryption.getInstance().getEncPass(password, user.getSalt(), user.getIs_password_kept_as_hash());
//            System.out.println("authenticate: username="+username+", formPass="+password+", formPassEnc="+passwordHash+", userPassEnc="+user.getPassword_hash()+", salt="+user.getSalt()+", hash="+user.getIs_password_kept_as_hash());
//            passwordHash = Security.userPasswordEncoder(user.getIs_password_kept_as_hash()).encode(password);

            if(user.getPassword_hash().contentEquals(passwordHash)){
//                System.out.println("authenticate: pass equals");
                registerUserLogin(user,true,ip);
                registerUserLoginIp(user,true,ip,login_ip);
                return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
            } else{
//                System.out.println("authenticate: pass dont equals");
                registerUserLogin(user,false,ip);
                registerUserLoginIp(user,false,ip,login_ip);
                throw new BadCredentialsException("Złe dane logowania");
            }
        }
        throw new UsernameNotFoundException("Nie znaleziono użytkownika");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }

    public void checkBlocade(Login_ip login_ip) throws AuthenticationException{
        if(login_ip != null){
            if(login_ip.isPermanent_blockade()) throw new UserBlockedException("User is blocked");
            Date lDate = login_ip.getBlockade_date();
            if(lDate !=null && lDate.compareTo(new Date()) > 0) throw new UserTempBlockedException("User is temporary blocked",lDate);
        }
    }

    public Login registerUserLogin(User user, boolean success, String ip){
        Login login = null;
        if(user != null) {
            login = setUpRegisterUserLogin(user,success,ip);
            return loginRepository.save(login);
        }
        return login;
    }

    public Login setUpRegisterUserLogin(User user, boolean success, String ip){
        Login login = new Login(user);
        login.setDate(new Date());
        login.setSuccess(success);
        login.setIp(ip);
        return login;
    }

    public Login_ip registerUserLoginIp(User user, boolean success, String ip, Login_ip login_ip){
        if(user != null) {
            login_ip = setUpRegisterUserLoginIp(user,success,ip,login_ip);
            return loginIpRepository.save(login_ip);
        }
        return null;
    }

    public Login_ip setUpRegisterUserLoginIp(User user, boolean success, String ip, Login_ip login_ip){
        if (login_ip == null) {
            login_ip = new Login_ip(user);
            login_ip.setIp(ip);
            login_ip.setSuccessful_logins(0);
            login_ip.setFailed_logins(0);
            login_ip.setLast_failed_logins(0);
            login_ip.setPermanent_blockade(false);
            login_ip.setBlockade_date(null);
        }
        if (success) {
            login_ip.setBlockade_date(null);
            login_ip.setLast_failed_logins(0);
            login_ip.setSuccessful_logins(login_ip.getSuccessful_logins()+1);
        } else {
            login_ip.setFailed_logins(login_ip.getFailed_logins()+1);
            int failed = login_ip.getLast_failed_logins()+1;
            login_ip.setLast_failed_logins(failed);
            long interval = 0;
            if(failed == 2) interval = (1000*5);
            else if(failed == 3) interval = (1000*10);
            else if(failed == 4) interval = (1000*60*2);
            else if(failed >= 5) login_ip.setPermanent_blockade(true);
            Date newDate = new Date();
            newDate.setTime(new Date().getTime()+(interval));
            login_ip.setBlockade_date(newDate);
        }
        return login_ip;
    }

    public class UserBlockedException extends AuthenticationException{
        public UserBlockedException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public UserBlockedException(String msg) {
            super(msg);
        }
    }

    public class UserTempBlockedException extends AuthenticationException{
        Date date;

        public UserTempBlockedException(String msg, Throwable cause, Date date) {
            super(msg, cause);
            this.date = date;
        }

        public UserTempBlockedException(String msg, Date date) {
            super(msg);
            this.date = date;
        }
    }

}
