package com.onlineShop.wallet.repositories;

import com.onlineShop.wallet.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet,Integer> {
    Optional<Wallet> findByUserId(Integer userId);
}
