package com.romanboehm.wichtelnng.integration;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.service.WichtelnService;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MultiValueMap;

import javax.mail.Address;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest
@AutoConfigureMockMvc
public class WichtelnIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP_IMAP)
            .withConfiguration(GreenMailConfiguration.aConfig().withDisabledAuthentication());

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private WichtelnService wichtelnService;

    @Test
    public void shouldDoGetFormSaveProvideLinkRegisterFlow() throws Exception {
        AtomicReference<URI> registrationUrl = new AtomicReference<>();
        Mockito.doAnswer(invocationOnMock -> {
            URI uri = (URI) invocationOnMock.callRealMethod();
            registrationUrl.set(uri);
            return uri;
        }).when(wichtelnService).createLink(ArgumentMatchers.any());

        AtomicReference<UUID> eventId = new AtomicReference<>();
        Mockito.doAnswer(invocationOnMock -> {
            UUID uuid = (UUID) invocationOnMock.callRealMethod();
            eventId.set(uuid);
            return uuid;
        }).when(wichtelnService).save(ArgumentMatchers.any());

        // Fetch page where event can be created
        mockMvc.perform(MockMvcRequestBuilders.get("/wichteln"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        // Fill out and submit form for event
        MultiValueMap<String, String> params = TestData.event().formParams();
        mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params)
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(String.format("/wichteln/%s/link", eventId.toString())));

        // "Redirect" to page showing registration link
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/wichteln/%s/link", eventId.toString())))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.stringContainsInOrder(
                        "Provide this link to everyone you wish to participate in your Wichteln event",
                        registrationUrl.toString()
                )));

        // Fetch page for participant registration
        mockMvc.perform(MockMvcRequestBuilders.get(registrationUrl.toString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        // Register participant for event
        params.addAll(TestData.participant().formParams());
        params.add("event.id", eventId.toString());
        mockMvc.perform(MockMvcRequestBuilders.post(registrationUrl.toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params)
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/wichteln/afterregistration"));

        Assertions.assertThat(greenMail.waitForIncomingEmail(1500, 1)).isTrue();
        Assertions.assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .containsExactly("angusyoung@acdc.net");
    }
}