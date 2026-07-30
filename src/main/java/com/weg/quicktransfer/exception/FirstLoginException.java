package com.weg.quicktransfer.exception;

public class FirstLoginException extends RuntimeException {
    public FirstLoginException(String message) {
        super("It is user's first login");
    }
}
