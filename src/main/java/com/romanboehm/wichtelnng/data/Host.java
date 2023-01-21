package com.romanboehm.wichtelnng.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Host {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    public Host() {
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public Host setName(String name) {
        this.name = name;
        return this;
    }

    public Host setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public String toString() {
        return "Host{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
