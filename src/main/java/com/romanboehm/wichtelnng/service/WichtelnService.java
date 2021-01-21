package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.dto.EventDto;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.model.util.EventBuilder;
import com.romanboehm.wichtelnng.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Service
public class WichtelnService {

    private final ParticipantsMatcher matcher;
    private final EventRepository eventRepository;
    private final LinkCreator linkCreator;

    public WichtelnService(ParticipantsMatcher matcher, EventRepository eventRepository, LinkCreator linkCreator) {
        this.matcher = matcher;
        this.eventRepository = eventRepository;
        this.linkCreator = linkCreator;
    }

    @Transactional
    public URI save(EventDto dto) {
        Event saved = eventRepository.save(EventBuilder.from(dto));
        return linkCreator.forEvent(saved);
    }
}
