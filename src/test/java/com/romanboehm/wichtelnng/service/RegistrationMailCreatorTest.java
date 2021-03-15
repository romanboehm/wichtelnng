package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.CustomSpringBootTest;
import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static com.romanboehm.wichtelnng.TestData.eventCreation;
import static javax.mail.Message.RecipientType.TO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;

@CustomSpringBootTest(properties= { "com.romanboehm.wichtelnng.domain=https://wichtelnng.romanboehm.com" })
public class RegistrationMailCreatorTest {

    @Autowired
    private RegistrationMailCreator registrationMailCreator;

    @Test
    public void shouldHandleToAndFromCorrectly() throws MessagingException {
        ParticipantRegistration registration = ParticipantRegistration.with(eventCreation())
                .setParticipantName("Angus Young")
                .setParticipantEmail("angusyoung@acdc.net");

        MimeMessage mail = registrationMailCreator.createMessage(registration);

        assertThat(mail).isNotNull();
        assertThat(mail.getRecipients(TO))
                .extracting(Address::toString)
                .containsExactly("angusyoung@acdc.net");
    }

    @Test
    public void shouldHandleDataCorrectly() throws IOException, MessagingException {
        ParticipantRegistration registration = ParticipantRegistration.with(eventCreation())
                .setParticipantName("Angus Young")
                .setParticipantEmail("angusyoung@acdc.net");

        MimeMessage mail = registrationMailCreator.createMessage(registration);

        assertThat(mail).isNotNull();
        assertThat(mail.getSubject()).isEqualTo("You have registered to wichtel at 'AC/DC Secret Santa'");
        MatcherAssert.assertThat(mail.getContent().toString(), stringContainsInOrder(
                "Hey Angus Young,",
                "You have successfully registered to wichtel at 'AC/DC Secret Santa' (https://wichtelnng.romanboehm.com/about)!",
                "You'll be provided with the name of your gift's recipient through an email to angusyoung@acdc.net after June 7, 2666 at 6:06:00 AM AEST.",
                "The gift's monetary value should not exceed AUD 78.50.",
                "Here's what the event's host says about it:",
                "\"There's gonna be some santa'ing\"",
                "If you have any questions, contact the event's host George Young at georgeyoung@acdc.net.",
                "This mail was generated using https://wichtelnng.romanboehm.com"
        ));
    }

}