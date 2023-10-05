package com.romanboehm.wichtelnng.usecases.createevent;

import io.micrometer.observation.annotation.Observed;
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
    @Observed(name = "get.event", contextualName = "getting-event")
    ModelAndView get() {
        return new ModelAndView("event", Map.of("eventForm", new EventForm()), OK);
    }

    @PostMapping("/event")
    @Observed(name = "create.event", contextualName = "creating-event")
    ModelAndView post(@ModelAttribute("eventForm") @Valid EventForm eventForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.debug(
                    "Failed to create {} because {}",
                    eventForm,
                    bindingResult.getAllErrors().stream()
                            .map(ObjectError::toString)
                            .collect(joining(", ")));
            return new ModelAndView("event", BAD_REQUEST);
        }
        try {
            UUID uuid = service.createEvent(eventForm);
            log.info("Created {}", eventForm);
            return new ModelAndView(format("redirect:/event/%s/link", uuid));
        }
        catch (DuplicateEventException dee) {
            log.info("Failed to create {} because duplicate", eventForm);
            return new ModelAndView("duplicateevent", BAD_REQUEST);
        }
    }
}
