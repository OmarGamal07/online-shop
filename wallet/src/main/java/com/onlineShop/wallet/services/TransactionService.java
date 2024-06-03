package com.onlineShop.wallet.services;

import com.onlineShop.wallet.entities.Transaction;
import com.onlineShop.wallet.entities.TransactionType;
import com.onlineShop.wallet.entities.Wallet;
import com.onlineShop.wallet.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    @Autowired
    public TransactionService(TransactionRepository transactionRepository, WalletService walletService) {
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }
    @Transactional
    public void recordTransaction(Wallet wallet, BigDecimal amount, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setTimestamp(new Timestamp(System.currentTimeMillis()));

        transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionHistory(Integer walletId) {
        return transactionRepository.findAllByWalletId(walletId);
    }
}
