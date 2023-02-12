package com.romanboehm.wichtelnng.data;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.domain.Persistable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Entity(name = "Event")
@Table(name = "event")
public class Event implements Persistable<UUID> {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 100)
    @NaturalId
    private String title;

    @Column(nullable = false, length = 1000)
    @NaturalId
    private String description;

    @Embedded
    @NaturalId
    private MonetaryAmount monetaryAmount;

    @Embedded
    @NaturalId
    private Deadline deadline;

    @Embedded
    @NaturalId
    private Host host;

    @OneToMany(mappedBy = "event", cascade = ALL, orphanRemoval = true, fetch = LAZY)
    private Set<Participant> participants = new HashSet<>();

    public Event() {
    }

    public Event addParticipant(Participant participant) {
        participants.add(participant);
        participant.setEvent(this);
        return this;
    }

    public Event removePartipant(Participant participant) {
        participants.remove(participant);
        participant.setEvent(null);
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
        Event event = (Event) o;
        // Cf. https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        // (Section "Fixing the entity identifier equals and hashCode")
        return (id != null) && id.equals(event.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // To reduce entity state checking
    @Override
    public boolean isNew() {
        return id == null;
    }

    public UUID getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public MonetaryAmount getMonetaryAmount() {
        return this.monetaryAmount;
    }

    public Deadline getDeadline() {
        return this.deadline;
    }

    public Host getHost() {
        return this.host;
    }

    public Set<Participant> getParticipants() {
        return this.participants;
    }

    public Event setId(UUID id) {
        this.id = id;
        return this;
    }

    public Event setTitle(String title) {
        this.title = title;
        return this;
    }

    public Event setDescription(String description) {
        this.description = description;
        return this;
    }

    public Event setMonetaryAmount(MonetaryAmount monetaryAmount) {
        this.monetaryAmount = monetaryAmount;
        return this;
    }

    public Event setDeadline(Deadline deadline) {
        this.deadline = deadline;
        return this;
    }

    public Event setHost(Host host) {
        this.host = host;
        return this;
    }

    public Event setParticipants(Set<Participant> participants) {
        this.participants = participants;
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", monetaryAmount=" + monetaryAmount +
                ", deadline=" + deadline +
                ", host=" + host +
                '}';
    }
}
