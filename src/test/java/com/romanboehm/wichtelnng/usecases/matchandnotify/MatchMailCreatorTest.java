package com.romanboehm.wichtelnng.usecases.matchandnotify;

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
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class MatchMailCreatorTest {

    @Autowired
    private MatchMailCreator mailCreator;

    @Test
    void shouldHandleToAndFromCorrectly() throws MessagingException {
        Donor angusYoung = new Donor("Angus Young", "angusyoung@acdc.net");
        Recipient malcolmYoung = new Recipient("Malcolm Young", "malcolmyoung@acdc.net");

        MatchMailEvent matchMailEvent = MatchMailEvent.from(
                event(),
                angusYoung, malcolmYoung
        );

        MimeMessage mail = mailCreator.createMessage(matchMailEvent);

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
        Donor angusYoung = new Donor("Angus Young", "angusyoung@acdc.net");
        Recipient malcolmYoung = new Recipient("Malcolm Young", "malcolmyoung@acdc.net");

        MatchMailEvent matchMailEvent = MatchMailEvent.from(
                event(),
                angusYoung, malcolmYoung
        );

        MimeMessage mail = mailCreator.createMessage(matchMailEvent);

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