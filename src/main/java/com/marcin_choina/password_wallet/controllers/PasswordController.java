package com.marcin_choina.password_wallet.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcin_choina.password_wallet.entities.SharedPassword;
import com.marcin_choina.password_wallet.model.AESenc;
import com.marcin_choina.password_wallet.entities.Password;
import com.marcin_choina.password_wallet.entities.User;
import com.marcin_choina.password_wallet.model.MD5Enc;
import com.marcin_choina.password_wallet.repositories.PasswordRepository;

import java.lang.String;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.marcin_choina.password_wallet.repositories.SharedPasswordRepository;
import com.marcin_choina.password_wallet.repositories.UserRepository;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PasswordController {

    @Autowired
    private PasswordRepository passwordRepository;

    @Autowired
    private SharedPasswordRepository sharedPasswordRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = {"/passwords/show","/passwords/show/{edit}"})
    public String passwordShowPage(@PathVariable(required = false) String edit,
                                Model m,
                               Authentication authentication,
                               HttpServletRequest request){
        if(edit == null) edit = "";
        User user = userRepository.findByLogin(authentication.getName());
        List<Password> passwords = user.getPasswords();
        List<SharedPassword> sharedPasswords = user.getShared_passwords();
//        try {
//            byte[] md5Pass = ((byte[]) request.getSession().getAttribute("MD5Pass"));
//            Key key = AESenc.generateKey(md5Pass);
//            for (Password password:passwords) {
//                password.setPassword(AESenc.decrypt(password.getPassword(),key));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if(edit.contentEquals("edit")) m.addAttribute("edit",true);
        else m.addAttribute("edit",false);
        m.addAttribute("passwords", passwords);
        m.addAttribute("sharedPasswords",sharedPasswords);
        return "passwords_show";
//       if(edit.contentEquals("edit")) return "passwords_show_edit";
//       else return "passwords_show_read";
    }

