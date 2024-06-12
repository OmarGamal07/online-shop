package com.onlineShop.wallet.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.onlineShop.wallet.entities.Permission.*;
import static com.onlineShop.wallet.entities.Role.ADMIN;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req->
                        req.requestMatchers("/auth/**")
                                .permitAll()
//                                .requestMatchers("http://localhost:8081/api/v1/products/**").hasAnyRole(ADMIN.name())
//                                .requestMatchers(GET, "/products/**").hasAnyAuthority(ADMIN_READ.name())
//                                .requestMatchers(POST, "/products/**").hasAnyAuthority(ADMIN_CREATE.name())
//                                .requestMatchers(PUT, "/products/**").hasAnyAuthority(ADMIN_UPDATE.name())
//                                .requestMatchers(DELETE, "/products/**").hasAnyAuthority(ADMIN_DELETE.name())
                                .anyRequest()
                                .authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();


    }
}
