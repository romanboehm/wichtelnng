package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.romanboehm.wichtelnng.utils.MailUtils;
import com.romanboehm.wichtelnng.utils.TestEventRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.concurrent.TimeUnit;

import static com.icegreen.greenmail.configuration.GreenMailConfiguration.aConfig;
import static com.icegreen.greenmail.util.ServerSetupTest.SMTP_IMAP;
import static com.romanboehm.wichtelnng.utils.GlobalTestData.event;
import static com.romanboehm.wichtelnng.utils.GlobalTestData.eventFormParams;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class RegisterParticipantIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP_IMAP)
            .withConfiguration(aConfig().withDisabledAuthentication());

    @Autowired
    private TestEventRepository eventRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Value("${com.romanboehm.wichtelnng.domain}")
    private String domain;

    @Value("${local.server.port}")
    String port;

    @BeforeEach
    void cleanup() {
        eventRepository.deleteAll();
    }

    @Test
    void providesRegistration() {
        var eventId = eventRepository.save(event()).getId();
        String registrationUrl = "%s:%s/event/%s/registration".formatted(domain, port, eventId);

        // Fetch page for participant registration
        var getRegisterResponse = testRestTemplate.getForEntity(registrationUrl, Void.class);
        assertThat(getRegisterResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Register participant for event
        var eventParams = eventFormParams();
        eventParams.add("participantName", "Angus Young");
        eventParams.add("participantEmail", "angusyoung@register.acdc.net");
        eventParams.add("id", eventId.toString());
        var postRegisterParticipantHeaders = new HttpHeaders();
        postRegisterParticipantHeaders.add("Content-Type", APPLICATION_FORM_URLENCODED_VALUE);
        var postRegisterParticipantEntity = new HttpEntity<>(eventParams, postRegisterParticipantHeaders);
        var postRegisterParticipantResponse = testRestTemplate.postForEntity(registrationUrl, postRegisterParticipantEntity, Void.class);
        assertThat(postRegisterParticipantResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(postRegisterParticipantResponse.getHeaders().getLocation()).hasPath("/event/%s/registration/finish".formatted(eventId));

        assertThat(eventRepository.findByIdWithParticipants(eventId)).hasValueSatisfying(e -> assertThat(e.getParticipants()).singleElement().satisfies(p -> {
            assertThat(p.getName()).isEqualTo("Angus Young");
            assertThat(p.getEmail()).isEqualTo("angusyoung@register.acdc.net");
        }));
        Awaitility.await().atMost(1500, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            var mail = MailUtils.findMailFor(greenMail, "angusyoung@register.acdc.net");
            assertThat(mail)
                    .singleElement()
                    .satisfies(mimeMessage -> assertThat(mimeMessage.getContent().toString()).contains("You have successfully registered to wichtel"));
        });
    }
}