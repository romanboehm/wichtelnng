package com.romanboehm.wichtelnng.controller;

import com.romanboehm.wichtelnng.model.dto.EventCreation;
import com.romanboehm.wichtelnng.model.dto.EventDto;
import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import com.romanboehm.wichtelnng.service.WichtelnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = {"/", "/wichteln"})
public class WichtelnController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WichtelnController.class);
    private final WichtelnService wichtelnService;

    public WichtelnController(WichtelnService wichtelnService) {
        this.wichtelnService = wichtelnService;
    }

    @GetMapping
    public ModelAndView getEvent() {
        return new ModelAndView(
                "wichteln",
                Map.of("eventCreation", EventCreation.withMinimalDefaults()),
                HttpStatus.OK
        );
    }

    @PostMapping("/save")
    public ModelAndView saveEvent(
            @ModelAttribute @Valid EventCreation eventCreation,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            LOGGER.debug(
                    "Failed to create {} because {}",
                    eventCreation,
                    bindingResult.getAllErrors().stream()
                            .map(ObjectError::toString)
                            .collect(Collectors.joining(", "))
            );
            return new ModelAndView("wichteln", HttpStatus.BAD_REQUEST);
        }
        UUID uuid = wichtelnService.save(eventCreation);
        LOGGER.info("Saved {}", eventCreation);
        return new ModelAndView(String.format("redirect:/wichteln/%s/link", uuid));
    }

    @GetMapping("/wichteln/{eventId}/link")
    public ModelAndView getLink(@PathVariable UUID eventId) {
        URI link = wichtelnService.createLink(eventId);
        LOGGER.info("Created link {}", link);
        return new ModelAndView("result", Map.of("link", link), HttpStatus.OK);
    }

    @GetMapping("/wichteln/{eventId}/register")
    public ModelAndView getEvent(@PathVariable UUID eventId) {
        Optional<EventDto> possibleEventDto = wichtelnService.getEvent(eventId);
        if (possibleEventDto.isEmpty()) {
            return new ModelAndView("redirect:/wichteln");
        }
        EventDto eventDto = possibleEventDto.get();
        ParticipantRegistration participantRegistration = new ParticipantRegistration(eventDto);
        return new ModelAndView(
                "registration",
                Map.of("participantRegistration", participantRegistration),
                HttpStatus.OK
        );
    }

    @PostMapping("/wichteln/{eventId}/register")
    public ModelAndView register(
            @PathVariable UUID eventId,
            @ModelAttribute @Valid ParticipantRegistration participantRegistration,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            LOGGER.debug(
                    "Failed to create {} because {}",
                    participantRegistration,
                    bindingResult.getAllErrors().stream()
                            .map(ObjectError::toString)
                            .collect(Collectors.joining(", "))
            );
            return new ModelAndView("registration", HttpStatus.BAD_REQUEST);
        }
        wichtelnService.register(eventId, participantRegistration);
        LOGGER.info("Registered {} for {}", participantRegistration, eventId);
        return new ModelAndView("redirect:/wichteln/afterregistration");
    }

    @GetMapping("wichteln/afterregistration")
    public ModelAndView afterRegistration() {
        return new ModelAndView("afterregistration");
    }
}
