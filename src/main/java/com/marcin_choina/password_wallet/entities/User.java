package com.marcin_choina.password_wallet.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    public User(int id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(length=30, nullable = false, unique = true)
    private String login;

    @Column(length=50,nullable = false, unique = true)
    private String email;

    @Column(length=512,nullable = false)
    private String password_hash;

    @Column(length=20,nullable = true)
    private String salt;

    @Column(nullable = true)
    private Boolean is_password_kept_as_hash = true;

    @OneToMany(mappedBy = "user",orphanRemoval = true)
    private List<Password> passwords;

    @OneToMany(mappedBy = "user",orphanRemoval = true)
    private List<SharedPassword> sharedPasswords;

    @OneToMany(/*fetch = FetchType.EAGER,*/mappedBy = "user",orphanRemoval = true)
    private List<Login> logins;

    @OneToMany(/*fetch = FetchType.EAGER,*/mappedBy = "user",orphanRemoval = true)
    private List<Login_ip> login_ips;

    public User() {
        sharedPasswords = new ArrayList<>();
        passwords = new ArrayList<>();
        logins = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Boolean getIs_password_kept_as_hash() {
        return is_password_kept_as_hash;
    }

    public void setIs_password_kept_as_hash(Boolean is_password_kept_as_hash) {
        this.is_password_kept_as_hash = is_password_kept_as_hash;
    }

    public List<Password> getPasswords() {
        return passwords;
    }

    public void setPasswords(List<Password> passwords) {
        this.passwords = passwords;
    }

    public void removePassword(Password password){
        if(password != null) {
            this.passwords.remove(password);
            password.setUser(null);
        }
    }

    public void addPassword(Password password){
        if(password != null) {
            if(password.getUser() != this) password.setUser(this);
            this.passwords.add(password);
        }
    }

    public List<SharedPassword> getShared_passwords() {
        return this.sharedPasswords;
    }

    public void setShared_passwords(List<SharedPassword> sharedPasswords) {
        this.sharedPasswords = sharedPasswords;
    }

    public void removeSharedPassword(SharedPassword sharedPassword){
        if(sharedPassword != null) {
            this.sharedPasswords.remove(sharedPassword);
            sharedPassword.setUser(null);
        }
    }

    public void addSharedPassword(SharedPassword sharedPassword){
        if(sharedPassword != null) {
            if(sharedPassword.getUser() != this) sharedPassword.setUser(this);
            this.sharedPasswords.add(sharedPassword);
        }
    }

    public List<Login> getLogins() {
        return logins;
    }

    public void setLogins(List<Login> logins) {
        this.logins = logins;
    }

    public void removeLogin(Login login){
        if(login != null) {
            this.logins.remove(login);
            login.setUser(null);
        }
    }

    public void addLogin(Login login){
        if(login != null) {
            if(login.getUser() != this) login.setUser(this);
            logins.add(login);
        }
    }

    public List<Login_ip> getLogin_ips() {
        return login_ips;
    }

    public void setLogin_ips(List<Login_ip> loginIps) {
        this.login_ips = login_ips;
    }

    public void removeLogin_ips(Login_ip login_ip){
        if(login_ip != null) {
            this.login_ips.remove(login_ip);
            login_ip.setUser(null);
        }
    }

    public void addLogin_ips(Login_ip login_ip){
        if(login_ip != null) {
            if(login_ip.getUser() != this) login_ip.setUser(this);
            login_ips.add(login_ip);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password_hash='" + password_hash + '\'' +
                ", salt='" + salt + '\'' +
                ", is_password_kept_as_hash=" + is_password_kept_as_hash +
                '}';
    }
}
