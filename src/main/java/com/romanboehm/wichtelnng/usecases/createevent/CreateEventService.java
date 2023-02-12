package com.romanboehm.wichtelnng.usecases.createevent;

import com.romanboehm.wichtelnng.data.Deadline;
import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.Host;
import com.romanboehm.wichtelnng.data.MonetaryAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
class CreateEventService {

    private final Logger log = LoggerFactory.getLogger(CreateEventService.class);

    private final CreateEventRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionTemplate tx;

    CreateEventService(CreateEventRepository repository, ApplicationEventPublisher eventPublisher, TransactionTemplate tx) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.tx = tx;
    }

    UUID save(CreateEvent createEvent) throws DuplicateEventException {
        Event saved;
        try {
            saved = tx.execute(status -> repository.save(eventFrom(createEvent)));
        }
        catch (DataIntegrityViolationException e) {
            log.warn("Failed to create event", e);
            throw new DuplicateEventException("Failed to create event as it is a duplicate.");
        }

        var eventId = saved.getId();

        var eventCreatedEvent = new EventCreatedEvent(this, eventId, createEvent.getInstant());
        eventPublisher.publishEvent(eventCreatedEvent);
        log.debug("Published {}", eventCreatedEvent);

        return eventId;
    }

    private static Event eventFrom(CreateEvent createEvent) {
        return new Event()
                .setTitle(createEvent.getTitle())
                .setDescription(createEvent.getDescription())
                .setDeadline(
                        new Deadline()
                                .setLocalDateTime(
                                        LocalDateTime.of(
                                                createEvent.getLocalDate(),
                                                createEvent.getLocalTime()))
                                .setZoneId(createEvent.getTimezone().getId()))
                .setHost(
                        new Host()
                                .setName(createEvent.getHostName())
                                .setEmail(createEvent.getHostEmail()))
                .setMonetaryAmount(
                        new MonetaryAmount()
                                .setNumber(createEvent.getNumber())
                                .setCurrency(createEvent.getCurrency().getCurrencyCode()));
    }
}
