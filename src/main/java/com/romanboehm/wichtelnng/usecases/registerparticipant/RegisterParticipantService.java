package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static java.time.Instant.now;

@Service
class RegisterParticipantService {

    private final Logger log = LoggerFactory.getLogger(RegisterParticipantService.class);

    private final RegisterParticipantNotifier participantNotifier;
    private final RegisterParticipantRepository repository;

    RegisterParticipantService(RegisterParticipantNotifier participantNotifier, RegisterParticipantRepository repository) {
        this.participantNotifier = participantNotifier;
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    Optional<Event> getEvent(UUID eventId) {
        return repository.findByIdAndDeadlineAfter(eventId, now());
    }

    @Transactional
    void register(UUID eventId, RegisterParticipant registerParticipant) {
        Optional<Event> possibleEvent = repository.findById(eventId);
        if (possibleEvent.isEmpty()) {
            log.error("Failed to retrieve event {}", eventId);
            throw new IllegalArgumentException();
        }
        Event event = possibleEvent.get();
        event.addParticipant(
                new Participant()
                        .setName(registerParticipant.getParticipantName())
                        .setEmail(registerParticipant.getParticipantEmail()));
        repository.save(event);
        log.info("Registered {}", registerParticipant);
        participantNotifier.send(RegistrationMailEvent.from(registerParticipant));
    }
}
