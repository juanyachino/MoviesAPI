package com.moviesAPI.DTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class UserRegisterDTO {
    @Size(min = 5,message = "username must be at least 5 characters long")
    private String username;

    @Size(min = 5,message = "username must be at least 5 characters long")
    private String password;

    @Email
    private String email;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
