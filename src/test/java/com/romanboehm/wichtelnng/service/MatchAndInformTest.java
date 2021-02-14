package com.romanboehm.wichtelnng.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.entity.Participant;
import com.romanboehm.wichtelnng.repository.EventRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.mail.Address;
import java.util.List;

@SpringBootTest
public class MatchAndInformTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP_IMAP)
            .withConfiguration(GreenMailConfiguration.aConfig().withDisabledAuthentication());

    @MockBean
    private EventRepository eventRepository;

    @Autowired
    private MatchAndInform matchAndInform;

    @Test
    public void shouldMatchAndInform() {
        Mockito.when(eventRepository.findAllByLocalDateTimeBefore(ArgumentMatchers.any())).thenReturn(List.of(
                TestData.event().entity()
                        .addParticipant(
                                new Participant()
                                        .setName("Angus Young")
                                        .setEmail("angusyoung@acdc.net")
                        )
                        .addParticipant(
                                new Participant()
                                        .setName("Malcolm Young")
                                        .setEmail("malcolmyoung@acdc.net")
                        )
        ));

        matchAndInform.matchAndInform();

        Assertions.assertThat(greenMail.waitForIncomingEmail(1500, 2)).isTrue();
        Assertions.assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .containsExactlyInAnyOrder(
                        "angusyoung@acdc.net",
                        "malcolmyoung@acdc.net"
                );


    }

}