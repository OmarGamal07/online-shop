package com.onlineShop.wallet.services;

import com.onlineShop.wallet.entities.User;
import com.onlineShop.wallet.entities.Wallet;
import com.onlineShop.wallet.exceptions.UserAlreadyExistsException;
import com.onlineShop.wallet.repositories.UserRepository;
import com.onlineShop.wallet.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final   JwtService jwtService ;

    @Autowired
    public UserService(UserRepository userRepository, WalletService walletService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public boolean userExistsById(Integer userId) {
        try {
            userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
            return true;
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }
    public Integer getIdFromToken(String token){
      return  jwtService.extractId(token);
    }
    @Transactional
    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }

        // Encode the password before saving the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user first without the wallet
        user.setWallet(null);
        User savedUser = userRepository.save(user);

        // Create wallet and link it to the saved user
        Wallet wallet = walletService.createWallet(savedUser);
        savedUser.setWallet(wallet);
        return userRepository.save(savedUser);
    }

//    public User authenticate(String email, String password) {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(email, password)
//            );
//        } catch (AuthenticationException e) {
//            throw new BadCredentialsException("Invalid email or password");
//        }
//
//        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//    }
public User authenticate(String email, String password) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new BadCredentialsException("Invalid password");
    }
    return user;
}

    public UserDetails findByEmail(String username) {
        return  userRepository.findByEmail(username)
                .map(user -> (UserDetails) user)
                .orElse(null);
    }
}
