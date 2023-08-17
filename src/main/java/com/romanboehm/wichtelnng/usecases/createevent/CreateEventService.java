package com.romanboehm.wichtelnng.usecases.createevent;

import com.romanboehm.wichtelnng.common.data.Deadline;
import com.romanboehm.wichtelnng.common.data.Event;
import com.romanboehm.wichtelnng.common.data.Host;
import com.romanboehm.wichtelnng.common.data.MonetaryAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.data.domain.ExampleMatcher.matching;

@Service
class CreateEventService {

    private final Logger log = LoggerFactory.getLogger(CreateEventService.class);

    private final CreateEventRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    CreateEventService(CreateEventRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public UUID save(CreateEvent createEvent) throws DuplicateEventException {
        var eventToCreate = eventFrom(createEvent);
        if (repository.findOne(Example.of(eventToCreate, matching().withIgnorePaths("id"))).isPresent()) {
            throw new DuplicateEventException("Failed to create event as it is a duplicate.");
        }

        var created = repository.save(eventToCreate);

        var eventId = created.getId();
        var eventCreatedEvent = new EventCreatedEvent(this, eventId, created.getDeadline().asInstant());
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
