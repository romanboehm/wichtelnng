package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.dto.EventCreation;
import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.model.entity.Participant;
import com.romanboehm.wichtelnng.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static java.time.Instant.now;

@Slf4j
@RequiredArgsConstructor
@Service
public class WichtelnService {

    private final RegistrationMailSender registrationMailSender;
    private final EventRepository eventRepository;
    private final LinkCreator linkCreator;

    @Transactional
    public UUID save(EventCreation eventCreation) {
        Event saved = eventRepository.save(Event.from(eventCreation));
        return saved.getId();
    }

    public URI createLink(UUID eventId) {
        return linkCreator.forId(eventId);
    }

    @Transactional(readOnly = true)
    public Optional<Event> getEvent(UUID eventId) {
        return eventRepository.findByIdAndDeadlineAfter(eventId, now());
    }

    @Transactional
    public void register(UUID eventId, ParticipantRegistration participantRegistration) {
        Optional<Event> possibleEvent = eventRepository.findById(eventId);
        if (possibleEvent.isEmpty()) {
            log.error("Failed to retrieve event {}", eventId);
            throw new IllegalArgumentException();
        }
        Event event = possibleEvent.get();
        eventRepository.save(event.addParticipant(Participant.from(participantRegistration)));
        log.info("Registered {}", participantRegistration);
        registrationMailSender.send(participantRegistration);
    }
}
