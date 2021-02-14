package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.Match;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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


    @Scheduled(fixedRateString = "${com.romanboehm.wichtlenng.rate}")
    @Transactional(readOnly = true)
    public void matchAndInform() {
        List<Event> eventsWhereDeadlineHasPassed = eventRepository.findAllByLocalDateTimeBefore(LocalDateTime.now());
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
