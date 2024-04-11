package com.romanboehm.wichtelnng.usecases.viewabout;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping(path = "about")
class AboutController {

    private final String contactEmail;

    AboutController(@Value("${com.romanboehm.wichtelnng.mail.from}") String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @GetMapping
    ModelAndView getAbout() {
        return new ModelAndView("about", Map.of("contactEmail", contactEmail), OK);
    }
}
