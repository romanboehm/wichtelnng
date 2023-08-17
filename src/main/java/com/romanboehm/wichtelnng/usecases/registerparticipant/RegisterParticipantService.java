package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.common.data.Event;
import com.romanboehm.wichtelnng.common.data.Participant;
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
    public EventForRegistration getEventOpenForRegistration(UUID eventId) throws RegistrationAttemptTooLateException {
        var event = getEventInternal(eventId);
        if (event.getDeadline().asInstant().isBefore(now())) {
            throw new RegistrationAttemptTooLateException("Failed to set up registration for event %s because its deadline has passed".formatted(eventId));
        }
        return EventForRegistration.from(event);
    }

    @Transactional(readOnly = true)
    public EventForRegistration getEvent(UUID eventId) {
        var event = getEventInternal(eventId);
        return EventForRegistration.from(event);
    }

    private Event getEventInternal(UUID eventId) {
        return repository.findById(eventId).orElseThrow(() -> {
            log.error("Failed to retrieve event {}", eventId);
            return new IllegalArgumentException();
        });
    }

    @Transactional
    public void register(UUID eventId, RegisterParticipant registerParticipant) throws DuplicateParticipantException {
        if (repository.eventContainsParticipant(eventId, registerParticipant.getParticipantName(), registerParticipant.getParticipantEmail())) {
            throw new DuplicateParticipantException("Failed to register because of duplicate %s".formatted(registerParticipant));
        }

        Optional<Event> possibleEvent = repository.findByIdWithParticipants(eventId);
        if (possibleEvent.isEmpty()) {
            log.error("Failed to retrieve event {}", eventId);
            throw new IllegalArgumentException();
        }

        var event = possibleEvent.get();
        repository.save(event.addParticipant(
                new Participant()
                        .setName(registerParticipant.getParticipantName())
                        .setEmail(registerParticipant.getParticipantEmail())));
        log.info("Registered {}", registerParticipant);
        participantNotifier.send(RegistrationMailEvent.from(event, registerParticipant));
    }
}
