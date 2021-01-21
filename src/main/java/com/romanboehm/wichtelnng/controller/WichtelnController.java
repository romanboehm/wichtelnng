package com.romanboehm.wichtelnng.controller;

import com.romanboehm.wichtelnng.model.dto.EventDto;
import com.romanboehm.wichtelnng.service.WichtelnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.net.URI;
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
        return new ModelAndView("wichteln", Map.of("event", EventDto.withMinimalDefaults()), HttpStatus.OK);
    }

    @PostMapping("/save")
    public ModelAndView saveEvent(
            @ModelAttribute @Valid EventDto event,
            BindingResult bindingResult,
            ModelMap model
    ) {
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
        URI link = wichtelnService.save(event);
        LOGGER.info("Saved {}", event);
        model.addAttribute("link", link);
        return new ModelAndView("redirect:/link", model);
    }
}
