package com.romanboehm.wichtelnng.usecases.createevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
public class CreateEventController {

    private final CreateEventService service;

    @GetMapping("/")
    public ModelAndView redirect() {
        return new ModelAndView("redirect:/event", OK);
    }

    @GetMapping("/event")
    public ModelAndView get() {
        return new ModelAndView("event", Map.of("createEvent", new CreateEvent()), OK);
    }

    @PostMapping("/event")
    public ModelAndView post(@ModelAttribute @Valid CreateEvent createEvent, BindingResult bindingResult) {
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
        UUID uuid = service.save(createEvent);
        log.info("Saved {}", createEvent);
        return new ModelAndView(format("redirect:/event/%s/link", uuid));
    }
}
