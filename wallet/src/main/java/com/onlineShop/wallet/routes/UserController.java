package com.onlineShop.wallet.routes;

import com.onlineShop.wallet.entities.User;
import com.onlineShop.wallet.exceptions.UserAlreadyExistsException;
import com.onlineShop.wallet.services.JwtService;
import com.onlineShop.wallet.services.LoginRequest;
import com.onlineShop.wallet.services.UserRegistrationRequest;
import com.onlineShop.wallet.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    private  final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }
    public static class TokenRequest {
        public String token;
    }
    @GetMapping("/auth/getUserIdByToken/{token}")
    public Integer getUserIdByToken(@PathVariable("token") String token) {
        return userService.getIdFromToken(token);
    }
    @GetMapping("/auth/users/email/{username}")
    public UserDetails getUserByUsername(@PathVariable String username) {
        return userService.findByEmail(username);
    }
    @Transactional
    @PostMapping("/auth/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = new User();
            user.setFirstname(registrationRequest.getFirstname());
            user.setLastname(registrationRequest.getLastname());
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(registrationRequest.getPassword());
            user.setRole(registrationRequest.getRole());

            userService.register(user);
            String jwtToken = jwtService.generateToken(user);
            response.put("success", true);
            response.put("message", "User registered successfully");
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("token", jwtToken);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (UserAlreadyExistsException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }
    @Transactional
    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> authenticate( @RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
            String jwtToken = jwtService.generateToken(user);
            response.put("success", true);
            response.put("message", "User logged in successfully");
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("token", jwtToken);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("/users/{userId}")
    public Boolean getUserById(@PathVariable Integer userId){
        return userService.userExistsById(userId);
    }
}
