package com.moviesAPI.controllers;


import com.moviesAPI.entities.User;
import com.moviesAPI.exceptions.InvalidDataException;
import com.moviesAPI.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(path="/auth")
public class UserController {
    @Autowired
    private UserServices userServices;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody User user)  {
        return new ResponseEntity<>(
                userServices.login(user.getUsername(),user.getPassword()),
                HttpStatus.OK);
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user)  {
        try {
            return ResponseEntity.ok(userServices.register(user));
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}