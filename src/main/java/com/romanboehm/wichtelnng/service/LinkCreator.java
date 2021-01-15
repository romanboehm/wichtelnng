package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.entity.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LinkCreator {

    private final String domain;

    public LinkCreator(@Value("domain") String domain) {
        this.domain = domain;
    }

    public String forEvent(Event event) {
        return String.format("%s/wichteln/%s", domain, event.getId());
    }
}
