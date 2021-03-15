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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        // Relying on `Clock::systemDefaultZone()` is fine when not running within a container.
        // Otherwise, we need to a) mount /etc/timezone or b) pass the correct `ZoneId` here.
        List<Event> eventsWhereDeadlineHasPassed = eventRepository.findAllByZonedDateTimeBefore(ZonedDateTime.now());
        log.debug(
                "Found the following events whose participants are ready to be matched and informed: {}",
                eventsWhereDeadlineHasPassed.stream()
                        .map(e -> e.getId().toString())
                        .collect(Collectors.joining(", "))
        );
        for (Event event : eventsWhereDeadlineHasPassed) {
            try {
                List<Match> matches = matcher.match(new ArrayList<>(event.getParticipants()));
                matchMailSender.send(event, matches);
                log.info(
                        "Matched {} and informed about {}",
                        matches.stream().map(Match::toString).collect(Collectors.joining(", ")),
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
