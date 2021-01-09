package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.Event;
import com.romanboehm.wichtelnng.model.SendResult;
import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@SpringBootTest
public class HostMailCreatorTest {

    @Autowired
    private HostMailCreator hostMailCreator;

    @Test
    public void shouldHandleToAndFromCorrectly() throws MessagingException {
        MimeMessage mail = hostMailCreator.createHostMessage(TestData.event().asObject(), Collections.emptyList());

        Assertions.assertThat(mail).isNotNull();
        Assertions.assertThat(mail.getRecipients(Message.RecipientType.TO))
                .extracting(Address::toString)
                .containsExactly("georgeyoung@acdc.net");
    }

    @Test
    public void shouldHandleSummaryDataCorrectly() throws IOException, MessagingException {
        Event event = TestData.event().asObject();

        MimeMessage mail = hostMailCreator.createHostMessage(
                event,
                List.of(
                        SendResult.failure("Angus Young", "angusyoung@acdc.net"),
                        SendResult.success("Malcolm Young", "malcolmyoung@acdc.net"),
                        SendResult.success("Phil Rudd", "philrudd@acdc.net")
                )
        );

        Assertions.assertThat(mail).isNotNull();
        Assertions.assertThat(mail.getSubject())
                .isEqualTo("The invitations for your Wichteln event 'AC/DC Secret Santa' have been sent");
        MatcherAssert.assertThat(mail.getContent().toString(), Matchers.stringContainsInOrder(
                "Hey George Young,",
                "We sent out the following invitations for 'AC/DC Secret Santa':",
                "- Malcolm Young, malcolmyoung@acdc.net",
                "- Phil Rudd, philrudd@acdc.net",
                "Mails to the following participants could not be sent:",
                "- Angus Young, angusyoung@acdc.net"
        ));
    }

}