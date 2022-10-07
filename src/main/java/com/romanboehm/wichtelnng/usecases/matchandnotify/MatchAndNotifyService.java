package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
class MatchAndNotifyService {

    private final EventRepository eventRepository;
    private final MatchNotifier matchNotifier;
    private final LostEventNotifier lostEventNotifier;

    @Scheduled(
            initialDelayString = "${com.romanboehm.wichtelnng.usecases.matchandnotify.initial.delay.in.ms}",
            fixedRateString = "${com.romanboehm.wichtelnng.usecases.matchandnotify.rate.in.ms}"
    )
    @Transactional
    void matchAndNotify() {
        List<Event> eventsWhereDeadlineHasPassed = eventRepository.findAllByDeadlineBefore(Instant.now());
        for (Event event : eventsWhereDeadlineHasPassed) {
            if (!hasEnoughParticipants(event)) {
                lostEventNotifier.send(LostMailEvent.from(event));
            } else {
                List<ParticipantsMatcher.Match> matches = ParticipantsMatcher.match(event.getParticipants());
                List<MatchMailEvent> matchMailEvents = matches.stream()
                        .map(m -> MatchMailEvent.from(event, m.getDonor(), m.getRecipient()))
                        .toList();
                matchNotifier.send(matchMailEvents);
            }

            eventRepository.delete(event);
        }
    }

    private static boolean hasEnoughParticipants(Event event) {
        return event.getParticipants().size() > 2;
    }
}
