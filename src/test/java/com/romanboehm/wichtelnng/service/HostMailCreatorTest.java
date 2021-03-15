package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.CustomSpringBootTest;
import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.Donor;
import com.romanboehm.wichtelnng.model.Match;
import com.romanboehm.wichtelnng.model.Recipient;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.model.entity.Participant;
import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

@CustomSpringBootTest(properties = {"com.romanboehm.wichtelnng.domain=https://wichtelnng.romanboehm.com"})
public class HostMailCreatorTest {

    @Autowired
    private HostMailCreator hostMailCreator;

    @Test
    public void shouldHandleToAndFromCorrectly() throws MessagingException {
        Event event = TestData.event()
                .addParticipant(
                        new Participant()
                                .setName("Angus Young")
                                .setEmail("angusyoung@acdc.net")
                );

        MimeMessage mail = hostMailCreator.createMessage(event);

        Assertions.assertThat(mail).isNotNull();
        Assertions.assertThat(mail.getRecipients(Message.RecipientType.TO))
                .extracting(Address::toString)
                .containsExactly("georgeyoung@acdc.net");
    }

    @Test
    public void shouldHandleDataCorrectly() throws IOException, MessagingException {
        Event event = TestData.event()
                .addParticipant(
                        new Participant()
                                .setName("Angus Young")
                                .setEmail("angusyoung@acdc.net")
                );

        MimeMessage mail = hostMailCreator.createMessage(event);

        Assertions.assertThat(mail).isNotNull();
        Assertions.assertThat(mail.getSubject()).isEqualTo("Unfortunately, nobody has registered for 'AC/DC Secret Santa'");
        MatcherAssert.assertThat(mail.getContent().toString(), Matchers.stringContainsInOrder(
                "Hey George Young,",
                "Unfortunately nobody has registered to wichtel at 'AC/DC Secret Santa'.",
                "Try creating a new event: https://wichtelnng.romanboehm.com!",
                "This mail was generated using https://wichtelnng.romanboehm.com"
        ));
    }

}