package com.romanboehm.wichtelnng.controller;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.TestUtils;
import com.romanboehm.wichtelnng.model.Event;
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
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.mail.Address;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@SpringBootTest
@AutoConfigureMockMvc
public class WichtelnControllerTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP_IMAP)
            .withConfiguration(GreenMailConfiguration.aConfig().withDisabledAuthentication());

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private JavaMailSender mailSender;

    @Test
    public void shouldDoGetFormPreviewMailSendMailFlow() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/wichteln"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/preview")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(TestData.event().asFormParams())
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.stringContainsInOrder(
                        "Hey <span>Angus Young</span>,",
                        "You have been invited to wichtel at <span>&#39;AC/DC Secret Santa&#39;</span> (<a href=\"https://wichtelnng.romanboehm.com/about\">https://wichtelnng.romanboehm.com/about</a>)!<br/>",
                        "You're therefore asked to give a gift to <span>Phil Rudd</span>. The gift's monetary value should not exceed <span>AUD 78.50</span>.<br/>",
                        "The event will take place at <span>Sydney Harbor</span> on <span>2666-06-07</span> at <span>06:06</span> local time.",
                        "Here's what the event's host says about it:",
                        "<i>\"<span>There&#39;s gonna be some santa&#39;ing</span>\"</i>",
                        "If you have any questions, contact the event's host <span>George Young</span> at <a href=\"mailto:georgeyoung@acdc.net\"><span>georgeyoung@acdc.net</span></a>.",
                        "This mail was generated using <a href=\"https://wichtelnng.romanboehm.com\">https://wichtelnng.romanboehm.com</a>")));

        mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(TestData.event().asFormParams())
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        Assertions.assertThat(greenMail.waitForIncomingEmail(1500, 4)).isTrue();
        Assertions.assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .containsExactlyInAnyOrder(
                        "angusyoung@acdc.net",
                        "malcolmyoung@acdc.net",
                        "philrudd@acdc.net",
                        "georgeyoung@acdc.net"
                );
    }

    @Test
    public void shouldValidate() throws Exception {
        LocalDate invalidDate = LocalDate.now().minus(1, ChronoUnit.DAYS);

        mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/preview")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(
                        TestData
                                .event()
                                .modifying(event -> event.setLocalDate(invalidDate))
                                .asFormParams()
                )
        )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(
                        "Must take place in the future."
                )));
    }

    @Test
    public void shouldStillValidateInPreviewModeToPreventTamperingWithHiddenForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(
                        TestData.event()
                                .modifying(event -> event.setTitle("This title is too long".repeat(20)))
                                .asFormParams()
                )
        );

        Assertions.assertThat(greenMail.waitForIncomingEmail(1500, 4)).isFalse();
    }

    @Test
    public void shouldShowErrorPageWhenHostMailCannotBeSent() throws Exception {
        Event event = TestData.event().asObject();

        Mockito
                .doThrow(new MailSendException("error"))
                .when(mailSender).send(ArgumentMatchers.argThat(TestUtils.isSentTo(event.getHost().getEmail())));

        mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(TestData.event().asFormParams())
        )
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andExpect(MockMvcResultMatchers.view().name("error"));

        Assertions.assertThat(greenMail.waitForIncomingEmail(1500, 4)).isFalse();
    }
}