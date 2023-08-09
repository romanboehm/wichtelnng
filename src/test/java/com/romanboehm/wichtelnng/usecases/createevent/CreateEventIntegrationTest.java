package com.romanboehm.wichtelnng.usecases.createevent;

import com.romanboehm.wichtelnng.utils.TestEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.regex.Pattern;

import static com.romanboehm.wichtelnng.utils.GlobalTestData.eventFormParams;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CreateEventIntegrationTest {

    @Autowired
    private TestEventRepository eventRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Value("${com.romanboehm.wichtelnng.domain}")
    private String domain;

    @BeforeEach
    void cleanup() {
        eventRepository.deleteAll();
    }

    @Test
    void createsEventAndProvidesRegistrationLink() {
        // Fetch page where event can be created
        var getCreateEventResponse = testRestTemplate.getForEntity("/event", Void.class);
        assertThat(getCreateEventResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Fill out and submit form for event
        var eventParams = eventFormParams();
        var postCreatEventHeaders = new HttpHeaders();
        postCreatEventHeaders.add("Content-Type", APPLICATION_FORM_URLENCODED_VALUE);
        var postCreateEventEntity = new HttpEntity<>(eventParams, postCreatEventHeaders);
        var postCreateEventResponse = testRestTemplate.postForEntity("/event", postCreateEventEntity, Void.class);
        assertThat(postCreateEventResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        var linkLocation = postCreateEventResponse.getHeaders().getLocation();
        var matcher = Pattern.compile(".+/([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})/link").matcher(linkLocation.toString());
        assertThat(matcher.matches()).isTrue();
        var eventId = UUID.fromString(matcher.group(1));

        var getShowLinkResponse = testRestTemplate.getForEntity(linkLocation, String.class);
        assertThat(getShowLinkResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getShowLinkResponse.getBody()).contains(
                "Provide this link to everyone you wish to participate in your Wichteln event",
                "%s/event/%s/registration".formatted(domain, eventId));

        assertThat(eventRepository.findById(eventId)).isPresent();
    }
}