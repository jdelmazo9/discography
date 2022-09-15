package org.wyeworks.exception;

public class ClientCredentialsException extends RuntimeException {
    public ClientCredentialsException(String message, Exception e) {
        super(message, e);
    }
}
