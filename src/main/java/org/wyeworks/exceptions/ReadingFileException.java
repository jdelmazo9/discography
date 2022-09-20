package org.wyeworks.exceptions;

public class ReadingFileException extends RuntimeException {

    public ReadingFileException(String message) {
        super(message);
    }

    public ReadingFileException(String message, Exception e) {
        super(message, e);
    }
}
