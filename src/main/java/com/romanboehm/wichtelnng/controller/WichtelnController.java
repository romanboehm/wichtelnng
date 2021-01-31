package com.romanboehm.wichtelnng.controller;

import com.romanboehm.wichtelnng.model.dto.EventCreationDto;
import com.romanboehm.wichtelnng.model.dto.EventDto;
import com.romanboehm.wichtelnng.model.dto.EventRegistrationDto;
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
                Map.of("eventCreationDto", new EventCreationDto(EventDto.withMinimalDefaults())),
                HttpStatus.OK
        );
    }

    @PostMapping("/save")
    public ModelAndView saveEvent(
            @ModelAttribute @Valid EventCreationDto eventCreationDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            LOGGER.debug(
                    "Failed to create {} because {}",
                    eventCreationDto,
                    bindingResult.getAllErrors().stream()
                            .map(ObjectError::toString)
                            .collect(Collectors.joining(", "))
            );
            return new ModelAndView("wichteln", HttpStatus.BAD_REQUEST);
        }
        UUID uuid = wichtelnService.save(eventCreationDto);
        LOGGER.info("Saved {}", eventCreationDto);
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
        EventRegistrationDto eventRegistrationDto = new EventRegistrationDto(eventDto);
        return new ModelAndView("registration", Map.of("eventRegistrationDto", eventRegistrationDto), HttpStatus.OK);
    }

    @PostMapping("/wichteln/{eventId}/register")
    public ModelAndView register(
            @PathVariable UUID eventId,
            @ModelAttribute @Valid EventRegistrationDto eventRegistrationDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            LOGGER.debug(
                    "Failed to create {} because {}",
                    eventRegistrationDto,
                    bindingResult.getAllErrors().stream()
                            .map(ObjectError::toString)
                            .collect(Collectors.joining(", "))
            );
            return new ModelAndView("registration", HttpStatus.BAD_REQUEST);
        }
        wichtelnService.register(eventId, eventRegistrationDto);
        LOGGER.info("Registered {} for {}", eventRegistrationDto, eventId);
        return new ModelAndView("redirect:/wichteln");
    }
}
