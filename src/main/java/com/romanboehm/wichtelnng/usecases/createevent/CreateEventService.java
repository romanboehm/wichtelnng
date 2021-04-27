package com.romanboehm.wichtelnng.usecases.createevent;

import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CreateEventService {

    private final EventRepository eventRepository;

    @Transactional
    UUID save(CreateEvent createEvent) {
        try {
            Event saved = eventRepository.save(Event.from(createEvent));
            log.debug("Saved {}", saved);
            return saved.getId();
        } catch (DataIntegrityViolationException e) {
            log.debug("Failed to save {}", createEvent, e);
            // Re-throw as `RuntimeException` to be handled by upstream by `ErrorController`
            throw new RuntimeException("Duplicate event");
        }
    }
}
