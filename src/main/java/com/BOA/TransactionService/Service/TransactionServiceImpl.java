package com.BOA.TransactionService.Service;

import com.BOA.TransactionService.Models.Account;
import com.BOA.TransactionService.Models.Transaction;
import com.BOA.TransactionService.Repositories.TransactionRepository;
import com.BOA.TransactionService.Repositories.AccountRepository;
import com.BOA.TransactionService.Service.Interfaces.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private static final Logger logger = LogManager.getLogger(TransactionServiceImpl.class);

    // Constructor-based dependency injection
    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public Transaction createTransaction(Long accountId, Transaction transaction) {
        // Retrieve the Account entity based on the accountId
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account with ID " + accountId + " not found"));

        // Ensure the transaction is valid (you could add additional validation here)
        if (transaction.getAmount() == null || transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero.");
        }

        // Log transaction creation details
        logger.info("Creating transaction for account ID: {}", accountId);
        logger.info("Transaction Type: {}", transaction.getTransactionType());
        logger.info("Transaction Amount: {}", transaction.getAmount());

        // Set the Account object for the transaction (bi-directional relationship)
        transaction.setAccount(account);

        // Save the transaction, ensuring the persistence context manages it
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        // Fetch all transactions for the given accountId
        return transactionRepository.findByAccountAccountId(accountId);
    }

    @Override
    @Transactional
    public Transaction getTransactionById(Long transactionId) {
        // Retrieve transaction by its ID
        return transactionRepository.findById(transactionId).orElse(null);
    }

    @Override
    @Transactional
    public Transaction updateTransaction(Transaction transaction) {
        // Check if the transaction exists
        Transaction existingTransaction = transactionRepository.findById(transaction.getTransactionId()).orElse(null);
        if (existingTransaction == null) {
            logger.error("Transaction with ID {} not found for update", transaction.getTransactionId());
            return null;
        }

        // Log transaction update
        logger.info("Updating transaction ID: {}", transaction.getTransactionId());
        logger.info("Updated Amount: {}", transaction.getAmount());

        // Update the existing transaction entity
        existingTransaction.setTransactionType(transaction.getTransactionType());
        existingTransaction.setAmount(transaction.getAmount());
        existingTransaction.setDescription(transaction.getDescription());

        // Save the updated transaction
        return transactionRepository.save(existingTransaction);
    }

    // Implementing the method to fetch all transactions
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();  // Using the repository to fetch all transactions
    }

    @Override
    @Transactional
    public boolean deleteTransaction(Long transactionId) {
        // Check if the transaction exists
        if (!transactionRepository.existsById(transactionId)) {
            logger.error("Transaction with ID {} not found for deletion", transactionId);
            return false;
        }

        // Delete the transaction
        transactionRepository.deleteById(transactionId);
        logger.info("Deleted transaction with ID: {}", transactionId);
        return true;
    }
}
