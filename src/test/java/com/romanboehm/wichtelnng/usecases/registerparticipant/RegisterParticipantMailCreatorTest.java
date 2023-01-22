package com.romanboehm.wichtelnng.usecases.registerparticipant;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static com.romanboehm.wichtelnng.GlobalTestData.event;
import static jakarta.mail.Message.RecipientType.TO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
public class RegisterParticipantMailCreatorTest {

    @Autowired
    private RegisterParticipantMailCreator mailCreator;

    @Test
    void shouldHandleToAndFromCorrectly() throws MessagingException {
        RegisterParticipant registration = RegisterParticipant.registerFor(event())
                .setParticipantName("Angus Young")
                .setParticipantEmail("angusyoung@acdc.net");

        MimeMessage mail = mailCreator.createMessage(RegistrationMailEvent.from(registration));

        assertThat(mail).isNotNull();
        assertThat(mail.getFrom())
                .extracting(Address::toString)
                .containsExactly("wichteln@romanboehm.com");
        assertThat(mail.getRecipients(TO))
                .extracting(Address::toString)
                .containsExactly("angusyoung@acdc.net");
    }

    @Test
    void shouldHandleDataCorrectly() throws IOException, MessagingException {
        RegisterParticipant registration = RegisterParticipant.registerFor(event())
                .setParticipantName("Angus Young")
                .setParticipantEmail("angusyoung@acdc.net");

        MimeMessage mail = mailCreator.createMessage(RegistrationMailEvent.from(registration));

        assertThat(mail).isNotNull();
        assertThat(mail.getSubject()).isEqualTo("You have registered to wichtel at 'AC/DC Secret Santa'");
        MatcherAssert.assertThat(mail.getContent().toString(), stringContainsInOrder(
                "Hey Angus Young,",
                "You have successfully registered to wichtel at 'AC/DC Secret Santa' (https://wichtelnng.romanboehm.com/about)!",
                "You'll be provided with the name of your gift's recipient through an email to angusyoung@acdc.net once everybody has registered.",
                "The gift's monetary value should not exceed AUD 78.50.",
                "Here's what the event's host says about it:",
                "\"There's gonna be some santa'ing\"",
                "If you have any questions, contact the event's host George Young at georgeyoung@acdc.net.",
                "This mail was generated using https://wichtelnng.romanboehm.com"
        ));
    }

}