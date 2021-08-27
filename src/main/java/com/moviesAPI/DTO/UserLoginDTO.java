package com.moviesAPI.DTO;

import javax.persistence.Column;
import javax.validation.constraints.Size;

public class UserLoginDTO {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
