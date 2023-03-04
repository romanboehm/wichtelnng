package com.romanboehm.wichtelnng.usecases.createevent;

class DuplicateEventException extends Exception {
    DuplicateEventException(String reason) {
        super(reason);
    }
}
