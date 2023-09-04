package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Event;
import com.romanboehm.wichtelnng.common.data.Event_;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.SpecHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
class NotifyService {

    private final Logger log = LoggerFactory.getLogger(NotifyService.class);

    private final MatchNotifier matchNotifier;
    private final LostEventNotifier lostEventNotifier;
    private final SessionFactory sessionFactory;

    NotifyService(EntityManagerFactory emf, MatchNotifier matchNotifier, LostEventNotifier lostEventNotifier) {
        this.sessionFactory = emf.unwrap(SessionFactory.class);
        this.matchNotifier = matchNotifier;
        this.lostEventNotifier = lostEventNotifier;
    }

    private static boolean hasEnoughParticipants(Event event) {
        return event.getParticipants().size() > 2;
    }

    void notify(UUID eventId) {
        try {
            Event event = sessionFactory.fromTransaction(session -> {
                var graph = session.createEntityGraph(Event.class);
                graph.addSubgraph(Event_.participants);
                var _event = session.find(Event.class, eventId, Map.of(SpecHints.HINT_SPEC_FETCH_GRAPH, graph));
                if (_event == null) {
                    throw new IllegalArgumentException("Failed to retrieve event %s".formatted(eventId));
                }
                session.remove(_event);
                return _event;
            });
            if (!hasEnoughParticipants(event)) {
                lostEventNotifier.send(LostMailEvent.from(event));
                log.debug("Notified host for event {}", eventId);
                return;
            }
            var matches = ParticipantsMatcher.match(event.getParticipants());
            var matchMailEvents = matches.stream()
                    .map(m -> MatchMailEvent.from(event, m.donor(), m.recipient()))
                    .toList();
            matchNotifier.send(matchMailEvents);
            log.debug("Notified participants for event {}", eventId);

        }
        catch (Exception e) {
            log.debug("Failed to notify host or participants for event {}", eventId, e);
            throw e;
        }
    }
}
