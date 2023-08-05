package com.romanboehm.wichtelnng.common.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

@Entity(name = "Participant")
@Table(name = "participant")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
    @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "hibernate_sequence", allocationSize = 1)
    private Long id;

    @NaturalId
    @NotBlank
    @Length(max = 100)
    private String name;

    @NaturalId
    @NotBlank
    @Length(max = 255)
    private String email;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public Participant() {
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

    public Long getId() {
        return this.id;
    }

    public Participant setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Participant setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return this.email;
    }

    public Participant setEmail(String email) {
        this.email = email;
        return this;
    }

    public Event getEvent() {
        return this.event;
    }

    public Participant setEvent(Event event) {
        this.event = event;
        return this;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