//    @GetMapping("/passwords/show/edit")
//    public String passwordShowEditPage(Model m,
//                               Authentication authentication,
//                               HttpServletRequest request){
//        List<Password> passwords = userRepository.findByLogin(authentication.getName()).getPasswords();
//
//        try {
//            byte[] md5Pass = ((byte[]) request.getSession().getAttribute("MD5Pass"));
//            Key key = AESenc.generateKey(md5Pass);
//            for (Password password:passwords) {
//                password.setPassword(AESenc.decrypt(password.getPassword(),key));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        m.addAttribute("passwords", passwords);
//        return "passwords_show_read";
//    }

    @GetMapping("/passwords/add")
    public String passwordAddPage(Model m){
        m.addAttribute("newPassword",new Password());
        return "passwords_add";
    }

    @PostMapping("/passwords/add")
    public String passwordAddPagePost(@ModelAttribute(value = "newPassword") Password password,
                                      HttpServletRequest request,
                                      Authentication authentication){
        try {
            byte[] md5Pass = ((byte[]) request.getSession().getAttribute("MD5Pass"));
            //byte[] md5Pass = MD5Enc.calculateMD5(mainPass); // to nie powinno tu byc
//            System.out.println("md5pass: "+md5Pass);
            Key key = AESenc.generateKey(md5Pass);
            password.setPassword(AESenc.encrypt(password.getPassword(), key));

            User user = userRepository.findByLogin(authentication.getName());
            password.setUser(user);
            passwordRepository.save(password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/passwords/show";
    }

    @GetMapping("/passwords/edit/{id}")
    public String passwordEditPage(@PathVariable Integer id,
                                   Model m,
                                   HttpServletRequest request,
                                   Principal principal){
        Optional<Password> passwordOptional = passwordRepository.findById(id);
        if(passwordOptional.isPresent()) {
            Password password = passwordOptional.get();
            if(password.getUser() == userRepository.findByLogin(principal.getName())) {
                try {
                    byte[] md5Pass = ((byte[]) request.getSession().getAttribute("MD5Pass"));
                    Key key = AESenc.generateKey(md5Pass);
                    password.setPassword(AESenc.decrypt(password.getPassword(), key));
                    m.addAttribute("editPassword", password);
                    return "passwords_edit";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "redirect:/passwords/show/edit";
    }

    @PostMapping(value = {"/passwords/edit","/passwords/edit/{id}"})
    public String passwordEditPagePost(@PathVariable(required = false) Integer id,
                                       @ModelAttribute(value = "editPassword") Password password,
                                      HttpServletRequest request,
                                      Principal principal){
        User user = userRepository.findByLogin(principal.getName());
        if(user != null) {
            Optional<Password> dbPasswordOptional = passwordRepository.findById(password.getId());
            if(dbPasswordOptional.isPresent()) {
                Password dbPassword = dbPasswordOptional.get();
                if(dbPassword.getUser() == user) {
                    try {
                        byte[] md5Pass = ((byte[]) request.getSession().getAttribute("MD5Pass"));
                        Key key = AESenc.generateKey(md5Pass);
                        dbPassword.setPassword(AESenc.encrypt(password.getPassword(), key));
                        dbPassword.setLogin(password.getLogin());
                        dbPassword.setDescription(password.getDescription());
                        dbPassword.setWeb_address(password.getWeb_address());
                        for(SharedPassword sharedPassword : dbPassword.getSharedPasswords()){
                            sharedPassword.setPassword(password.getPassword());
                            sharedPassword.setPassword(encryptSharedPassword(sharedPassword,request).getPassword());
                            sharedPasswordRepository.save(sharedPassword);
                        }
                        passwordRepository.save(dbPassword);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "redirect:/passwords/show/edit";
    }

    @GetMapping("/passwords/remove/{id}")
    public String passwordRemovePage(@PathVariable Integer id,
                                     HttpServletRequest request,
                                     Principal principal){
        Optional<Password> passwordOptional = passwordRepository.findById(id);
        if(passwordOptional.isPresent()){
            Password password = passwordOptional.get();
            if(password.getUser() == userRepository.findByLogin(principal.getName())){
                passwordRepository.delete(password);
            }
        }
//        try {
//            int pass_id = Integer.parseInt(request.getParameter("pass_id"));
//            passwordRepository.deleteById(pass_id);
//        }catch (Exception e){e.printStackTrace();}
        return "redirect:/passwords/show/edit";
    }

    @ResponseBody
    @PostMapping(value = "/api/passwords/decrypt")
    public ResponseEntity<String> passwordDecryptApi(
            HttpServletRequest request,
            @RequestBody String requestPassword,
            Errors errors,
            Principal principal){
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(requestPassword);
            int password_id = jsonNode.get("password_id").asInt();
            JsonNode shared = jsonNode.get("shared");
            if(shared != null && shared.asBoolean()){
                Optional<SharedPassword> passwordOptional = sharedPasswordRepository.findById(password_id);
                if(passwordOptional.isPresent()) {
                    SharedPassword password = passwordOptional.get();
                    if(password.getUser() == userRepository.findByLogin(principal.getName())) {
//                    Key key = AESenc.generateKey(md5Pass);
//                    String encPass = AESenc.decrypt(password.getPassword(), key);
                        decryptSharedPassword(password,request);
                        String encPass = password.getPassword();
                        return ResponseEntity.ok("{\"enc_pass\":\"" + encPass + "\"}");
                    }
                }
            }else{
                byte[] md5Pass = ((byte[]) request.getSession().getAttribute("MD5Pass"));
                Optional<Password> passwordOptional = passwordRepository.findById(password_id);
                if(passwordOptional.isPresent()) {
                    Password password = passwordOptional.get();
                    if(password.getUser() == userRepository.findByLogin(principal.getName())){
                        Key key = AESenc.generateKey(md5Pass);
                        String encPass = AESenc.decrypt(password.getPassword(), key);
                        return ResponseEntity.ok("{\"enc_pass\":\"" + encPass + "\"}");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @ResponseBody
    @PostMapping(value = "/api/passwords/share")
    public ResponseEntity<String> passwordShareApi(
            Principal principal,
            HttpServletRequest request,
            @RequestBody String requestData, Errors errors){
        ObjectMapper mapper = new ObjectMapper();
        int password_id = -1;
        String email = "";
        try {
            JsonNode jsonNode = mapper.readTree(requestData);
            password_id = jsonNode.get("password_id").asInt();
            email = jsonNode.get("email").asText();
            System.out.println("data: password_id="+password_id+", email="+email);
            byte[] md5Pass = ((byte[]) request.getSession().getAttribute("MD5Pass"));
            Optional<Password> passwordOptional = passwordRepository.findById(password_id);
            if(passwordOptional.isPresent()) {
                Password password = passwordOptional.get();
                User newUser = userRepository.findByEmail(email);
                if(newUser != null) {
                    Key key = AESenc.generateKey(md5Pass);
                    String decPass = AESenc.decrypt(password.getPassword(), key);
                    User user = userRepository.findByLogin(principal.getName());
                    if (password.getUser() == user) {
                        SharedPassword sharedPassword = new SharedPassword();
                        sharedPassword.setUser(newUser);
                        sharedPassword.setMainPassword(password);
                        sharedPassword.setPassword(decPass);
                        this.encryptSharedPassword(sharedPassword,request);
                        sharedPasswordRepository.save(sharedPassword);
                        return ResponseEntity.ok("{\"status\":\"shared\"}");
                    }else return ResponseEntity.ok("{\"status\":\"authentication denied\"}");
                }else return ResponseEntity.ok("{\"status\":\"email not found\"}");
            }else return ResponseEntity.ok("{\"status\":\"password not found\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("{\"status\":\"error\"}");
        }
//        return null;
    }

    private SharedPassword encryptSharedPassword(SharedPassword sharedPassword,HttpServletRequest request) throws Exception {
        Key key = AESenc.generateKey(MD5Enc.calculateMD5(sharedPassword.getMainPassword().getPassword()));
        String encpPass = AESenc.encrypt(sharedPassword.getPassword(),key);
        sharedPassword.setPassword(encpPass);
        return sharedPassword;
    }

    private SharedPassword decryptSharedPassword(SharedPassword sharedPassword,HttpServletRequest request) throws Exception {
        Key key = AESenc.generateKey(MD5Enc.calculateMD5(sharedPassword.getMainPassword().getPassword()));
        String decPass = AESenc.decrypt(sharedPassword.getPassword(),key);
        sharedPassword.setPassword(decPass);
        return sharedPassword;
    }
}
