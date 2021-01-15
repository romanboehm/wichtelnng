package com.romanboehm.wichtelnng.model.entity;

import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "Participant")
@Table(name = "participant")
public class Participant {

    @Id
    @GeneratedValue
    private Long id;

    @NaturalId
    @Column(nullable = false, length = 100)
    private String name;

    @NaturalId
    @Column(nullable = false, length = 255)
    private String email;

    @ManyToMany(mappedBy = "participants")
    private Set<Event> events = new HashSet<>();

    public Participant() {
    }

    public Long getId() {
        return id;
    }

    public Participant setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Participant setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Participant setEmail(String email) {
        this.email = email;
        return this;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public Participant setEvents(Set<Event> events) {
        this.events = events;
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
        Participant that = (Participant) o;
        return Objects.equals(name, that.name) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }
}
