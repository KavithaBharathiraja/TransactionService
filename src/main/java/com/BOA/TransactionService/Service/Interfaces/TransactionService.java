package com.BOA.TransactionService.Service.Interfaces;

import com.BOA.TransactionService.Models.Transaction;
import java.util.List;

public interface TransactionService {

    // Method to create a transaction for an account
    Transaction createTransaction(Long accountId, Transaction transaction);

    // Method to fetch all transactions
    List<Transaction> getAllTransactions();

    // Method to retrieve all transactions for an account
    List<Transaction> getTransactionsByAccountId(Long accountId);

    // Method to get a transaction by its ID
    Transaction getTransactionById(Long transactionId);

    // Method to update an existing transaction
    Transaction updateTransaction(Transaction transaction);

    // Method to delete a transaction by its ID
    boolean deleteTransaction(Long transactionId);


}
