package com.romanboehm.wichtelnng.usecases.registerparticipant;

class RegistrationAttemptTooLateException extends Exception {
    RegistrationAttemptTooLateException(String message) {
        super(message);
    }
}
