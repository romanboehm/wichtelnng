package com.romanboehm.wichtelnng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.util.Map;

@Controller
@RequestMapping(path = "link")
public class LinkController {

    @GetMapping
    public ModelAndView getLink(@RequestParam("link") URI link) {
        return new ModelAndView("link", Map.of("link", link));
    }
}
