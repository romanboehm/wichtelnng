package com.romanboehm.wichtelnng.usecases.registerparticipant;

class DuplicateParticipantException extends Exception {
    DuplicateParticipantException(String s) {
        super(s);
    }
}