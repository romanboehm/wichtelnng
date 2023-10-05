package com.romanboehm.wichtelnng.usecases.finishregistration;

import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
class FinishRegistrationController {

    @GetMapping("/event/{eventId}/registration/finish")
    @Observed(name = "finish.registration", contextualName = "finishing-registration")
    ModelAndView get(@PathVariable UUID eventId) {
        return new ModelAndView("finishregistration");
    }
}
