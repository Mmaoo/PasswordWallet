package com.marcin_choina.password_wallet.entities;

import javax.persistence.*;

@Entity(name = "Shared_password")
public class SharedPassword {
    public SharedPassword(){}

//    public Shared_password(User user, Password mainPassword) {
//        this.user = user;
//        this.main_password = mainPassword;
//    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(length=256, nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "main_password",nullable = false)
    private Password mainPassword;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if(user != null) {
            if (this.user != null) this.user.removeSharedPassword(this);
            this.user = user;
            user.addSharedPassword(this);
        }
    }

    public Password getMainPassword() {
        return this.mainPassword;
    }

    public void setMainPassword(Password mainPassword) {
        if(mainPassword != null) {
            if (this.mainPassword != null) this.mainPassword.removeSharedPassword(this);
            this.mainPassword = mainPassword;
            mainPassword.addSharedPassword(this);
        }
    }
}
