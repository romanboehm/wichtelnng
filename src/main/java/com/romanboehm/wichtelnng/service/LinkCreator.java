package com.romanboehm.wichtelnng.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.UUID;

import static java.lang.String.format;

@Component
public class LinkCreator {

    private final String domain;

    public LinkCreator(@Value("${com.romanboehm.wichtelnng.domain}") String domain) {
        this.domain = domain;
    }

    public URI forId(UUID eventId) {
        return URI.create(format("%s/wichteln/%s/register", domain, eventId));
    }
}
