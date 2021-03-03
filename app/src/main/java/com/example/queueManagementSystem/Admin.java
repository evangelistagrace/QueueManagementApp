package com.example.queueManagementSystem;

import java.io.Serializable;

public class Admin implements  Serializable{
    int id;
    String username, password;

    public Admin(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword (String newPassword) {
        this.password = newPassword;
    }

    public int getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

