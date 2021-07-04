package com.moviesAPI.moviesAPI.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                //HTTP Basic authentication
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/movies/**").hasRole("USER")
                .antMatchers(HttpMethod.PUT, "/characters/**").hasRole("USER")
                .and()
                .csrf().disable()
                .formLogin().disable();
    }
}
