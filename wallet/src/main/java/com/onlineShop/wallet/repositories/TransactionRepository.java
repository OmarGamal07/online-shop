package com.onlineShop.wallet.repositories;

import com.onlineShop.wallet.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    List<Transaction> findAllByWalletId(Integer walletId);
}
