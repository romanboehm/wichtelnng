package com.romanboehm.wichtelnng.service;


import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.UUID;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.nameUUIDFromBytes;
import static org.assertj.core.api.Assertions.assertThat;

public class LinkCreatorTest {

    @Test
    public void shouldCreateLink() {
        LinkCreator linkCreator = new LinkCreator("https://wichteln.com");
        UUID id = nameUUIDFromBytes("acdc-secret-santa".getBytes(UTF_8));

        URI actual = linkCreator.forId(id);

        assertThat(actual)
                .isEqualTo(URI.create(format("https://wichteln.com/wichteln/%s/register", id)));
    }
}