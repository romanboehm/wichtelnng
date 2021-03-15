package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Slf4j
@RequiredArgsConstructor
@Component
public class HostMailSender {

    private final HostMailCreator hostMailCreator;
    private final JavaMailSender javaMailSender;

    public void send(Event event) {
        MimeMessage message = hostMailCreator.createMessage(event);
        try {
            javaMailSender.send(message);
            log.info("Sent mail to inform about empty event: {}", event);
        } catch (Exception e) {
            log.error("Failed to send mail informing about empty event: {}", event, e);
        }
    }
}
