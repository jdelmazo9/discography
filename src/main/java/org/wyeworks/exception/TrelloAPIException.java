package org.wyeworks.exception;

public class TrelloAPIException extends RuntimeException {
    public TrelloAPIException(String message) {
        super(message);
    }
}
