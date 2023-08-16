package com.romanboehm.wichtelnng.common.data;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity(name = "Participant")
@Table(name = "participant")
public class Participant {

    @EmbeddedId
    private ParticipantId id = new ParticipantId();

    @MapsId(ParticipantId_.EVENT_ID)
    @ManyToOne(fetch = FetchType.LAZY)
    private Event event;

    public ParticipantId getId() {
        return id;
    }

    public Participant setId(ParticipantId id) {
        this.id = id;
        return this;
    }

    public Event getEvent() {
        return this.event;
    }

    public Participant setEvent(Event event) {
        this.event = event;
        return this;
    }

    @Transient
    public String getName() {
        return this.id.getName();
    }

    @Transient
    public String getEmail() {
        return this.id.getEmail();
    }

    @Transient
    public Participant setName(String name) {
        this.id.setName(name);
        return this;
    }

    @Transient
    public Participant setEmail(String email) {
        this.id.setEmail(email);
        return this;
    }
}
