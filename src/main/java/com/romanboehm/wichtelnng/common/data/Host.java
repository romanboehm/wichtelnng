package com.romanboehm.wichtelnng.common.data;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

@Embeddable
public class Host {

    @NotBlank
    @Length(max = 100)
    private String name;

    @NotBlank
    @Length(max = 255)
    private String email;

    public Host() {
    }

    public String getName() {
        return this.name;
    }

    public Host setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return this.email;
    }

    public Host setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Host host = (Host) o;
        return Objects.equals(name, host.name) && Objects.equals(email, host.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }

    @Override
    public String toString() {
        return "Host{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
