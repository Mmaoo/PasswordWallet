/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.marcin_choina.password_wallet.controllers;

import com.marcin_choina.password_wallet.configuration.Security;
import com.marcin_choina.password_wallet.entities.Login;
import com.marcin_choina.password_wallet.entities.Login_ip;
import com.marcin_choina.password_wallet.model.AESenc;
import com.marcin_choina.password_wallet.model.Encryption;
import com.marcin_choina.password_wallet.model.MD5Enc;
import com.marcin_choina.password_wallet.entities.Password;
import com.marcin_choina.password_wallet.entities.User;
import com.marcin_choina.password_wallet.repositories.LoginRepository;
import com.marcin_choina.password_wallet.repositories.Login_ipRepository;
import com.marcin_choina.password_wallet.repositories.PasswordRepository;
import com.marcin_choina.password_wallet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.*;

@Controller
public class UserController {

//    @Autowired
//    private PasswordEncoder ShaPasswordEncoder;
//    @Autowired
//    private PasswordEncoder HmacPasswordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordRepository passwordRepository;
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private Login_ipRepository loginIpRepository;

    @GetMapping("/register")
    public String registerPage(Model m){
        m.addAttribute("user",new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerPagePost(@ModelAttribute(value = "user") User user){
        if(user.getLogin().trim().isEmpty() || user.getPassword_hash().trim().isEmpty()) return "redirect:/register";
        user.setSalt(Encryption.getInstance().generateSalt(20));
        user.setPassword_hash(Encryption.getInstance().getEncPass(user.getPassword_hash(),
                user.getSalt(),
                user.getIs_password_kept_as_hash()));
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Authentication authentication,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            Model m) throws IOException {
//        System.out.println(request.getParameter("error"));
        String ep = request.getParameter("error");
        if(ep == null) return "login";
        try {
            int eId = Integer.parseInt(ep);
            String error = null;
            switch (eId) {
                case Security.ERROR_USERNAME_NOT_FOUND:
                    error = "Nie znaleziono użytkownika";
                    break;
                case Security.ERROR_BAD_CREDENTIALS:
                    error = "Błędne dane logowania";
                    break;
                case Security.ERROR_USER_BLOCKED:
                    error = "Konto zablokowane";
                    break;
                case Security.ERROR_TEMPORARY_BLOCKED:
                    error = "Musisz poczekać";
                    Long date = null;
                    try {
                            long lDate = Long.parseLong(request.getParameter("date"));
                            date = (lDate - (new Date()).getTime())/1000;
                    }catch (NumberFormatException e){System.out.println(e.getMessage());}


//                    Date date = loginIpRepository.findByUserAndIp(
//                            userRepository.findByLogin(authentication.getName()),
//                            ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress()).getBlockade_date();
                    if (date != null) error += " " + date + "s";
                    break;
            }
            m.addAttribute("error", error);
        }catch (NumberFormatException e){
            System.out.println(e.getMessage());
            response.sendRedirect("login");}
        return "login";
    }

    @GetMapping("/change_password")
    public String changePasswordPage(Model m,
                                     Authentication authentication){
        User user = userRepository.findByLogin(authentication.getName());
        m.addAttribute("user",user);
        return "change_password";
    }

    @PostMapping("/change_password")
    public String changePasswordPostPage(HttpServletRequest request,
                                         Authentication authentication){
        String newPassword;
        String oldPassword;
        newPassword = request.getParameter("password");
        oldPassword = request.getParameter("old_password");
        boolean is_password_kept_as_hash = false;
        try {
            String is_hash = request.getParameter("is_password_kept_as_hash");
            System.out.println("is_hash="+is_hash);
            is_password_kept_as_hash = Boolean.parseBoolean(is_hash);
            System.out.println("is_hash_bool="+is_password_kept_as_hash);
        }catch (SpelEvaluationException e){e.printStackTrace();}

        if(newPassword != null && oldPassword != null) {
            System.out.println("is_password_kept_as_hash="+is_password_kept_as_hash);
            // Find logged in user
            User user = userRepository.findByLogin(authentication.getName());
            if (user != null) {

                // Check if old password is correct
                String oldPassEnc = Encryption.getInstance().getEncPass(
                        oldPassword, user.getSalt(), user.getIs_password_kept_as_hash());
                if (user.getPassword_hash().contentEquals(oldPassEnc)) {

                    user.setIs_password_kept_as_hash(is_password_kept_as_hash);
                    // Generate new salt
                    user.setSalt(Encryption.getInstance().generateSalt(20));
                    // Encrypt new user password
                    user.setPassword_hash(Encryption.getInstance().getEncPass(newPassword,
                            user.getSalt(),
                            is_password_kept_as_hash));
                    // Get Md5Pass
                    String oldMd5PassString = Base64.getEncoder().encodeToString(MD5Enc.calculateMD5(oldPassword));
                    byte[] newMd5Pass = MD5Enc.calculateMD5(newPassword);
                    String newMd5PassString = Base64.getEncoder().encodeToString(newMd5Pass);
                    // Encrypt passwords
                    Key oldKey = null;
                    Key newKey = null;
                    try {
                        oldKey = AESenc.generateKey(MD5Enc.calculateMD5(oldPassword));
                        newKey = AESenc.generateKey(newMd5Pass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    List<Password> passwords = user.getPasswords();
                    for (Password pass : passwords) {
                        try {
                            pass.setPassword(AESenc.decrypt(pass.getPassword(),oldKey));
                            pass.setPassword(AESenc.encrypt(pass.getPassword(),newKey));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        pass.setPassword(AESenc.decrypt(pass.getPassword(), oldMd5PassString));
//                        pass.setPassword(AESenc.encrypt(pass.getPassword(), newMd5PassString));
                    }
                    // Save user, passwords id database
                    userRepository.save(user);
                    passwordRepository.saveAll(passwords);
                    // Save new md5Pass in session
                    request.getSession().setAttribute("MD5Pass", newMd5Pass);
                    return "redirect:/passwords";
                }
            }
        }
        return "redirect:/change_password";
    }

    @GetMapping("/logins")
    public String userLoginsPage(Model m,
                                 HttpServletRequest request,
                                 Authentication authentication){
        User user = userRepository.findByLogin(authentication.getName());

//        List<Login> uLogins = userRepository.findByLogin(authentication.getName()).getLogins();
        List<Login> uLogins = loginRepository.findByUserOrderByDateAsc(user);
        List<Login> logins = new ArrayList<>();
        for(int i=uLogins.size()-2;i>=0;i--){
            Login log = uLogins.get(i);
            if(log.isSuccess()){
                logins.add(0,log);
                break;
            }else{
                logins.add(0,log);
            }
        }
        m.addAttribute("logins",logins);
        List<Login_ip> login_ips = user.getLogin_ips();
        m.addAttribute("login_ips",login_ips);
        return "logins";
    }

    @GetMapping("/blockade/remove")
    public String blockade_remove(
                      HttpServletRequest request,
                      Authentication authentication){
        int ipId = Integer.parseInt(request.getParameter("ip_id"));
        System.out.println("ipId="+ipId);
        Optional<Login_ip> optionalLoginIp = loginIpRepository.findById(ipId);
        if(optionalLoginIp.isPresent()){
            Login_ip login_ip = optionalLoginIp.get();
            System.out.println("Login_ip="+login_ip.getId());
            login_ip.setPermanent_blockade(false);
            login_ip.setLast_failed_logins(0);
            if(login_ip.getBlockade_date() != null){
                if(login_ip.getBlockade_date().compareTo(new Date()) > 0) login_ip.setBlockade_date(new Date());
            }
            loginIpRepository.save(login_ip);
        }
        return "redirect:/logins";
    }
}
