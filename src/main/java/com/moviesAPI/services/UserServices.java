package com.moviesAPI.services;

import com.moviesAPI.entities.User;

import com.moviesAPI.exceptions.InvalidDataException;
import com.moviesAPI.repositories.UserRepository;
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
        return token;
    }
    public String register(String username, String password, String email)
            throws InvalidDataException {
        if (username.length() < 5) {
            throw new InvalidDataException("the username must be at least 5 characters long!");
        }
        if (password.length() < 5) {
            throw new InvalidDataException("the password must be at least 5 characters long!");
        }
        if (userRepository.findByEmail(email).isPresent()){
            throw new InvalidDataException("the email already exists");
        }
        if (userRepository.findByUsername(username).isPresent()){
            throw new InvalidDataException("the username is already taken");
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
