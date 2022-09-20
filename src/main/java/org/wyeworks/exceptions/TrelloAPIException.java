package org.wyeworks.exceptions;

public class TrelloAPIException extends RuntimeException {

    public TrelloAPIException(String resource, String name, Integer status, String message) {
        super(
                "The " + resource + " post for: " + name + " request failed with status: \"" + status + "\" and message: \"" + message + "\""
        );
    }
}
