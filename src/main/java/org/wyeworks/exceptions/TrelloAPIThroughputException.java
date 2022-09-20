package org.wyeworks.exceptions;

public class TrelloAPIThroughputException extends TrelloAPIException {
    public TrelloAPIThroughputException(String resource, String name, Integer status, String message) {
        super(resource, name, status, message);
    }
}
