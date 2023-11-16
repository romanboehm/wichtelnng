package com.romanboehm.wichtelnng.usecases.showlink;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.UUID;

import static java.lang.String.format;

@Service
class ShowLinkService {

    private final String domain;

    ShowLinkService(@Value("${com.romanboehm.wichtelnng.domain}") String domain) {
        this.domain = domain;
    }

    URI createLink(UUID eventId) {
        return URI.create(format("%s/event/%s/registration", domain, eventId));
    }
}
