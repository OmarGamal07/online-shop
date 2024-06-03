package com.onlineShop.wallet.routes;

import com.onlineShop.wallet.entities.Transaction;
import com.onlineShop.wallet.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    @GetMapping("transactions/{walletId}")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable Integer walletId) {
        List<Transaction> transactions = transactionService.getTransactionHistory(walletId);
        return ResponseEntity.ok(transactions);

    }
}
