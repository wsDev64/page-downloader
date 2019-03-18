package com.oracle.techtask.exception;

public class SizeUndefined extends RuntimeException {

    public SizeUndefined() {
    }

    public SizeUndefined(String message) {
        super(message);
    }
}
