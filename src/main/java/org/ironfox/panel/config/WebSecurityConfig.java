package org.ironfox.panel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    Environment environment;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/css/**", "/fonts/**", "/images/**",
                        "/js/**",
                        "/swf/**",
                        "/font-awesome.4.5.0/**",
                        "/font-awesome/**");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http

                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/", "/v1/*")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successForwardUrl("/")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
//        //todo get from database

        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username(environment.getProperty("ironfox.username"))
                        .password(environment.getProperty("ironfox.password"))
                        .roles("ADMIN")
                        .build();

        return new InMemoryUserDetailsManager(user);

    }
}