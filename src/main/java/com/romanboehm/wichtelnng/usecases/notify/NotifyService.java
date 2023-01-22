package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.data.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
class NotifyService {

    private final Logger log = LoggerFactory.getLogger(NotifyService.class);

    private final NotifyRepository repository;
    private final MatchNotifier matchNotifier;
    private final LostEventNotifier lostEventNotifier;

    NotifyService(NotifyRepository repository, MatchNotifier matchNotifier, LostEventNotifier lostEventNotifier) {
        this.repository = repository;
        this.matchNotifier = matchNotifier;
        this.lostEventNotifier = lostEventNotifier;
    }

    @Transactional
    void notify(UUID eventId) {
        var possibleEvent = repository.findById(eventId);
        if (possibleEvent.isEmpty()) {
            log.warn("Event {} not found for notification", eventId);
            return;
        }
        var event = possibleEvent.get();
        if (!hasEnoughParticipants(event)) {
            lostEventNotifier.send(LostMailEvent.from(event));
        } else {
            var matches = ParticipantsMatcher.match(event.getParticipants());
            var matchMailEvents = matches.stream()
                    .map(m -> MatchMailEvent.from(event, m.donor(), m.recipient()))
                    .toList();
            matchNotifier.send(matchMailEvents);
        }
        repository.delete(event);
    }

    private static boolean hasEnoughParticipants(Event event) {
        return event.getParticipants().size() > 2;
    }
}
