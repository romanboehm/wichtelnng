package com.romanboehm.wichtelnng.usecases.registerparticipant;

import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
            var eventForRegistration = service.getEventOpenForRegistration(eventId);
            var registrationForm = new RegistrationForm();
            return new ModelAndView(
                    "registration",
                    Map.of(
                            "eventId", eventId,
                            "eventForRegistration", eventForRegistration,
                            "registrationForm", registrationForm),
                    OK);
        }
        catch (RegistrationAttemptTooLateException e) {
            log.info("Failed to register participant for event {} because too late", eventId);
            return new ModelAndView("registrationattempttoolate", BAD_REQUEST);
        }
    }

    @PostMapping("/event/{eventId}/registration")
    @Observed(name = "register.participant", contextualName = "registering-participant")
    ModelAndView post(
                      @PathVariable UUID eventId,
                      @ModelAttribute("registrationForm") @Valid RegistrationForm registrationForm,
                      BindingResult bindingResult,
                      Model model) {
        if (bindingResult.hasErrors()) {
            log.debug(
                    "Failed to create {} because {}",
                    registrationForm,
                    bindingResult.getAllErrors().stream()
                            .map(ObjectError::toString)
                            .collect(joining(", ")));
            model.addAttribute("eventForRegistration", service.getEvent(eventId));
            return new ModelAndView(
                    "registration",
                    BAD_REQUEST);
        }
        try {
            service.register(eventId, registrationForm);
            log.info("Registered {} for {}", registrationForm, eventId);
            return new ModelAndView(format("redirect:/event/%s/registration/finish", eventId));
        }
        catch (DuplicateParticipantException e) {
            log.info("Failed to register participant for event {} because duplicate", eventId);
            return new ModelAndView("duplicateparticipant", BAD_REQUEST);
        }
    }
}
