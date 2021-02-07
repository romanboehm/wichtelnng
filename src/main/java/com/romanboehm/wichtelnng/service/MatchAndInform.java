package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.Match;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.repository.EventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class MatchAndInform {

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


    @Scheduled(cron = "@midnight")
    @Transactional(readOnly = true)
    public void matchAndInform() {
        List<Event> yesterdaysEvents = eventRepository.findAllByDeadline(
                LocalDate.now().minus(1, ChronoUnit.DAYS)
        );
        yesterdaysEvents.forEach(event -> {
            List<Match> matches = matcher.match(new ArrayList<>(event.getParticipants()));
            matchMailSender.send(event, matches);
        });
    }
}
