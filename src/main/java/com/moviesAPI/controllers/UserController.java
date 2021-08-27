package com.moviesAPI.controllers;


import com.moviesAPI.DTO.UserLoginDTO;
import com.moviesAPI.DTO.UserRegisterDTO;
import com.moviesAPI.exceptions.InvalidDataException;
import com.moviesAPI.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(path="/auth")
public class UserController {
    @Autowired
    private UserServices userServices;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO userDTO)  {
        return new ResponseEntity<>(
                userServices.login(userDTO),
                HttpStatus.OK);
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegisterDTO userDTO)  {
        try {
            return ResponseEntity.ok(userServices.register(userDTO));
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    e.getMessage(),
                    HttpStatus.OK);
        }
    }
}