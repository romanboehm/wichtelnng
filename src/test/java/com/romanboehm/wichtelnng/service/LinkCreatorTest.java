package com.romanboehm.wichtelnng.service;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class LinkCreatorTest {

    @Test
    public void shouldCreateLink() {
        LinkCreator linkCreator = new LinkCreator("https://wichteln.com");
        UUID id = UUID.nameUUIDFromBytes("acdc-secret-santa".getBytes(StandardCharsets.UTF_8));

        URI actual = linkCreator.forId(id);

        Assertions.assertThat(actual)
                .isEqualTo(URI.create(String.format("https://wichteln.com/wichteln/%s/register", id)));
    }
}