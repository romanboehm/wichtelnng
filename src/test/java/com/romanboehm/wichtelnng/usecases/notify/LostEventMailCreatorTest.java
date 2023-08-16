package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Host;
import com.romanboehm.wichtelnng.common.data.Participant;
import com.romanboehm.wichtelnng.utils.MailUtils;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.romanboehm.wichtelnng.utils.GlobalTestData.event;
import static jakarta.mail.Message.RecipientType.TO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;

class LostEventMailCreatorTest {

    private final LostEventMailCreator mailCreator = new LostEventMailCreator(
            "https://wichtelnng.romanboehm.com",
            "wichteln@romanboehm.com",
            MailUtils.getJavaMailSender());

    @Test
    void shouldHandleToAndFromCorrectly() throws MessagingException {
        LostMailEvent lostMailEvent = LostMailEvent.from(
                event()
                        .setHost(
                                new Host("George Young", "georgeyoung@acdc.net"))
                        // Only one participant
                        .addParticipant(
                                new Participant()
                                        .setName("Angus Young")
                                        .setEmail("angusyoung@acdc.net")));

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
                                new Host("George Young", "georgeyoung@acdc.net"))
                        .addParticipant(
                                new Participant()
                                        .setName("Angus Young")
                                        .setEmail("angusyoung@acdc.net")));

        MimeMessage mail = mailCreator.createMessage(lostMailEvent);

        assertThat(mail).isNotNull();
        assertThat(mail.getSubject()).isEqualTo("Unfortunately, nobody has registered for 'AC/DC Secret Santa'");
        MatcherAssert.assertThat(mail.getContent().toString(), stringContainsInOrder(
                "Hey George Young,",
                "Unfortunately nobody has registered to wichtel at 'AC/DC Secret Santa'.",
                "Try creating a new event: https://wichtelnng.romanboehm.com!",
                "This mail was generated using https://wichtelnng.romanboehm.com"));
    }

}