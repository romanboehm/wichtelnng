package com.romanboehm.wichtelnng.usecases.showlink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@Controller
public class ShowLinkController {

    private final Logger log = LoggerFactory.getLogger(ShowLinkController.class);

    private final ShowLinkService service;

    public ShowLinkController(ShowLinkService service) {
        this.service = service;
    }

    @GetMapping("/event/{eventId}/link")
    public ModelAndView showResult(@PathVariable UUID eventId) {
        URI link = service.createLink(eventId);
        log.info("Created link {}", link);
        return new ModelAndView("link", Map.of("link", link), OK);
    }
}
