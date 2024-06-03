package com.onlineShop.wallet.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private BigDecimal balance;

    @OneToOne(mappedBy = "wallet",cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private User user;

//    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
//    private List<Transaction> transactions;
//    public void  addTransaction(Transaction transaction){
//        if (transactions == null) {
//
//            transactions=new ArrayList<>();
//        }
//        transactions.add(transaction);
//        transaction.setWallet(this);
//    }
}
