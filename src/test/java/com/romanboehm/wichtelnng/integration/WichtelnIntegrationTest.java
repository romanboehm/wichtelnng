package com.romanboehm.wichtelnng.integration;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.romanboehm.wichtelnng.CustomSpringBootTest;
import com.romanboehm.wichtelnng.service.WichtelnService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MultiValueMap;

import javax.mail.Address;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static com.icegreen.greenmail.configuration.GreenMailConfiguration.aConfig;
import static com.icegreen.greenmail.util.ServerSetupTest.SMTP_IMAP;
import static com.romanboehm.wichtelnng.TestData.eventFormParams;
import static com.romanboehm.wichtelnng.TestData.participantFormParams;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@CustomSpringBootTest
@AutoConfigureMockMvc
public class WichtelnIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP_IMAP)
            .withConfiguration(aConfig().withDisabledAuthentication());

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private WichtelnService wichtelnService;

    @Test
    public void shouldDoGetFormSaveProvideLinkRegisterFlow() throws Exception {
        AtomicReference<URI> registrationUrl = new AtomicReference<>();
        doAnswer(invocationOnMock -> {
            URI uri = (URI) invocationOnMock.callRealMethod();
            registrationUrl.set(uri);
            return uri;
        }).when(wichtelnService).createLink(any());

        AtomicReference<UUID> eventId = new AtomicReference<>();
        doAnswer(invocationOnMock -> {
            UUID uuid = (UUID) invocationOnMock.callRealMethod();
            eventId.set(uuid);
            return uuid;
        }).when(wichtelnService).save(any());

        // Fetch page where event can be created
        mockMvc.perform(get("/wichteln"))
                .andExpect(status().is2xxSuccessful());

        // Fill out and submit form for event
        MultiValueMap<String, String> params = eventFormParams();
        mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/save")
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(params)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/wichteln/%s/link", eventId.toString())));

        // "Redirect" to page showing registration link
        mockMvc.perform(get(format("/wichteln/%s/link", eventId.toString())))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(stringContainsInOrder(
                        "Provide this link to everyone you wish to participate in your Wichteln event",
                        registrationUrl.toString()
                )));

        // Fetch page for participant registration
        mockMvc.perform(get(registrationUrl.toString()))
                .andExpect(status().is2xxSuccessful());

        // Register participant for event
        params.addAll(participantFormParams());
        params.add("id", eventId.toString());
        mockMvc.perform(MockMvcRequestBuilders.post(registrationUrl.toString())
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(params)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/wichteln/afterregistration"));

        assertThat(greenMail.waitForIncomingEmail(1500, 1)).isTrue();
        assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .containsExactly("angusyoung@acdc.net");
    }
}