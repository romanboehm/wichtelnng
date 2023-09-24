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

class LostParticipationMailCreatorTest {

    private final LostParticipationMailCreator mailCreator = new LostParticipationMailCreator(
            "https://wichtelnng.romanboehm.com",
            "wichteln@romanboehm.com",
            MailUtils.getJavaMailSender());

    @Test
    void shouldHandleToAndFromCorrectly() throws MessagingException {
        LostParticipationMailDto lostParticipationMailDto = LostParticipationMailDto.from(
                event()
                        .setHost(
                                new Host("George Young", "georgeyoung@acdc.net"))
                        // Only one participant
                        .addParticipant(
                                new Participant()
                                        .setName("Angus Young")
                                        .setEmail("angusyoung@acdc.net")),
                "Angus Young", "angusyoung@acdc.net");

        MimeMessage mail = mailCreator.createMessage(lostParticipationMailDto);

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
        LostParticipationMailDto lostParticipationMailDto = LostParticipationMailDto.from(
                event()
                        .setHost(
                                new Host("George Young", "georgeyoung@acdc.net"))
                        // Only one participant
                        .addParticipant(
                                new Participant()
                                        .setName("Angus Young")
                                        .setEmail("angusyoung@acdc.net")),
                "Angus Young", "angusyoung@acdc.net");

        MimeMessage mail = mailCreator.createMessage(lostParticipationMailDto);

        assertThat(mail).isNotNull();
        assertThat(mail.getSubject()).isEqualTo("Unfortunately, not enough other people have registered for 'AC/DC Secret Santa'");
        MatcherAssert.assertThat(mail.getContent().toString(), stringContainsInOrder(
                "Hey Angus Young,",
                "Unfortunately, not enough other people have registered to wichtel at 'AC/DC Secret Santa'.",
                "Try contacting the event's host if you have any questions: georgeyoung@acdc.net!",
                "This mail was generated using https://wichtelnng.romanboehm.com"));
    }

}