package com.romanboehm.wichtelnng.usecases.matchandnotify;

public class TooFewParticipants extends Exception {
    public TooFewParticipants(String s) {
        super(s);
    }
}
