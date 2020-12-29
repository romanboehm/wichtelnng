package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.Event;
import com.romanboehm.wichtelnng.model.ParticipantsMatch;
import com.romanboehm.wichtelnng.model.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class AsyncParticipantsMailer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncParticipantsMailer.class);

    private final MatchMailCreator matchMailCreator;
    private final AsyncJavaMailSender mailSender;

    public AsyncParticipantsMailer(MatchMailCreator matchMailCreator, AsyncJavaMailSender mailSender) {
        this.matchMailCreator = matchMailCreator;
        this.mailSender = mailSender;
    }

    public CompletableFuture<List<SendResult>> send(Event event, List<ParticipantsMatch> matches) {
        List<CompletableFuture<SendResult>> sendResults = new ArrayList<>();
        for (ParticipantsMatch match : matches) {
            MimeMessage matchMessage = matchMailCreator.createMatchMessage(event, match);
            LOGGER.debug("Created mail for {} matching {}", event, match);
            CompletableFuture<SendResult> sendMatch = mailSender.send(matchMessage).handle(
                    (__, throwable) -> {
                        String name = match.getDonor().getName();
                        String email = match.getDonor().getEmail();
                        // N.B.: This does not mean the mail has bounced.
                        // This is a case we could only check by retrieving a bounce notification.
                        if (throwable != null) {
                            LOGGER.error(
                                    "Encountered exception while trying to send mail to {}", match.getDonor(), throwable
                            );
                            return SendResult.failure(name, email);
                        }
                        return SendResult.success(name, email);
                    }
            );
            sendResults.add(sendMatch);
        }
        return allOf(sendResults);
    }

    // Do not merge the individual `CompletableFuture<SendResult>`s into a single `CompletableFuture<Void>` but keep
    // them as a `List<CompletableFuture<SendReuslt>>`.
    // Cf. https://www.nurkiewicz.com/2013/05/java-8-completablefuture-in-action.html
    private static CompletableFuture<List<SendResult>> allOf(List<CompletableFuture<SendResult>> futures) {
        CompletableFuture<Void> allFuturesResult = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        return allFuturesResult.thenApply(v ->
                futures.stream().
                        map(CompletableFuture::join).
                        collect(Collectors.<SendResult>toList())
        );
    }
}
