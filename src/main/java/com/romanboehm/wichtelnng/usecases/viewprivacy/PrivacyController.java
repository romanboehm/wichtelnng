package com.romanboehm.wichtelnng.usecases.viewprivacy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping(path = "privacy")
class PrivacyController {

    private final String contactEmail;

    PrivacyController(@Value("${com.romanboehm.wichtelnng.mail.from}") String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @GetMapping
    ModelAndView getPrivacy() {
        return new ModelAndView("privacy", Map.of("contactEmail", contactEmail), OK);
    }
}
