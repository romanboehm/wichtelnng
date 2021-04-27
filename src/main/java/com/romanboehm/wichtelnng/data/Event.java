package com.romanboehm.wichtelnng.data;

import com.romanboehm.wichtelnng.usecases.createevent.CreateEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.domain.Persistable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "participants")
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

    @Column(nullable = false)
    @NaturalId
    private Instant deadline;

    @Embedded
    @NaturalId
    private Host host;

    @OneToMany(mappedBy = "event", cascade = ALL, orphanRemoval = true, fetch = LAZY)
    private Set<Participant> participants = new HashSet<>();

    public static Event from(CreateEvent createEvent) {
        return new Event()
                .setTitle(createEvent.getTitle())
                .setDescription(createEvent.getDescription())
                .setDeadline(createEvent.getInstant())
                .setHost(
                        new Host()
                                .setName(createEvent.getHostName())
                                .setEmail(createEvent.getHostEmail())
                )
                .setMonetaryAmount(
                        new MonetaryAmount()
                                .setNumber(createEvent.getNumber())
                                .setCurrency(createEvent.getCurrency().getCurrencyCode())
                );
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
}
