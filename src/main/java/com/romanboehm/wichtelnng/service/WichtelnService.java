package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.dto.EventCreation;
import com.romanboehm.wichtelnng.model.dto.EventDto;
import com.romanboehm.wichtelnng.model.dto.ParticipantDto;
import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.model.util.EventBuilder;
import com.romanboehm.wichtelnng.model.util.ParticipantBuilder;
import com.romanboehm.wichtelnng.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Service
public class WichtelnService {

    private final static Logger LOGGER = LoggerFactory.getLogger(WichtelnService.class);

    private final ParticipantsMatcher matcher;
    private final EventRepository eventRepository;
    private final LinkCreator linkCreator;

    public WichtelnService(ParticipantsMatcher matcher, EventRepository eventRepository, LinkCreator linkCreator) {
        this.matcher = matcher;
        this.eventRepository = eventRepository;
        this.linkCreator = linkCreator;
    }

    @Transactional
    public UUID save(EventCreation dto) {
        Event saved = eventRepository.save(EventBuilder.fromDto(dto.getEvent()));
        return saved.getId();
    }

    public URI createLink(UUID eventId) {
        return linkCreator.forId(eventId);
    }

    @Transactional(readOnly = true)
    public Optional<EventDto> getEvent(UUID eventId) {
        return eventRepository.findById(eventId).map(EventBuilder::fromEntity);
    }

    @Transactional
    public void register(UUID eventId, ParticipantRegistration participantRegistration) {
        Optional<Event> possibleEvent = eventRepository.findById(eventId);
        if (possibleEvent.isEmpty()) {
            LOGGER.error("Failed to retrieve event {}", eventId);
            throw new IllegalArgumentException();
        }
        ParticipantDto participantDto = participantRegistration.getParticipant();
        Event event = possibleEvent.get();
        eventRepository.save(event.addParticipant(ParticipantBuilder.fromDto(participantDto)));
    }
}
