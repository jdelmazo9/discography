package org.wyeworks.exception;

public class ProcessingPictureException extends RuntimeException {
    public ProcessingPictureException(String message, Exception e) {
        super(message, e);
    }
}
