package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.Event;
import com.romanboehm.wichtelnng.model.ParticipantsMatch;
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

@SpringBootTest
public class MatchMailCreatorTest {

    @Autowired
    private MatchMailCreator matchMailCreator;

    @Test
    public void shouldHandleToAndFromCorrectly() throws MessagingException {
        Event event = TestData.event().asObject();
        ParticipantsMatch angusGiftsToMalcolm = new ParticipantsMatch(
                new ParticipantsMatch.Donor(event.getParticipants().get(0)),
                new ParticipantsMatch.Recipient(event.getParticipants().get(1))
        );

        MimeMessage mail = matchMailCreator.createMatchMessage(event, angusGiftsToMalcolm);

        Assertions.assertThat(mail).isNotNull();
        Assertions.assertThat(mail.getRecipients(Message.RecipientType.TO))
                .extracting(Address::toString)
                .containsExactly("angusyoung@acdc.net");
    }

    @Test
    public void shouldHandleEventDataCorrectly() throws IOException, MessagingException {
        Event event = TestData.event().asObject();
        ParticipantsMatch angusGiftsToMalcolm = new ParticipantsMatch(
                new ParticipantsMatch.Donor(event.getParticipants().get(0)),
                new ParticipantsMatch.Recipient(event.getParticipants().get(1))
        );

        MimeMessage mail = matchMailCreator.createMatchMessage(event, angusGiftsToMalcolm);

        Assertions.assertThat(mail).isNotNull();
        Assertions.assertThat(mail.getSubject()).isEqualTo("You have been invited to wichtel at 'AC/DC Secret Santa'");
        MatcherAssert.assertThat(mail.getContent().toString(), Matchers.stringContainsInOrder(
                "Hey Angus Young,",
                "You have been invited to wichtel at 'AC/DC Secret Santa' (https://wichtelnng.romanboehm.com/about)!",
                "You're therefore asked to give a gift to Malcolm Young. The gift's monetary value should not exceed AUD 78.50.",
                "The event will take place at Sydney Harbor on 2666-06-07 at 06:06 local time.",
                "Here's what the event's host says about it:",
                "\"There's gonna be some santa'ing\"",
                "If you have any questions, contact the event's host George Young at georgeyoung@acdc.net.",
                "This mail was generated using https://wichtelnng.romanboehm.com"
        ));
    }

}