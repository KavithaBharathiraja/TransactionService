package com.BOA.TransactionService.Controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    // Handle Bad Request (400)
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<String> handleBadRequest(Exception e) {
        logger.error("Bad Request: {}", e.getMessage());
        return new ResponseEntity<>("Bad Request: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

   /* // Handle Resource Not Found (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException e) {
        logger.error("Resource Not Found: {}", e.getMessage());
        return new ResponseEntity<>("Resource Not Found: " + e.getMessage(), HttpStatus.NOT_FOUND);
    }*/

    // Handle Internal Server Error (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleInternalServerError(Exception e) {
        logger.error("Internal Server Error: {}", e.getMessage());
        return new ResponseEntity<>("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle Method Argument Type Mismatch (400)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        logger.error("Invalid Argument Type: {}", e.getMessage());
        return new ResponseEntity<>("Invalid Argument Type: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
