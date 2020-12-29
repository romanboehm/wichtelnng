package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.Event;
import com.romanboehm.wichtelnng.model.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class AsyncHostMailer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncHostMailer.class);

    private final HostMailCreator hostMailCreator;
    private final AsyncJavaMailSender mailSender;

    public AsyncHostMailer(HostMailCreator hostMailCreator, AsyncJavaMailSender mailSender) {
        this.hostMailCreator = hostMailCreator;
        this.mailSender = mailSender;
    }

    public CompletableFuture<SendResult> send(Event event, List<SendResult> sendResults) {
        MimeMessage hostMessage = hostMailCreator.createHostMessage(event, sendResults);
        LOGGER.debug(
                "Created mail for {} informing {} about {}",
                event,
                event.getHost(),
                sendResults.stream().map(SendResult::toString).collect(Collectors.joining(", "))
        );
        return mailSender.send(hostMessage).handle(
                (__, throwable) -> {
                    String name = event.getHost().getName();
                    String email = event.getHost().getEmail();
                    // N.B.: This does not mean the mail has bounced.
                    // This is a case we could only check by retrieving a bounce notification.
                    if (throwable != null) {
                        LOGGER.error(
                                "Encountered exception while trying to send mail to {}", event.getHost(), throwable
                        );
                        return SendResult.failure(name, email);
                    }
                    return SendResult.success(name, email);
                }
        );
    }
}
