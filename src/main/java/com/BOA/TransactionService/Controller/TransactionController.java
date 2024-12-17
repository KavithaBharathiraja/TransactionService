package com.BOA.TransactionService.Controller;

import com.BOA.TransactionService.Models.Account;
import com.BOA.TransactionService.Models.Transaction;
import com.BOA.TransactionService.Service.Interfaces.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private static final Logger logger = LogManager.getLogger(TransactionController.class);

    private final TransactionService transactionService;
    private final RestTemplate restTemplate;

    @Autowired
    public TransactionController(TransactionService transactionService, RestTemplate restTemplate) {
        this.transactionService = transactionService;
        this.restTemplate = restTemplate;
    }

    // For creating a transaction with a full Transaction object in the body
    @PostMapping()
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        logger.info("Attempting to create transaction for accountId: {}", transaction.getAccount().getAccountId());

        // Call the external Account service using RestTemplate to fetch the account by accountId
        Account account = getAccountById(transaction.getAccount().getAccountId());

        // If account not found, log and return a BAD_REQUEST response with an error message
        if (account == null) {
            logger.error("Account not found for accountId: {}", transaction.getAccount().getAccountId());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Return 400 if account is not found
        }

        // Set the Account object in the Transaction
        transaction.setAccount(account);

        // Log the transaction before saving to ensure details are correct
        logger.info("Transaction details: {}", transaction);

        // Create the transaction with the associated account
        Transaction createdTransaction = transactionService.createTransaction(account.getAccountId(), transaction);

        // If the transaction is created successfully, return a CREATED response with the transaction
        if (createdTransaction != null) {
            logger.info("Transaction created successfully with transactionId: {}", createdTransaction.getTransactionId());
            return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
        } else {
            // Log and return a BAD_REQUEST response if transaction creation fails
            logger.error("Failed to create transaction for accountId: {}", transaction.getAccount().getAccountId());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // GET method for fetching a transaction by ID
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String transactionId) {
        logger.info("Fetching transaction details for transactionId: {}", transactionId);

        try {
            // Attempt to parse the transactionId to a Long value
            Long id = Long.parseLong(transactionId);

            Transaction transaction = transactionService.getTransactionById(id);

            if (transaction == null) {
                logger.error("Transaction with ID {} not found", id);
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // 404 Not Found if transaction not found
            }

            logger.info("Transaction details retrieved for transactionId: {}", id);
            return new ResponseEntity<>(transaction, HttpStatus.OK); // 200 OK with transaction data
        } catch (NumberFormatException e) {
            // If transactionId is not a valid number, return BAD_REQUEST
            logger.error("Invalid transactionId provided: {}. It must be a valid number.", transactionId);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        logger.info("Fetching all transactions.");

        // Fetch all transactions from the service layer
        List<Transaction> transactions = transactionService.getAllTransactions();

        if (transactions.isEmpty()) {
            logger.error("No transactions found.");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content if no transactions found
        }

        logger.info("Retrieved {} transactions.", transactions.size());
        return new ResponseEntity<>(transactions, HttpStatus.OK); // 200 OK with list of transactions
    }

    // Fetch account by ID using RestTemplate
    private Account getAccountById(Long accountId) {
        String url = "http://localhost:8082/accounts/" + accountId;  // URL for the external Account service

        try {
            // Making a GET request to fetch the account from external service
            ResponseEntity<Account> response = restTemplate.exchange(url, HttpMethod.GET, null, Account.class);

            // Check if the response is successful (HTTP 200 OK)
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Successfully fetched account details for accountId: {}", accountId);
                return response.getBody();  // Return the account if found
            } else {
                logger.error("Failed to fetch account details for accountId: {}. Status code: {}", accountId, response.getStatusCode());
                return null;  // Return null if the account is not found
            }
        } catch (Exception e) {
            // Log and return null if there is an exception (e.g., connection issues, timeouts)
            logger.error("Error fetching account details for accountId: {}. Exception: {}", accountId, e.getMessage());
            return null;
        }
    }
}
