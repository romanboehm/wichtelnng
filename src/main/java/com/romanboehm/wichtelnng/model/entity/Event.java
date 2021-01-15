package com.romanboehm.wichtelnng.model.entity;

import org.springframework.data.domain.Persistable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "Event")
@Table(name = "event")
public class Event implements Persistable<UUID> {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Embedded
    private MonetaryAmount monetaryAmount;

    @Column(nullable = false)
    private LocalDateTime localDateTime;

    @Column(nullable = false, length = 100)
    private String place;

    @Embedded
    private Host host;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "event_participant",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private Set<Participant> participants = new HashSet<>();

    public Event() {
    }

    public UUID getId() {
        return id;
    }

    public Event setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Event setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Event setDescription(String description) {
        this.description = description;
        return this;
    }

    public MonetaryAmount getMonetaryAmount() {
        return monetaryAmount;
    }

    public Event setMonetaryAmount(MonetaryAmount monetaryAmount) {
        this.monetaryAmount = monetaryAmount;
        return this;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public Event setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
        return this;
    }

    public String getPlace() {
        return place;
    }

    public Event setPlace(String place) {
        this.place = place;
        return this;
    }

    public Host getHost() {
        return host;
    }

    public Event setHost(Host host) {
        this.host = host;
        return this;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public Event setParticipants(Set<Participant> participants) {
        this.participants = participants;
        return this;
    }

    public Event addParticipant(Participant participant) {
        participants.add(participant);
        participant.getEvents().add(this);
        return this;
    }

    public Event removePartipant(Participant participant) {
        participants.remove(participant);
        participant.getEvents().remove(this);
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
}
