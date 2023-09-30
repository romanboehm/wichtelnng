package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.common.data.Event;
import com.romanboehm.wichtelnng.common.data.Event_;
import com.romanboehm.wichtelnng.common.data.Participant;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.jpa.SpecHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static java.time.Instant.now;

@Service
class RegisterParticipantService {

    private final Logger log = LoggerFactory.getLogger(RegisterParticipantService.class);

    private final RegisterParticipantMailSender participantNotifier;
    private final Session session;

    RegisterParticipantService(EntityManager em, RegisterParticipantMailSender participantNotifier) {
        this.participantNotifier = participantNotifier;
        this.session = em.unwrap(Session.class);
    }

    @Transactional(readOnly = true)
    public EventForRegistration getEventOpenForRegistration(UUID eventId) throws RegistrationAttemptTooLateException {
        var event = session.find(Event.class, eventId);
        if (event == null) {
            throw new IllegalArgumentException("Failed to retrieve event %s".formatted(eventId));
        }
        if (event.getDeadline().asInstant().isBefore(now())) {
            throw new RegistrationAttemptTooLateException("Failed to set up registration for event %s because its deadline has passed".formatted(eventId));
        }
        return EventForRegistration.from(event);
    }

    @Transactional(readOnly = true)
    public EventForRegistration getEvent(UUID eventId) {
        var event = session.find(Event.class, eventId);
        if (event == null) {
            throw new IllegalArgumentException("Failed to retrieve event %s".formatted(eventId));
        }
        return EventForRegistration.from(event);
    }

    @Transactional
    public void register(UUID eventId, RegistrationForm registrationForm) throws DuplicateParticipantException {
        if (eventContainsParticipant(eventId, registrationForm)) {
            throw new DuplicateParticipantException("Failed to register because of duplicate %s".formatted(registrationForm));
        }

        var graph = session.createEntityGraph(Event.class);
        graph.addSubgraph(Event_.participants);
        var event = session.find(Event.class, eventId, Map.of(SpecHints.HINT_SPEC_FETCH_GRAPH, graph));
        if (event == null) {
            throw new IllegalArgumentException("Failed to retrieve event %s".formatted(eventId));
        }

        session.persist(event.addParticipant(
                new Participant()
                        .setName(registrationForm.getParticipantName())
                        .setEmail(registrationForm.getParticipantEmail())));
        log.info("Registered {}", registrationForm);
        participantNotifier.send(MailToParticipantDataForRegistration.from(event, registrationForm));
    }

    private Boolean eventContainsParticipant(UUID eventId, RegistrationForm registrationForm) {
        return session.createNativeQuery("""
                select
                    (case when count(p.*) > 0 then true else false end)
                from participant p
                where p.event_id = :eventId and p.name = :pName and p.email = :pEmail
                """,
                Boolean.class)
                .setParameter("eventId", eventId)
                .setParameter("pName", registrationForm.getParticipantName())
                .setParameter("pEmail", registrationForm.getParticipantEmail())
                .getSingleResult();
    }
}
