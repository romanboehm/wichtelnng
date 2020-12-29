package com.romanboehm.wichtelnng.controller;

import com.romanboehm.wichtelnng.model.Event;
import com.romanboehm.wichtelnng.model.Participant;
import com.romanboehm.wichtelnng.service.WichtelnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Map;
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
        return new ModelAndView("wichteln", Map.of("event", Event.withMinimalDefaults()), HttpStatus.OK);
    }

    @PostMapping("/preview")
    public ModelAndView previewEvent(@ModelAttribute @Valid Event event, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            LOGGER.debug(
                    "Failed to create {} because {}",
                    event,
                    bindingResult.getAllErrors().stream()
                            .map(ObjectError::toString)
                            .collect(Collectors.joining(", "))
            );
            return new ModelAndView("wichteln", HttpStatus.BAD_REQUEST);
        }
        LOGGER.info("Previewed {}", event);
        return new ModelAndView("wichteln", Map.of("preview", true), HttpStatus.OK);
    }

    @PostMapping("/save")
    public ModelAndView submitEvent(@ModelAttribute @Valid Event event, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            wichtelnService.save(event);
            LOGGER.info("Saved {}", event);
        }
        return new ModelAndView("redirect:/wichteln");
    }

    @PostMapping("/add")
    public ModelAndView addParticipant(@ModelAttribute Event event) {
        event.addParticipant(new Participant());
        LOGGER.debug("Added participant to {}", event);
        return new ModelAndView("wichteln", HttpStatus.OK);
    }

    @PostMapping("/remove/{index}")
    public ModelAndView removeParticipant(
            @PathVariable(name = "index") Integer participantIndex,
            @ModelAttribute Event event
    ) {
        event.removeParticipantNumber(participantIndex);
        LOGGER.debug("Removed participant {} from {}", participantIndex, event);

        return new ModelAndView("wichteln", HttpStatus.OK);
    }
}
