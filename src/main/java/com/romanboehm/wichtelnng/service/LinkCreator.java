package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.entity.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class LinkCreator {

    private final String domain;

    public LinkCreator(@Value("${domain}") String domain) {
        this.domain = domain;
    }

    public URI forEvent(Event event) {
        return URI.create(String.format("%s/wichteln/%s", domain, event.getId()));
    }
}
