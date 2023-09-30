package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.utils.MailUtils;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.romanboehm.wichtelnng.utils.GlobalTestData.event;
import static jakarta.mail.Message.RecipientType.TO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;

class MailToDonorComposerTest {

    private MailToDonorComposer mailCreator;

    @BeforeEach
    void setUp() {
        mailCreator = new MailToDonorComposer(
                "https://wichtelnng.romanboehm.com",
                "wichteln@romanboehm.com",
                MailUtils.getTemplateEngine(),
                MailUtils.getJavaMailSender());
    }

    @Test
    void shouldHandleToAndFromCorrectly() throws MessagingException {
        var match = new Match(new Match.Donor("Angus Young", "angusyoung@acdc.net"), new Match.Recipient("Malcolm Young", "malcolmyoung@acdc.net"));

        var mailToDonorData = MailToDonorData.from(event(), match);

        var mail = mailCreator.createMessage(mailToDonorData);

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
        var match = new Match(new Match.Donor("Angus Young", "angusyoung@acdc.net"), new Match.Recipient("Malcolm Young", "malcolmyoung@acdc.net"));

        var mailToDonorData = MailToDonorData.from(event(), match);

        var mail = mailCreator.createMessage(mailToDonorData);

        assertThat(mail).isNotNull();
        assertThat(mail.getSubject()).isEqualTo("You have been matched to wichtel at 'AC/DC Secret Santa'");
        MatcherAssert.assertThat(mail.getContent().toString(), stringContainsInOrder(
                "Hey Angus Young,",
                "You registered to wichtel at 'AC/DC Secret Santa' (https://wichtelnng.romanboehm.com/about)!",
                "We matched the event's participants and you're therefore now asked to give a gift to Malcolm Young. The gift's monetary value should not exceed AUD 78.50.",
                "Here's what the event's host says about it:",
                "\"There's gonna be some santa'ing\"",
                "If you have any questions, contact the event's host George Young at georgeyoung@acdc.net.",
                "This mail was generated using https://wichtelnng.romanboehm.com"));
    }

}