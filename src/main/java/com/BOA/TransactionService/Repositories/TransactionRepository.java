package com.BOA.TransactionService.Repositories;

import com.BOA.TransactionService.Models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountAccountId(Long accountId);
}
