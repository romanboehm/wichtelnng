package com.romanboehm.wichtelnng.model.entity;

import com.romanboehm.wichtelnng.model.dto.EventCreation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
import java.util.function.Predicate;

import static java.time.Instant.now;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "participants")
@Entity(name = "Event")
@Table(name = "event")
public class Event implements Persistable<UUID> {

    // Relying on `Clock::systemDefaultZone()` is fine when not running within a container.
    // Otherwise, we need to a) mount /etc/timezone or b) pass the correct `ZoneId` here.
    public static final Predicate<Event> DEADLINE_HAS_PASSED = event -> event.getDeadline().isBefore(now());

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
    private Instant deadline;

    @Embedded
    private Host host;

    @OneToMany(mappedBy = "event", cascade = ALL, orphanRemoval = true, fetch = LAZY)
    private Set<Participant> participants = new HashSet<>();

    public static Event from(EventCreation eventCreation) {
        return new Event()
                .setTitle(eventCreation.getTitle())
                .setDescription(eventCreation.getDescription())
                .setDeadline(eventCreation.getInstant())
                .setHost(
                        new Host()
                                .setName(eventCreation.getHostName())
                                .setEmail(eventCreation.getHostEmail())
                )
                .setMonetaryAmount(
                        new MonetaryAmount()
                                .setNumber(eventCreation.getNumber())
                                .setCurrency(eventCreation.getCurrency().getCurrencyCode())
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
