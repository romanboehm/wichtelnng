package com.romanboehm.wichtelnng.common.data;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Length;
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

    @NaturalId
    @NotBlank
    @Length(max = 100)
    private String title;

    @NaturalId
    @NotBlank
    @Length(max = 1000)
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

    public Event setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public Event setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public Event setDescription(String description) {
        this.description = description;
        return this;
    }

    public MonetaryAmount getMonetaryAmount() {
        return this.monetaryAmount;
    }

    public Event setMonetaryAmount(MonetaryAmount monetaryAmount) {
        this.monetaryAmount = monetaryAmount;
        return this;
    }

    public Deadline getDeadline() {
        return this.deadline;
    }

    public Event setDeadline(Deadline deadline) {
        this.deadline = deadline;
        return this;
    }

    public Host getHost() {
        return this.host;
    }

    public Event setHost(Host host) {
        this.host = host;
        return this;
    }

    public Set<Participant> getParticipants() {
        return this.participants;
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
