package com.BOA.TransactionService.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;


    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accountId")
    @JsonBackReference
    private Account account;

    private String transactionType;
    private Double amount;
    private String description;
    private LocalDateTime createdAt;

    @Version
    private Integer version;
// Version for optimistic locking

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }



}
