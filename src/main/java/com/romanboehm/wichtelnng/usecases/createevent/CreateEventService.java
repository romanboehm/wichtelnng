package com.romanboehm.wichtelnng.usecases.createevent;

import com.romanboehm.wichtelnng.data.Deadline;
import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.Host;
import com.romanboehm.wichtelnng.data.MonetaryAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
class CreateEventService {

    private final Logger log = LoggerFactory.getLogger(CreateEventService.class);

    private final CreateEventRepository repository;

    CreateEventService(CreateEventRepository repository) {
        this.repository = repository;
    }

    @Transactional
    UUID save(CreateEvent createEvent) {
        try {
            Event saved = repository.save(new Event()
                    .setTitle(createEvent.getTitle())
                    .setDescription(createEvent.getDescription())
                    .setDeadline(
                            new Deadline()
                                    .setLocalDateTime(
                                            LocalDateTime.of(
                                                    createEvent.getLocalDate(),
                                                    createEvent.getLocalTime()
                                            )
                                    )
                                    .setZoneId(createEvent.getTimezone().getId())
                    )
                    .setHost(
                            new Host()
                                    .setName(createEvent.getHostName())
                                    .setEmail(createEvent.getHostEmail())
                    )
                    .setMonetaryAmount(
                            new MonetaryAmount()
                                    .setNumber(createEvent.getNumber())
                                    .setCurrency(createEvent.getCurrency().getCurrencyCode())
                    ));
            log.debug("Saved {}", saved);
            return saved.getId();
        } catch (DataIntegrityViolationException e) {
            log.debug("Failed to save {}", createEvent, e);
            // Re-throw as `RuntimeException` to be handled by upstream by `ErrorController`
            throw new RuntimeException("Duplicate event");
        }
    }
}
