package com.romanboehm.wichtelnng.usecases.createevent;

public class DuplicateEventException extends Exception {
    public DuplicateEventException(String reason) {
        super(reason);
    }
}
