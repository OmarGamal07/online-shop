package com.onlineShop.wallet.services;

import com.onlineShop.wallet.entities.User;
import com.onlineShop.wallet.entities.Wallet;
import com.onlineShop.wallet.exceptions.InsufficientBalanceException;
import com.onlineShop.wallet.exceptions.UserAlreadyExistsException;
import com.onlineShop.wallet.repositories.UserRepository;
import com.onlineShop.wallet.repositories.WalletRepository;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final JwtService jwtService ;
    private  final UserRepository userRepository;
    @Autowired
    public WalletService(WalletRepository walletRepository, JwtService jwtService, UserRepository userRepository) {
        this.walletRepository = walletRepository;

        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }
    @Transactional
    public Wallet createWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(0.0));
        wallet.setUser(user);
        return walletRepository.save(wallet);
    }

    public Optional<Wallet> getWalletByUserId(Integer userId) {
        return walletRepository.findByUserId(userId);
    }
    public  Wallet getWalletById(Integer walletId){ return walletRepository.findById(walletId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));}

    public Wallet deposit(Integer walletId, BigDecimal amount, @NonNull HttpServletRequest request) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deposit amount must be greater than zero");
        }
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        Optional<User> userOptional=userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        User user = userOptional.get();
        Wallet userWallet = user.getWallet();

        if (userWallet == null || !userWallet.getId().equals(walletId)) {
            throw new AccessDeniedException("Access denied: User does not own this wallet");
        }
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(amount));
        return walletRepository.save(wallet);
    }

    public Wallet withdraw(Integer walletId, BigDecimal amount, @NonNull HttpServletRequest request) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "withdraw amount must be greater than zero");
        }
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        Optional<User> userOptional=userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        User user = userOptional.get();
        Wallet userWallet = user.getWallet();

        if (userWallet == null || !userWallet.getId().equals(walletId)) {
            throw new AccessDeniedException("Access denied: User does not own this wallet");
        }
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));
        if(amount.compareTo(wallet.getBalance()) > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance in the wallet");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        return walletRepository.save(wallet);
    }

}
