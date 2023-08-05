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
    public Event getEvent(UUID eventId) throws RegistrationAttemptTooLateException {
        var possibleEvent = repository.findById(eventId);
        if (possibleEvent.isEmpty()) {
            log.error("Failed to retrieve event {}", eventId);
            throw new IllegalArgumentException();
        }
        if (possibleEvent.get().getDeadline().getInstant().isBefore(now())) {
            throw new RegistrationAttemptTooLateException("Failed to set up registration for event %s because its deadline has passed".formatted(eventId));
        }
        return possibleEvent.get();
    }

    @Transactional
    public void register(UUID eventId, RegisterParticipant registerParticipant) throws DuplicateParticipantException {
        Optional<Event> possibleEvent = repository.findById(eventId);
        if (possibleEvent.isEmpty()) {
            log.error("Failed to retrieve event {}", eventId);
            throw new IllegalArgumentException();
        }
        var eventBefore = possibleEvent.get();
        var participantsCountBefore = eventBefore.getParticipants().size();
        eventBefore.addParticipant(
                new Participant()
                        .setName(registerParticipant.getParticipantName())
                        .setEmail(registerParticipant.getParticipantEmail()));
        var eventAfter = repository.save(eventBefore);
        if (eventAfter.getParticipants().size() == participantsCountBefore) {
            throw new DuplicateParticipantException("Failed to register because of duplicate %s".formatted(registerParticipant));
        }
        log.info("Registered {}", registerParticipant);
        participantNotifier.send(RegistrationMailEvent.from(registerParticipant));
    }
}
