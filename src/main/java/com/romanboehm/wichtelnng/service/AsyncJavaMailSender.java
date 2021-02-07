package com.romanboehm.wichtelnng.service;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;

@Component
public class AsyncJavaMailSender {

    private final JavaMailSender internalSender;

    public AsyncJavaMailSender(JavaMailSender internalSender) {
        this.internalSender = internalSender;
    }

    public MimeMessage createMimeMessage() {
        return internalSender.createMimeMessage();
    }

    @Async
    public CompletableFuture<Void> send(MimeMessage mimeMessage) throws MailException {
        internalSender.send(mimeMessage);
        return null;
    }
}
