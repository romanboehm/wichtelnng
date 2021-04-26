package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static java.time.Instant.now;
import static java.util.stream.Collectors.joining;

@Slf4j
@RequiredArgsConstructor
@Service
public class MatchAndNotifyService {

    private final EventRepository eventRepository;
    private final ParticipantsMatcher participantsMatcher;
    private final MatchNotifier matchNotifier;
    private final LostEventNotifier informHost;

    @Scheduled(
            initialDelayString = "${com.romanboehm.wichtelnng.usecases.matchandnotify.initial.delay.in.ms}",
            fixedRateString = "${com.romanboehm.wichtelnng.usecases.matchandnotify.rate.in.ms}"
    )
    @Transactional
    public void matchAndInform() {
        List<Event> eventsWhereDeadlineHasPassed = eventRepository.findAllByDeadlineBefore(now());
        for (Event event : eventsWhereDeadlineHasPassed) {
            try {
                List<Match> matches = participantsMatcher.match(new ArrayList<>(event.getParticipants()));
                matchNotifier.send(event, matches);
                log.info(
                        "Matched {} and informed about {}",
                        matches.stream().map(Match::toString).collect(joining(", ")),
                        event
                );
            } catch (TooFewParticipants e) {
                informHost.send(event);
            }
            eventRepository.delete(event);
            log.debug("Deleted {}", event);
        }
    }
}
