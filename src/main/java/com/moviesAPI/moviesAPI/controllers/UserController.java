package com.moviesAPI.moviesAPI.controllers;


import com.moviesAPI.moviesAPI.entities.User;
import com.moviesAPI.moviesAPI.repositories.CharacterRepository;
import com.moviesAPI.moviesAPI.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/auth") // This means URL's start with /auth (after Application path)
public class UserController {
    @Autowired // This means to get the bean called characterRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostMapping("/login")
    public ResponseEntity login(@RequestParam("username") String username, @RequestParam("password") String password) {
        Optional<User> userFound = userRepository.findByUsername(username);
        if (!userFound.isPresent()){
            return new ResponseEntity<>(
                    "username doesn't exist",
                    HttpStatus.BAD_REQUEST);
        }
        User user = userFound.get();

        if (!passwordEncoder.matches(password, user.getPassword()) ){
            return new ResponseEntity<>(
                    "Incorrect password",
                    HttpStatus.BAD_REQUEST);
        }
        String token = getJWTToken(username);
        user.setToken(token);
        userRepository.save(user);
        return new ResponseEntity<>(
                "Successfully logged in. bearer token: "+ token,
                HttpStatus.OK);
    }
    @PostMapping("/register")
    public ResponseEntity register(@RequestParam("username") String username, @RequestParam("password") String password,
                                   @RequestParam("email") String email) {

        if (userRepository.findByEmail(email).isPresent()){
            return new ResponseEntity<>(
                    "Email already exists",
                    HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByUsername(username).isPresent()){
            return new ResponseEntity<>(
                    "Username already taken",
                    HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return new ResponseEntity<>(
                "Account created sucessfully",
                HttpStatus.OK);
    }
    private String getJWTToken(String username) {
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId("softtekJWT")
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512,
                        secretKey.getBytes()).compact();

        return "Bearer " + token;
    }
}