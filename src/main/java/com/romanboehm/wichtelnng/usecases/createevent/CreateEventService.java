package com.romanboehm.wichtelnng.usecases.createevent;

import com.romanboehm.wichtelnng.common.data.Deadline;
import com.romanboehm.wichtelnng.common.data.Event;
import com.romanboehm.wichtelnng.common.data.Event_;
import com.romanboehm.wichtelnng.common.data.Host;
import com.romanboehm.wichtelnng.common.data.MonetaryAmount;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
class CreateEventService {

    private final Logger log = LoggerFactory.getLogger(CreateEventService.class);

    private final Session session;
    private final ApplicationEventPublisher eventPublisher;

    CreateEventService(EntityManager em, ApplicationEventPublisher eventPublisher) {
        this.session = em.unwrap(Session.class);
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public UUID save(CreateEvent createEvent) throws DuplicateEventException {
        var event = eventFrom(createEvent);
        var existingEvent = session.byNaturalId(Event.class)
                .using(Event_.title, event.getTitle())
                .using(Event_.description, event.getDescription())
                .using(Event_.host, event.getHost())
                .using(Event_.deadline, event.getDeadline())
                .using(Event_.monetaryAmount, event.getMonetaryAmount())
                .load();
        if (existingEvent != null) {
            throw new DuplicateEventException("Failed to create event as it is a duplicate.");
        }

        session.persist(event);

        var eventId = event.getId();
        var eventCreatedEvent = new EventCreatedEvent(this, eventId, event.getDeadline().asInstant());
        eventPublisher.publishEvent(eventCreatedEvent);
        log.debug("Published {}", eventCreatedEvent);

        return eventId;
    }

    private static Event eventFrom(CreateEvent createEvent) {
        return new Event()
                .setTitle(createEvent.getTitle())
                .setDescription(createEvent.getDescription())
                .setDeadline(
                        new Deadline(LocalDateTime.of(
                                createEvent.getLocalDate(),
                                createEvent.getLocalTime()),
                                createEvent.getTimezone()))
                .setHost(
                        new Host(createEvent.getHostName(), createEvent.getHostEmail()))
                .setMonetaryAmount(
                        new MonetaryAmount(createEvent.getNumber(), createEvent.getCurrency()));
    }
}
