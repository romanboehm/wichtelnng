package com.romanboehm.wichtelnng.usecases.showlink;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.UUID;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.nameUUIDFromBytes;
import static org.assertj.core.api.Assertions.assertThat;

class ShowLinkServiceTest {

    @Test
    void shouldShowLink() {
        ShowLinkService service = new ShowLinkService("https://wichteln.com");
        UUID id = nameUUIDFromBytes("acdc-secret-santa".getBytes(UTF_8));

        URI actual = service.createLink(id);

        assertThat(actual)
                .isEqualTo(URI.create(format("https://wichteln.com/event/%s/registration", id)));
    }

}