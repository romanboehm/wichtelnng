package com.romanboehm.wichtelnng.usecases.viewprivacy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path = "privacy")
public class PrivacyController {

    @GetMapping
    public ModelAndView getPrivacy() {
        return new ModelAndView("privacy");
    }
}
