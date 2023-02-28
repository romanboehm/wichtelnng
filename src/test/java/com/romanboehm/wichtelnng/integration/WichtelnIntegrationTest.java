package com.romanboehm.wichtelnng.integration;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.romanboehm.wichtelnng.MailUtils;
import com.romanboehm.wichtelnng.data.TestEventRepository;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.icegreen.greenmail.configuration.GreenMailConfiguration.aConfig;
import static com.icegreen.greenmail.util.ServerSetupTest.SMTP_IMAP;
import static com.romanboehm.wichtelnng.GlobalTestData.eventFormParams;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
class WichtelnIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP_IMAP)
            .withConfiguration(aConfig().withDisabledAuthentication());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestEventRepository eventRepository;

    @Value("${com.romanboehm.wichtelnng.domain}")
    private String domain;

    @BeforeEach
    void cleanup() {
        eventRepository.deleteAllInBatch();
        eventRepository.flush();
    }

    @Test
    void redirectsFromRootToEvent() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void providesGetFormSaveProvideLinkRegisterFlow() throws Exception {
        // Fetch page where event can be created
        mockMvc.perform(get("/event"))
                .andExpect(status().is2xxSuccessful());

        // Fill out and submit form for event
        var eventParams = eventFormParams();
        var createEventRedirect = mockMvc.perform(post("/event")
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(eventParams))
                .andExpect(status().is3xxRedirection());

        // Hacky way to retrieve event's ID since we cannot spy `EventRepository`.
        // Cf. https://github.com/spring-projects/spring-boot/issues/7033
        var eventId = eventRepository.findAll().get(0).getId();

        createEventRedirect
                .andExpect(redirectedUrl("/event/%s/link".formatted(eventId)));

        String registrationUrl = "%s/event/%s/registration".formatted(domain, eventId);

        // "Redirect" to page showing registration link
        mockMvc.perform(get("/event/%s/link".formatted(eventId)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(stringContainsInOrder(
                        "Provide this link to everyone you wish to participate in your Wichteln event",
                        registrationUrl)));

        // Fetch page for participant registration
        mockMvc.perform(get(registrationUrl))
                .andExpect(status().is2xxSuccessful());

        // Register participant for event
        eventParams.add("participantName", "Angus Young");
        eventParams.add("participantEmail", "angusyoung@workflow.acdc.net");
        eventParams.put("id", List.of(eventId.toString()));
        mockMvc.perform(post(registrationUrl)
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(eventParams))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/event/%s/registration/finish".formatted(eventId)));

        Awaitility.await().atMost(1500, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            var mail = MailUtils.findMailFor(greenMail, "angusyoung@workflow.acdc.net");
            assertThat(mail).isNotEmpty();
            assertThat(mail.get().getContent()).asInstanceOf(InstanceOfAssertFactories.STRING).contains(
                    "You have successfully registered to wichtel");
        });
    }
}