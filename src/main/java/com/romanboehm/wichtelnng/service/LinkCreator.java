package com.romanboehm.wichtelnng.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.UUID;

@Component
public class LinkCreator {

    private final String domain;

    public LinkCreator(@Value("${com.romanboehm.wichtlenng.domain}") String domain) {
        this.domain = domain;
    }

    public URI forId(UUID eventId) {
        return URI.create(String.format("%s/wichteln/%s/register", domain, eventId));
    }
}
