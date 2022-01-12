package com.marcin_choina.password_wallet.configuration;

import com.marcin_choina.password_wallet.entities.Login;
import com.marcin_choina.password_wallet.entities.Login_ip;
import com.marcin_choina.password_wallet.model.MD5Enc;
import com.marcin_choina.password_wallet.repositories.LoginRepository;
import com.marcin_choina.password_wallet.repositories.Login_ipRepository;
import com.marcin_choina.password_wallet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Date;

@Configuration
@EnableWebSecurity
public class Security extends WebSecurityConfigurerAdapter {

    public static final int ERROR_USERNAME_NOT_FOUND = 1;
    public static final int ERROR_BAD_CREDENTIALS = 2;
    public static final int ERROR_USER_BLOCKED = 3;
    public static final int ERROR_TEMPORARY_BLOCKED = 4;

    @Autowired
    UserRepository userRepository;
    @Autowired
    Login_ipRepository loginIpRepository;

    @Autowired
    private UserAuthenticationDetails userAuthenticationDetails;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.httpBasic().disable()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/logins")
                .authenticated()
                .antMatchers("/register","/login**")
                .permitAll()
                .antMatchers("/webjars/jquery/**","/webjars/bootstrap/**")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("login")
                .passwordParameter("passwd")
                .successHandler(this::loginSuccessHandler)
                .failureHandler(this::loginFailureHandler /*new CustomAuthenticationFailureHandler()*/)
//                .defaultSuccessUrl("/passwords",true)
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true);
    }

    private void loginSuccessHandler(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        // Save password's md5 hash to session
        byte[] md5Pass = MD5Enc.calculateMD5(request.getParameter("passwd"));
        request.getSession().setAttribute("MD5Pass",md5Pass);

        response.sendRedirect("passwords/show");
    }

    private void loginFailureHandler(HttpServletRequest request,
                                     HttpServletResponse response,
                                     AuthenticationException e) throws IOException {
        int error = -1;
        if(e.getClass().isAssignableFrom(UsernameNotFoundException.class)) error = ERROR_USERNAME_NOT_FOUND;
        else if(e.getClass().isAssignableFrom(BadCredentialsException.class)) error = ERROR_BAD_CREDENTIALS;
        else if(e.getClass().isAssignableFrom(CustomAuthenticationProvider.UserBlockedException.class)) error = ERROR_USER_BLOCKED;
        else if(e.getClass().isAssignableFrom(CustomAuthenticationProvider.UserTempBlockedException.class)) error = ERROR_TEMPORARY_BLOCKED;

        String errorDate = "";
        if(error == ERROR_TEMPORARY_BLOCKED) {
            Login_ip login_ip = loginIpRepository.findByUserAndIp(
                    userRepository.findByLogin(request.getParameter("login")),
                    request.getRemoteAddr());
            if (login_ip != null) {
                Date date = login_ip.getBlockade_date();
                if (date != null) errorDate = "&date=" + date.getTime();
            }
            System.out.println(errorDate);
        }
        response.sendRedirect("login?error="+error+errorDate);
    }
}
