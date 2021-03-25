package com.romanboehm.wichtelnng.controller;

import com.romanboehm.wichtelnng.model.dto.EventCreation;
import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.service.WichtelnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(path = {"/", "/wichteln"})
public class WichtelnController {

    private final WichtelnService wichtelnService;

    @GetMapping
    public ModelAndView getEvent() {
        return new ModelAndView("wichteln", Map.of("eventCreation", new EventCreation()), OK);
    }

    @PostMapping("/save")
    public ModelAndView saveEvent(@ModelAttribute @Valid EventCreation eventCreation, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.debug(
                    "Failed to create {} because {}",
                    eventCreation,
                    bindingResult.getAllErrors().stream()
                            .map(ObjectError::toString)
                            .collect(joining(", "))
            );
            return new ModelAndView("wichteln", BAD_REQUEST);
        }
        UUID uuid = wichtelnService.save(eventCreation);
        log.info("Saved {}", eventCreation);
        return new ModelAndView(format("redirect:/wichteln/%s/link", uuid));
    }

    @GetMapping("/wichteln/{eventId}/link")
    public ModelAndView getLink(@PathVariable UUID eventId) {
        URI link = wichtelnService.createLink(eventId);
        log.info("Created link {}", link);
        return new ModelAndView("result", Map.of("link", link), OK);
    }

    @GetMapping("/wichteln/{eventId}/register")
    public ModelAndView getEvent(@PathVariable UUID eventId) {
        Optional<Event> possibleEvent = wichtelnService.getEvent(eventId);
        if (possibleEvent.isEmpty()) {
            return new ModelAndView("redirect:/wichteln");
        }
        ParticipantRegistration participantRegistration = ParticipantRegistration.with(possibleEvent.get());
        return new ModelAndView("registration", Map.of("participantRegistration", participantRegistration), OK);
    }

    @PostMapping("/wichteln/{eventId}/register")
    public ModelAndView register(
            @PathVariable UUID eventId,
            @ModelAttribute @Valid ParticipantRegistration participantRegistration,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            log.debug(
                    "Failed to create {} because {}",
                    participantRegistration,
                    bindingResult.getAllErrors().stream()
                            .map(ObjectError::toString)
                            .collect(joining(", "))
            );
            return new ModelAndView("registration", BAD_REQUEST);
        }
        wichtelnService.register(eventId, participantRegistration);
        log.info("Registered {} for {}", participantRegistration, eventId);
        return new ModelAndView("redirect:/wichteln/afterregistration");
    }

    @GetMapping("wichteln/afterregistration")
    public ModelAndView afterRegistration() {
        return new ModelAndView("afterregistration");
    }
}
