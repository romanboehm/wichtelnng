package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.usecases.createevent.EventCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
@RecordApplicationEvents
class EventCreatedEventListenerTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private TaskScheduler scheduler;

    @BeforeEach
    void setUp() {
        Mockito.reset(scheduler);
    }

    @Test
    void schedulesTaskForEventNotification() {
        var deadline = Instant.now();
        eventPublisher.publishEvent(new EventCreatedEvent(Optional.empty(), UUID.randomUUID(), deadline));

        verify(scheduler, times(1)).schedule(any(Runnable.class), eq(deadline));
    }
}