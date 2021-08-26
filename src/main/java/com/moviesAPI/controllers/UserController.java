package com.moviesAPI.controllers;


import com.moviesAPI.exceptions.InvalidDataException;
import com.moviesAPI.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(path="/auth")
public class UserController {
    @Autowired
    private UserServices userServices;
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return new ResponseEntity<>(
                userServices.login(username,password),
                HttpStatus.OK);
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam("username") String username, @RequestParam("password") String password,
                                   @RequestParam("email") String email) {
        try {
            return new ResponseEntity<>(
                    userServices.register(username,password,email),
                    HttpStatus.OK);
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }
    @RequestMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}