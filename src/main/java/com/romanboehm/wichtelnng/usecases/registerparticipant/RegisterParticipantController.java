package com.romanboehm.wichtelnng.usecases.registerparticipant;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Controller
class RegisterParticipantController {

    private final Logger log = LoggerFactory.getLogger(RegisterParticipantController.class);

    private final RegisterParticipantService service;

    RegisterParticipantController(RegisterParticipantService service) {
        this.service = service;
    }

    @GetMapping("/event/{eventId}/registration")
    ModelAndView get(@PathVariable UUID eventId) {
        try {
            var registerParticipant = service.getEvent(eventId);
            return new ModelAndView("registration", Map.of("registerParticipant", registerParticipant), OK);
        }
        catch (RegistrationAttemptTooLateException e) {
            return new ModelAndView("registrationattempttoolate", BAD_REQUEST);
        }
    }

    @PostMapping("/event/{eventId}/registration")
    ModelAndView post(
                      @PathVariable UUID eventId,
                      @ModelAttribute @Valid RegisterParticipant registerParticipant,
                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.debug(
                    "Failed to create {} because {}",
                    registerParticipant,
                    bindingResult.getAllErrors().stream()
                            .map(ObjectError::toString)
                            .collect(joining(", ")));
            return new ModelAndView("registration", BAD_REQUEST);
        }
        try {
            service.register(eventId, registerParticipant);
            log.info("Registered {} for {}", registerParticipant, eventId);
            return new ModelAndView(format("redirect:/event/%s/registration/finish", eventId));
        }
        catch (DuplicateParticipantException e) {
            return new ModelAndView("duplicateparticipant", BAD_REQUEST);
        }
    }
}
