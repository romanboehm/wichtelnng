package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.exception.WichtelnMailTransmissionException;
import com.romanboehm.wichtelnng.model.Event;
import com.romanboehm.wichtelnng.model.ParticipantsMatch;
import com.romanboehm.wichtelnng.model.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class WichtelnMailer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WichtelnMailer.class);

    private final AsyncHostMailer hostMailer;
    private final AsyncParticipantsMailer participantsMailer;

    public WichtelnMailer(AsyncHostMailer hostMailer, AsyncParticipantsMailer participantsMailer) {
        this.hostMailer = hostMailer;
        this.participantsMailer = participantsMailer;
    }

    public void send(Event event, List<ParticipantsMatch> matches) {
        try {
            SendResult sendHostResult = participantsMailer.send(event, matches)
                    .thenCompose(sendResults -> hostMailer.send(event, sendResults))
                    .get(30, TimeUnit.SECONDS);
            if (!(sendHostResult.isSuccess())) {
                LOGGER.error("Error while sending out mail for {} to {}", event, event.getHost());
                throw new WichtelnMailTransmissionException();
            }
            LOGGER.info("Sent mail to host and matches about {}", event);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
           LOGGER.error("Error while sending out mail for {}", event, e);
        }
    }
}
