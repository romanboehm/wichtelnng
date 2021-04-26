package com.romanboehm.wichtelnng.integration;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.romanboehm.wichtelnng.data.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

import javax.mail.Address;
import java.util.UUID;

import static com.icegreen.greenmail.configuration.GreenMailConfiguration.aConfig;
import static com.icegreen.greenmail.util.ServerSetupTest.SMTP_IMAP;
import static com.romanboehm.wichtelnng.TestData.eventFormParams;
import static com.romanboehm.wichtelnng.TestData.participantFormParams;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WichtelnIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP_IMAP)
            .withConfiguration(aConfig().withDisabledAuthentication());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Value("${com.romanboehm.wichtelnng.domain}")
    private String domain;

    @AfterEach
    void cleanup() {
        eventRepository.deleteAll();
    }

    @Test
    void shouldDoGetFormSaveProvideLinkRegisterFlow() throws Exception {

        // Fetch page where event can be created
        mockMvc.perform(get("/event"))
                .andExpect(status().is2xxSuccessful());

        // Fill out and submit form for event
        MultiValueMap<String, String> params = eventFormParams();
        ResultActions createEvent = mockMvc.perform(post("/event")
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(params)
        )
                .andExpect(status().is3xxRedirection());

        // Hacky way to retrieve event's ID without mocking repo
        UUID eventId = eventRepository.findAll().get(0).getId();

        createEvent
                .andExpect(redirectedUrl(format("/event/%s/link", eventId)));

        String registrationUrl = format("%s/event/%s/registration", domain, eventId);

        // "Redirect" to page showing registration link
        mockMvc.perform(get(format("/event/%s/link", eventId)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(stringContainsInOrder(
                        "Provide this link to everyone you wish to participate in your Wichteln event",
                        registrationUrl
                )));

        // Fetch page for participant registration
        mockMvc.perform(get(registrationUrl))
                .andExpect(status().is2xxSuccessful());

        // Register participant for event
        params.addAll(participantFormParams());
        params.add("id", eventId.toString());
        mockMvc.perform(post(registrationUrl)
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(params)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/event/%s/registration/finish", eventId)));

        assertThat(greenMail.waitForIncomingEmail(1500, 1)).isTrue();
        assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .containsExactly("angusyoung@acdc.net");
    }
}