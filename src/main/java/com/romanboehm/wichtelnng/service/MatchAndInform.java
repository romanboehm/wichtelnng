package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.exception.TooFewParticipantsException;
import com.romanboehm.wichtelnng.model.Match;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.romanboehm.wichtelnng.model.entity.Event.DEADLINE_HAS_PASSED;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
@Service
public class MatchAndInform {

    private final EventRepository eventRepository;
    private final Matcher matcher;
    private final MatchMailSender matchMailSender;
    private final HostMailSender hostMailSender;

    @Scheduled(
            initialDelayString = "${com.romanboehm.wichtelnng.matchandinform.initial.delay.in.ms}",
            fixedRateString = "${com.romanboehm.wichtelnng.matchandinform.rate.in.ms}"
    )
    @Transactional
    public void matchAndInform() {
        // Yes, retrieving all events and filtering in the app is slower than a custom query. But the number of events
        // is low and it's easier than writing a custom query.
        List<Event> eventsWhereDeadlineHasPassed = eventRepository.findAll().stream()
                .filter(DEADLINE_HAS_PASSED)
                .collect(toList());
        for (Event event : eventsWhereDeadlineHasPassed) {
            try {
                List<Match> matches = matcher.match(new ArrayList<>(event.getParticipants()));
                matchMailSender.send(event, matches);
                log.info(
                        "Matched {} and informed about {}",
                        matches.stream().map(Match::toString).collect(joining(", ")),
                        event
                );
                eventRepository.delete(event);
                log.debug("Deleted {} because participants have been matched", event);
            } catch (TooFewParticipantsException e) {
                hostMailSender.send(event);
            }
        }
    }
}
