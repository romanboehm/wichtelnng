package com.romanboehm.wichtelnng.model.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Host {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    public String getName() {
        return name;
    }

    public Host setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Host setEmail(String email) {
        this.email = email;
        return this;
    }
}
