package io.hireroo.ecsite.exception;

public class InsufficientUserBalanceException extends Exception {
    public InsufficientUserBalanceException(String message) {
        super(message);
    }
}
