package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Host;
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
class LostEventMailCreatorTest {

    @Autowired
    private LostEventMailCreator mailCreator;

    @Test
    void shouldHandleToAndFromCorrectly() throws MessagingException {
        LostMailEvent lostMailEvent = LostMailEvent.from(
                event()
                        .setHost(
                                new Host()
                                        .setEmail("georgeyoung@acdc.net")
                        )
                        // Only one participant
                        .addParticipant(
                                new Participant()
                                        .setName("Angus Young")
                                        .setEmail("angusyoung@acdc.net")
                        )
        );

        MimeMessage mail = mailCreator.createMessage(lostMailEvent);

        assertThat(mail).isNotNull();
        assertThat(mail.getFrom())
                .extracting(Address::toString)
                .containsExactly("wichteln@romanboehm.com");
        assertThat(mail.getRecipients(TO))
                .extracting(Address::toString)
                .containsExactly("georgeyoung@acdc.net");
    }

    @Test
    void shouldHandleDataCorrectly() throws IOException, MessagingException {
        LostMailEvent lostMailEvent = LostMailEvent.from(
                event()
                        .setHost(
                                new Host()
                                        .setEmail("georgeyoung@acdc.net")
                                        .setName("George Young")
                        )
                        .addParticipant(
                                new Participant()
                                        .setName("Angus Young")
                                        .setEmail("angusyoung@acdc.net")
                        )
        );

        MimeMessage mail = mailCreator.createMessage(lostMailEvent);

        assertThat(mail).isNotNull();
        assertThat(mail.getSubject()).isEqualTo("Unfortunately, nobody has registered for 'AC/DC Secret Santa'");
        MatcherAssert.assertThat(mail.getContent().toString(), stringContainsInOrder(
                "Hey George Young,",
                "Unfortunately nobody has registered to wichtel at 'AC/DC Secret Santa'.",
                "Try creating a new event: https://wichtelnng.romanboehm.com!",
                "This mail was generated using https://wichtelnng.romanboehm.com"
        ));
    }

}