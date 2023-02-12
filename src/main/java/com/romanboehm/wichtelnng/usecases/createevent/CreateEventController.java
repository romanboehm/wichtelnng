package com.romanboehm.wichtelnng.usecases.createevent;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Controller
class CreateEventController {

    private final Logger log = LoggerFactory.getLogger(CreateEventController.class);

    private final CreateEventService service;

    CreateEventController(CreateEventService service) {
        this.service = service;
    }

    @GetMapping("/")
    ModelAndView redirect() {
        return new ModelAndView("redirect:/event", HttpStatus.FOUND);
    }

    @GetMapping("/event")
    ModelAndView get() {
        return new ModelAndView("event", Map.of("createEvent", new CreateEvent()), OK);
    }

    @PostMapping("/event")
    ModelAndView post(@ModelAttribute @Valid CreateEvent createEvent, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.debug(
                    "Failed to create {} because {}",
                    createEvent,
                    bindingResult.getAllErrors().stream()
                            .map(ObjectError::toString)
                            .collect(joining(", "))
            );
            return new ModelAndView("event", BAD_REQUEST);
        }
        try {
            UUID uuid = service.save(createEvent);
            log.info("Saved {}", createEvent);
            return new ModelAndView(format("redirect:/event/%s/link", uuid));
        } catch (DuplicateEventException dee) {
            return new ModelAndView("duplicateevent", BAD_REQUEST);
        }
    }
}
