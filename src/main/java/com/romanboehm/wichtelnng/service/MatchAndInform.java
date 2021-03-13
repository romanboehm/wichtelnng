package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.Match;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MatchAndInform {

    private final static Logger LOGGER = LoggerFactory.getLogger(MatchAndInform.class);

    private final EventRepository eventRepository;
    private final Matcher matcher;
    private final MatchMailSender matchMailSender;

    public MatchAndInform(
            EventRepository eventRepository,
            Matcher matcher,
            MatchMailSender matchMailSender
    ) {
        this.eventRepository = eventRepository;
        this.matcher = matcher;
        this.matchMailSender = matchMailSender;
    }


    @Scheduled(initialDelayString = "${com.romanboehm.wichtlenng.matchandinform.initial.delay.in.ms}", fixedRateString = "${com.romanboehm.wichtlenng.matchandinform.rate.in.ms}")
    @Transactional(readOnly = true)
    public void matchAndInform() {
        // Relying on `Clock::systemDefaultZone()` is fine when not running within a container.
        // Otherwise, we need to a) mount /etc/timezone or b) pass the correct `ZoneId` here.
        List<Event> eventsWhereDeadlineHasPassed = eventRepository.findAllByZonedDateTimeBefore(ZonedDateTime.now());
        LOGGER.debug(
                "Found the following events whose participants are ready to be matched and informed: {}",
                eventsWhereDeadlineHasPassed.stream()
                        .map(e -> e.getId().toString())
                        .collect(Collectors.joining(", "))
        );
        eventsWhereDeadlineHasPassed.forEach(event -> {
            List<Match> matches = matcher.match(new ArrayList<>(event.getParticipants()));
            matchMailSender.send(event, matches);
        });
    }
}
