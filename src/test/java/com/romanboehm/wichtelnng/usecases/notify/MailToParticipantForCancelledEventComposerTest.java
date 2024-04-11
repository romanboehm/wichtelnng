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

class MailToParticipantForCancelledEventComposerTest {

    private final MailToParticipantForCancelledEventComposer mailCreator = new MailToParticipantForCancelledEventComposer(
            "https://wichtelnng.romanboehm.com",
            "mail@wichtelnng.romanboehm.com",
            MailUtils.getJavaMailSender());

    @Test
    void shouldHandleToAndFromCorrectly() throws MessagingException {
        MailToParticipantDataForCancelledEvent mailToParticipantDataForCancelledEvent = MailToParticipantDataForCancelledEvent.from(
                event()
                        .setHost(
                                new Host("George Young", "georgeyoung@acdc.net"))
                        // Only one participant
                        .addParticipant(
                                new Participant()
                                        .setName("Angus Young")
                                        .setEmail("angusyoung@acdc.net")),
                "Angus Young", "angusyoung@acdc.net");

        MimeMessage mail = mailCreator.createMessage(mailToParticipantDataForCancelledEvent);

        assertThat(mail).isNotNull();
        assertThat(mail.getFrom())
                .extracting(Address::toString)
                .containsExactly("mail@wichtelnng.romanboehm.com");
        assertThat(mail.getRecipients(TO))
                .extracting(Address::toString)
                .containsExactly("angusyoung@acdc.net");
    }

    @Test
    void shouldHandleDataCorrectly() throws IOException, MessagingException {
        MailToParticipantDataForCancelledEvent mailToParticipantDataForCancelledEvent = MailToParticipantDataForCancelledEvent.from(
                event()
                        .setHost(
                                new Host("George Young", "georgeyoung@acdc.net"))
                        // Only one participant
                        .addParticipant(
                                new Participant()
                                        .setName("Angus Young")
                                        .setEmail("angusyoung@acdc.net")),
                "Angus Young", "angusyoung@acdc.net");

        MimeMessage mail = mailCreator.createMessage(mailToParticipantDataForCancelledEvent);

        assertThat(mail).isNotNull();
        assertThat(mail.getSubject()).isEqualTo("Unfortunately, not enough other people have registered for 'AC/DC Secret Santa'");
        MatcherAssert.assertThat(mail.getContent().toString(), stringContainsInOrder(
                "Hey Angus Young,",
                "Unfortunately, not enough other people have registered to wichtel at 'AC/DC Secret Santa'.",
                "Try contacting the event's host if you have any questions: georgeyoung@acdc.net!",
                "This mail was generated using https://wichtelnng.romanboehm.com"));
    }

}