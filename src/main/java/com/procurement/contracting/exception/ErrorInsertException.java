package com.procurement.contracting.exception;

public class ErrorInsertException extends RuntimeException {

    private final String message;

    public ErrorInsertException(final String message) {
        this.message = message;
    }
}
