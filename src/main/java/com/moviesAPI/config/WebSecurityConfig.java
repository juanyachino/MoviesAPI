package com.moviesAPI.config;

import com.moviesAPI.security.JWTAuthorizationFilter;
import com.sendgrid.SendGrid;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final String SENDGRID_API_KEY = System.getenv("SENDGRID_API_KEY");
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        //http.csrf((csrf) -> csrf.ignoringAntMatchers("/auth/login","/auth/register"))
        http.csrf().disable()
                .addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/auth/login","/auth/register").permitAll()
                .anyRequest().authenticated();
    }
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SendGrid sendGrid(){
        return new SendGrid(SENDGRID_API_KEY);
    }

}
