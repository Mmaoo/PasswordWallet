package com.marcin_choina.password_wallet.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
public class Login {

    public Login(){
    }

    public Login(User user) {
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    private Date date;

    private boolean success;

    private String ip;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if(user != null) {
            if (this.user != null) this.user.removeLogin(this);
            this.user = user;
            user.addLogin(this);
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "Login{" +
                "id=" + id +
                ", date=" + date +
                ", success=" + success +
                '}';
    }
}
