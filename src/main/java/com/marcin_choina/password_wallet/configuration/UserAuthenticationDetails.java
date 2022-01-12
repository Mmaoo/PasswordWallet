package com.marcin_choina.password_wallet.configuration;

import com.marcin_choina.password_wallet.entities.User;
import com.marcin_choina.password_wallet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserAuthenticationDetails implements UserDetailsService {

    @Autowired
    public UserRepository rep;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = rep.findByLogin(login);
        if (user != null){
            List<GrantedAuthority> grupa = new ArrayList<>();
            grupa.add(new SimpleGrantedAuthority("normalUser"));
            return new org.springframework.security.core.userdetails.User(
                    user.getLogin(),user.getPassword_hash(),true,true,true,true,grupa);
        }else{
            throw new UsernameNotFoundException("Zły login lub hasło");
        }
    }
}
