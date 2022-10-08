package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.data.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
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
class RegisterParticipantController {

    private final RegisterParticipantService service;

    @GetMapping("/event/{eventId}/registration")
    ModelAndView get(@PathVariable UUID eventId) {
        Optional<Event> possibleEvent = service.getEvent(eventId);
        if (possibleEvent.isEmpty()) {
            return new ModelAndView("redirect:/event");
        }
        RegisterParticipant registerParticipant = RegisterParticipant.registerFor(possibleEvent.get());
        return new ModelAndView("registration", Map.of("registerParticipant", registerParticipant), OK);
    }

    @PostMapping("/event/{eventId}/registration")
    ModelAndView post(
            @PathVariable UUID eventId,
            @ModelAttribute @Valid RegisterParticipant registerParticipant,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            log.debug(
                    "Failed to create {} because {}",
                    registerParticipant,
                    bindingResult.getAllErrors().stream()
                            .map(ObjectError::toString)
                            .collect(joining(", "))
            );
            return new ModelAndView("registration", BAD_REQUEST);
        }
        service.register(eventId, registerParticipant);
        log.info("Registered {} for {}", registerParticipant, eventId);
        return new ModelAndView(format("redirect:/event/%s/registration/finish", eventId));
    }
}
