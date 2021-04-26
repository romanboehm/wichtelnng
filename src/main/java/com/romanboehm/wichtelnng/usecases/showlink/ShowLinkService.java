package com.romanboehm.wichtelnng.usecases.showlink;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.UUID;

import static java.lang.String.format;

@Component
public class ShowLinkService {

    private final String domain;

    public ShowLinkService(@Value("${com.romanboehm.wichtelnng.domain}") String domain) {
        this.domain = domain;
    }

    URI createLink(UUID eventId) {
        return URI.create(format("%s/event/%s/registration", domain, eventId));
    }
}
