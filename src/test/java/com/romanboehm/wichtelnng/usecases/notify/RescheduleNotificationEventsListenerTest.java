package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.data.Deadline;
import com.romanboehm.wichtelnng.data.TestEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.romanboehm.wichtelnng.GlobalTestData.event;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
@RecordApplicationEvents
class RescheduleNotificationEventsListenerTest {

    @Autowired
    private TestEventRepository eventRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @SpyBean
    private TaskScheduler scheduler;

    @BeforeEach
    public void setUp() {
        eventRepository.deleteAll();
        eventRepository.flush();
    }

    @Test
    void schedulesNotificationForAllPendingEvents() {
        var l = List.of(1, 2, 3);
        var deadline = LocalDateTime.now();
        eventRepository.saveAllAndFlush(
                l.stream()
                        .map(i -> event()
                                .setTitle(i.toString())
                                .setDeadline(new Deadline()
                                        .setZoneId(ZoneId.of("UTC").getId())
                                        .setLocalDateTime(deadline.plus(i, ChronoUnit.SECONDS))
                                )
                        ).toList()
        );

        eventPublisher.publishEvent(new RescheduleNotificationEventsListener.RescheduleNotificationEvent());

        l.forEach(i ->
                verify(scheduler, times(1)).schedule(any(Runnable.class), eq(deadline.plus(i, ChronoUnit.SECONDS).toInstant(ZoneOffset.UTC)))
        );
    }
}