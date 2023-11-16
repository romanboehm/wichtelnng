package com.romanboehm.wichtelnng.usecases.showlink;

import io.micrometer.observation.annotation.Observed;
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
class ShowLinkController {

    private final Logger log = LoggerFactory.getLogger(ShowLinkController.class);

    private final ShowLinkService service;

    ShowLinkController(ShowLinkService service) {
        this.service = service;
    }

    @GetMapping("/event/{eventId}/link")
    @Observed(name = "show.link", contextualName = "showing-link")
    ModelAndView showResult(@PathVariable UUID eventId) {
        URI link = service.createLink(eventId);
        log.info("Created link {}", link);
        return new ModelAndView("link", Map.of("link", link), OK);
    }
}
