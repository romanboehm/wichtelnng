package com.romanboehm.wichtelnng.usecases.finishregistration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class FinishRegistrationController {

    @GetMapping("/event/{eventId}/registration/finish")
    public ModelAndView get(@PathVariable UUID eventId) {
        return new ModelAndView("finishregistration");
    }
}
