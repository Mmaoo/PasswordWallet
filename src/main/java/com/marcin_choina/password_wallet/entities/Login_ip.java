package com.marcin_choina.password_wallet.entities;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Login_ip {
    public Login_ip(){
    }

    public Login_ip(User user) {
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    private String ip;
    private int successful_logins;
    private int failed_logins;
    private int last_failed_logins;
    private boolean permanent_blockade;
    private Date blockade_date;

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
            if (this.user != null) this.user.removeLogin_ips(this);
            this.user = user;
            user.addLogin_ips(this);
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getSuccessful_logins() {
        return successful_logins;
    }

    public void setSuccessful_logins(int successful_logins) {
        this.successful_logins = successful_logins;
    }

    public int getFailed_logins() {
        return failed_logins;
    }

    public void setFailed_logins(int failed_logins) {
        this.failed_logins = failed_logins;
    }

    public int getLast_failed_logins() {
        return last_failed_logins;
    }

    public void setLast_failed_logins(int last_failed_logins) {
        this.last_failed_logins = last_failed_logins;
    }

    public boolean isPermanent_blockade() {
        return permanent_blockade;
    }

    public void setPermanent_blockade(boolean permanent_blockade) {
        this.permanent_blockade = permanent_blockade;
    }

    public Date getBlockade_date() {
        return blockade_date;
    }

    public void setBlockade_date(Date blockade_date) {
        this.blockade_date = blockade_date;
    }
}
