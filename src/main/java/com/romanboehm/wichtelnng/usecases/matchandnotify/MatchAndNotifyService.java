package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Event;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
class MatchAndNotifyService {

    private final MatchAndNotifyRepository repository;
    private final MatchNotifier matchNotifier;
    private final LostEventNotifier lostEventNotifier;

    MatchAndNotifyService(MatchAndNotifyRepository repository, MatchNotifier matchNotifier, LostEventNotifier lostEventNotifier) {
        this.repository = repository;
        this.matchNotifier = matchNotifier;
        this.lostEventNotifier = lostEventNotifier;
    }

    @Scheduled(
            initialDelayString = "${com.romanboehm.wichtelnng.usecases.matchandnotify.initial.delay.in.ms}",
            fixedRateString = "${com.romanboehm.wichtelnng.usecases.matchandnotify.rate.in.ms}"
    )
    @Transactional
    void matchAndNotify() {
        List<Event> eventsWhereDeadlineHasPassed = repository.findAllByDeadlineBefore(Instant.now());
        for (Event event : eventsWhereDeadlineHasPassed) {
            if (!hasEnoughParticipants(event)) {
                lostEventNotifier.send(LostMailEvent.from(event));
            } else {
                List<ParticipantsMatcher.Match> matches = ParticipantsMatcher.match(event.getParticipants());
                List<MatchMailEvent> matchMailEvents = matches.stream()
                        .map(m -> MatchMailEvent.from(event, m.donor(), m.recipient()))
                        .toList();
                matchNotifier.send(matchMailEvents);
            }

            repository.delete(event);
        }
    }

    private static boolean hasEnoughParticipants(Event event) {
        return event.getParticipants().size() > 2;
    }
}
