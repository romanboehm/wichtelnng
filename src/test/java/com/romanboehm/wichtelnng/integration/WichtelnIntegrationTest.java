package com.romanboehm.wichtelnng.integration;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.romanboehm.wichtelnng.data.TestEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

import java.util.UUID;

import static com.icegreen.greenmail.configuration.GreenMailConfiguration.aConfig;
import static com.icegreen.greenmail.util.ServerSetupTest.SMTP_IMAP;
import static com.romanboehm.wichtelnng.GlobalTestData.eventFormParams;
import static com.romanboehm.wichtelnng.GlobalTestData.participantFormParams;
import static java.lang.String.format;
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
    protected static GreenMailExtension greenMail = new GreenMailExtension(SMTP_IMAP)
            .withConfiguration(aConfig().withDisabledAuthentication());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestEventRepository eventRepository;

    @Value("${com.romanboehm.wichtelnng.domain}")
    private String domain;

    @BeforeEach
    void cleanup() {
        eventRepository.deleteAll();
    }

    @Test
    void redirectsFromRootToEvent() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldDoGetFormSaveProvideLinkRegisterFlow() throws Exception {
        // Fetch page where event can be created
        mockMvc.perform(get("/event"))
                .andExpect(status().is2xxSuccessful());

        // Fill out and submit form for event
        MultiValueMap<String, String> params = eventFormParams();
        ResultActions createEventRedirect = mockMvc.perform(post("/event")
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .params(params)
                )
                .andExpect(status().is3xxRedirection());

        // Hacky way to retrieve event's ID since we cannot spy `EventRepository`.
        // Cf. https://github.com/spring-projects/spring-boot/issues/7033
        UUID eventId = eventRepository.findAll().get(0).getId();

        createEventRedirect
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

        assertThat(greenMail.waitForIncomingEmail(2500, 1)).isTrue();
        assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(addr -> addr.toString())
                .containsExactly("angusyoung@acdc.net");
    }
}