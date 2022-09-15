package org.wyeworks.exception;

public class ReadingFileException extends RuntimeException {
    public ReadingFileException(String message, Exception e) {
        super(message, e);
    }
}
