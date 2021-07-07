package com.moviesAPI.moviesAPI.controllers;


import com.moviesAPI.moviesAPI.entities.User;
import com.moviesAPI.moviesAPI.services.ProvideAutoLoginService;
import com.moviesAPI.moviesAPI.services.UserRegisterService;
import com.moviesAPI.moviesAPI.services.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller // This means that this class is a Controller
@Validated
@RequestMapping(path="/auth") // This means URL's start with /auth (after Application path)
public class UserController {
    @Autowired // This means to get the bean called characterRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRegisterService userService;

    @Autowired
    private ProvideAutoLoginService securityService;

    @Autowired
    private UserValidator userValidator;


    @PostMapping("/register")
    public String register(@RequestParam String userName,@RequestParam String password,@RequestParam String email/*,
                               @RequestParam BindingResult bindingResult*/ ) {
        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        user.setEmail(email);
        //userValidator.validate(user, bindingResult);

        /*if (bindingResult.hasErrors()) {
            return "register";
        }
        */
        userService.save(user);

        securityService.autoLogin(user.getUsername(), user.getPassword());

        return "redirect:/hello";
    }
    @GetMapping("/welcome")
    public String welcome() {
        return "welcome!!";
    }


}