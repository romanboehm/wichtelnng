package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.Participant;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static com.romanboehm.wichtelnng.TestData.event;
import static javax.mail.Message.RecipientType.TO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class MatchMailCreatorTest {

    @Autowired
    private MatchMailCreator mailCreator;

    @Test
    void shouldHandleToAndFromCorrectly() throws MessagingException {
        Participant angusYoung = new Participant()
                .setName("Angus Young")
                .setEmail("angusyoung@acdc.net");
        Participant malcolmYoung = new Participant()
                .setName("Malcolm Young")
                .setEmail("malcolmyoung@acdc.net");
        Event event = event()
                .addParticipant(angusYoung)
                .addParticipant(malcolmYoung);
        Match angusGiftsToMalcolm = new Match(
                new Donor(angusYoung),
                new Recipient(malcolmYoung)
        );

        MimeMessage mail = mailCreator.createMessage(event, angusGiftsToMalcolm);

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
        Participant angusYoung = new Participant()
                .setName("Angus Young")
                .setEmail("angusyoung@acdc.net");
        Participant malcolmYoung = new Participant()
                .setName("Malcolm Young")
                .setEmail("malcolmyoung@acdc.net");
        Event event = event()
                .addParticipant(angusYoung)
                .addParticipant(malcolmYoung);
        Match angusGiftsToMalcolm = new Match(
                new Donor(angusYoung),
                new Recipient(malcolmYoung)
        );

        MimeMessage mail = mailCreator.createMessage(event, angusGiftsToMalcolm);

        assertThat(mail).isNotNull();
        assertThat(mail.getSubject()).isEqualTo("You have been matched to wichtel at 'AC/DC Secret Santa'");
        MatcherAssert.assertThat(mail.getContent().toString(), stringContainsInOrder(
                "Hey Angus Young,",
                "You registered to wichtel at 'AC/DC Secret Santa' (https://wichtelnng.romanboehm.com/about)!",
                "We matched the event's participants and you're therefore now asked to give a gift to Malcolm Young. The gift's monetary value should not exceed AUD 78.50.",
                "Here's what the event's host says about it:",
                "\"There's gonna be some santa'ing\"",
                "If you have any questions, contact the event's host George Young at georgeyoung@acdc.net.",
                "This mail was generated using https://wichtelnng.romanboehm.com"
        ));
    }

}