package com.romanboehm.wichtelnng.service;


import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.entity.Event;
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
        Event event = TestData.event().asEntity().setId(id);

        URI actual = linkCreator.forEvent(event);

        Assertions.assertThat(actual).isEqualTo(URI.create("https://wichteln.com/wichteln/" + id));
    }
}