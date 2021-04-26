package com.romanboehm.wichtelnng.usecases.viewabout;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path = "about")
public class AboutController {

    @GetMapping
    public ModelAndView getAbout() {
        return new ModelAndView("about");
    }
}
