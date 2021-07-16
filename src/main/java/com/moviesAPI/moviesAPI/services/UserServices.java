package com.moviesAPI.moviesAPI.services;

import com.moviesAPI.moviesAPI.entities.User;
import com.moviesAPI.moviesAPI.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServices {
    @Autowired // This means to get the bean called characterRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    EmailService sendGridEmailService;

    public String login (String username, String password){
        Optional<User> userFound = userRepository.findByUsername(username);
        if ((!userFound.isPresent()) || !passwordEncoder.matches(password, userFound.get().getPassword())) {
            return "Incorrect username and/or password. try again";
        }
        User user = userFound.get();
        String token = getJWTToken(username);
        user.setToken(token);
        userRepository.save(user);
        return "Successfully logged in! token-> "+ token;
    }
    public String register(String username, String password, String email) {
        if (userRepository.findByEmail(email).isPresent()){
            return "Email already exists!";
        }
        if (userRepository.findByUsername(username).isPresent()){
            return "Username already taken!";
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        sendWelcomeEmail(email,username);
        return "account created successfully!";
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
    private void sendWelcomeEmail(String email, String username) {
        sendGridEmailService.sendHTML("test@example.com",email,
                "Welcome to MoviesAPI!",
                "Hey "+username+"! Welcome to MoviesAPI. Have fun!");
    }
}