package com.marcin_choina.password_wallet.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Password {

    public Password() {
        this.sharedPasswords = new ArrayList<>();
    }

    public Password(User user) {
        this.sharedPasswords = new ArrayList<>();
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(length=256, nullable = false)
    private String password;

    @Column(length = 256, nullable = true)
    private String web_address;

    @Column(length = 256, nullable = true)
    private String description;

    @Column(length = 30, nullable = true)
    private String login;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @OneToMany(mappedBy = "mainPassword",orphanRemoval = true)
    private List<SharedPassword> sharedPasswords;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWeb_address() {
        return web_address;
    }

    public void setWeb_address(String web_address) {
        this.web_address = web_address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if(user != null) {
            if (this.user != null) this.user.removePassword(this);
            this.user = user;
            user.addPassword(this);
        }
    }

    public List<SharedPassword> getSharedPasswords() {
        return sharedPasswords;
    }

    public void setSharedPasswords(List<SharedPassword> sharedPasswords) {
        this.sharedPasswords = sharedPasswords;
    }

    public void removeSharedPassword(SharedPassword sharedPassword){
        if(sharedPassword != null) {
            this.sharedPasswords.remove(sharedPassword);
            sharedPassword.setMainPassword(null);
        }
    }

    public void addSharedPassword(SharedPassword sharedPassword){
        if(sharedPassword != null) {
            if(sharedPassword.getMainPassword() != this) sharedPassword.setMainPassword(this);
            this.sharedPasswords.add(sharedPassword);
        }
    }

    @Override
    public String toString() {
        return "Password{" +
                "id=" + id +
                ", password='" + password + '\'' +
                '}';
    }
}
