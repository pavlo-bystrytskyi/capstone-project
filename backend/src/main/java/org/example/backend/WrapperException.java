package org.example.backend;

public class WrapperException extends RuntimeException {

    public WrapperException(Exception exception) {
        super(exception);
    }
}
