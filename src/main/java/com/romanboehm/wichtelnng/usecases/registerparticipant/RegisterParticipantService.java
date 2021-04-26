package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.EventRepository;
import com.romanboehm.wichtelnng.data.Participant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static java.time.Instant.now;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegisterParticipantService {

    private final RegisterParticipantNotifier participantNotifier;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    Optional<Event> getEvent(UUID eventId) {
        return eventRepository.findByIdAndDeadlineAfter(eventId, now());
    }

    @Transactional
    void register(UUID eventId, RegisterParticipant registerParticipant) {
        Optional<Event> possibleEvent = eventRepository.findById(eventId);
        if (possibleEvent.isEmpty()) {
            log.error("Failed to retrieve event {}", eventId);
            throw new IllegalArgumentException();
        }
        Event event = possibleEvent.get();
        event.addParticipant(Participant.from(registerParticipant));
        eventRepository.save(event);
        log.info("Registered {}", registerParticipant);
        participantNotifier.send(registerParticipant);
    }
}
