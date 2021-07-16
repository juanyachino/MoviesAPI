package com.moviesAPI.moviesAPI.controllers;



import com.moviesAPI.moviesAPI.repositories.UserRepository;
import com.moviesAPI.moviesAPI.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(path="/auth")
public class UserController {
    @Autowired // This means to get the bean called characterRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;
    @Autowired
    private UserServices userServices;
    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return userServices.login(username,password);
    }
    @PostMapping("/register")
    public String register(@RequestParam("username") String username, @RequestParam("password") String password,
                                   @RequestParam("email") String email) {
        return userServices.register(username,password,email);
    }
}