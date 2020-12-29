package com.romanboehm.wichtelnng.model;

public class SendResult {
    private final String name;
    private final String email;
    private final boolean isSuccess;

    public static SendResult success(String name, String email) {
        return new SendResult(name, email, true);
    }

    public static SendResult failure(String name, String email) {
        return new SendResult(name, email, false);
    }

    private SendResult(String name, String email, boolean isSuccess) {
        this.name = name;
        this.email = email;
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return String.format("SendResult(name='%s', email='%s', isSuccess=%s)", name, email, isSuccess);
    }
}
