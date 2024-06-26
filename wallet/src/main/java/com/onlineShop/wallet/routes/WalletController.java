package com.onlineShop.wallet.routes;

import com.onlineShop.wallet.entities.TransactionType;
import com.onlineShop.wallet.entities.User;
import com.onlineShop.wallet.entities.Wallet;
import com.onlineShop.wallet.repositories.UserRepository;
import com.onlineShop.wallet.services.JwtService;
import com.onlineShop.wallet.services.TransactionService;
import com.onlineShop.wallet.services.WalletService;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class WalletController {
    private  final WalletService walletService;

    private final TransactionService transactionService;

    @Autowired
    public WalletController(WalletService walletService,  TransactionService transactionService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
    }
    @Transactional
    @PostMapping("/deposit/{userId}/{amount}")
    public ResponseEntity<Map<String, Object>> deposit(@PathVariable Integer userId,
                                                       @PathVariable BigDecimal amount,
                                                       @NonNull HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Wallet wallet = walletService.deposit(userId, amount, request);

            transactionService.recordTransaction(wallet, amount, TransactionType.deposit);
            response.put("success", true);
            response.put("message", "Wallet deposited successfully");
            Map<String, Object> data = new HashMap<>();
            data.put("wallet", wallet);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Transactional
    @PostMapping("/withdraw/{userId}/{mount}")
    public ResponseEntity<Map<String, Object>> withdraw(@PathVariable Integer userId, @PathVariable BigDecimal mount, @NonNull HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Wallet wallet = walletService.withdraw(userId, mount,request);

            transactionService.recordTransaction(wallet, mount, TransactionType.withDrawal);

            response.put("success", true);
            response.put("message", "wallet withdraw successfully");
            Map<String, Object> data = new HashMap<>();
            data.put("wallet", wallet);
            response.put("data", data);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }

    }
    @GetMapping("wallet/{userId}/balance")
    public ResponseEntity<BigDecimal> getWalletBalance(@PathVariable Integer userId, @NonNull HttpServletRequest request) {
        BigDecimal balance = walletService.getWalletBalance(userId,request);
        return ResponseEntity.ok(balance);
    }
}
