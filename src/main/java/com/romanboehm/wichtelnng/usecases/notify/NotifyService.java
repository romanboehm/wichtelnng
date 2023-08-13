package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Service
class NotifyService {

    private final Logger log = LoggerFactory.getLogger(NotifyService.class);

    private final NotifyRepository repository;
    private final MatchNotifier matchNotifier;
    private final LostEventNotifier lostEventNotifier;
    private final TransactionTemplate tx;

    NotifyService(NotifyRepository repository, MatchNotifier matchNotifier, LostEventNotifier lostEventNotifier, TransactionTemplate tx) {
        this.repository = repository;
        this.matchNotifier = matchNotifier;
        this.lostEventNotifier = lostEventNotifier;
        this.tx = tx;
    }

    private static boolean hasEnoughParticipants(Event event) {
        return event.getParticipants().size() > 2;
    }

    void notify(UUID eventId) {
        try {
            var event = tx.execute(__ -> {
                var _event = repository.findByIdWithParticipants(eventId).orElseThrow(() -> new IllegalArgumentException("Failed to find event %s".formatted(eventId)));
                repository.delete(_event);
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
