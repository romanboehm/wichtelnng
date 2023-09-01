package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Deadline;
import com.romanboehm.wichtelnng.utils.TestEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Stream;

import static com.romanboehm.wichtelnng.utils.GlobalTestData.event;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

    @Autowired
    private TaskScheduler scheduler;

    @BeforeEach
    public void setUp() {
        Mockito.reset(scheduler);
        eventRepository.deleteAll();
    }

    @Test
    void schedulesNotificationForAllPendingEvents() {
        var now = LocalDateTime.now();
        var deadlines = Stream.of(1, 2, 3).map(i -> new Deadline(now.plusSeconds(i), ZoneId.of("UTC"))).toList();
        eventRepository.saveAllAndFlush(
                deadlines.stream()
                        .map(dl -> event().setDeadline(dl))
                        .toList());

        eventPublisher.publishEvent(new RescheduleNotificationEventsListener.RescheduleNotificationEvent());

        var captor = ArgumentCaptor.forClass(Instant.class);
        verify(scheduler, times(deadlines.size())).schedule(any(Runnable.class), captor.capture());

        assertThat(captor.getAllValues().stream().map(Instant::toEpochMilli))
                .containsExactlyInAnyOrderElementsOf(deadlines.stream().map(Deadline::asInstant).map(Instant::toEpochMilli).toList());
    }
}