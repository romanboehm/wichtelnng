package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Participant;
import com.romanboehm.wichtelnng.utils.TestEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.romanboehm.wichtelnng.utils.GlobalTestData.event;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
class NotifyServiceTest {

    @Autowired
    private TestEventRepository eventRepository;

    @Autowired
    private NotifyService service;

    @BeforeEach
    public void cleanup() {
        eventRepository.deleteAll();
    }

    @Test
    void providesAtMostOnceNotificationSemantics() throws ExecutionException, InterruptedException {
        var id = eventRepository.saveAndFlush(
                event()
                        .addParticipant(
                                new Participant()
                                        .setName("Angus Young")
                                        .setEmail("angusyoung@lockmatched.acdc.net"))
                        .addParticipant(
                                new Participant()
                                        .setName("Malcolm Young")
                                        .setEmail("malcolmyoung@lockmatched.acdc.net"))
                        .addParticipant(
                                new Participant()
                                        .setName("Phil Rudd")
                                        .setEmail("philrudd@lockmatched.acdc.net")))
                .getId();

        var executorService = Executors.newFixedThreadPool(2);
        var firstFailed = new AtomicBoolean();
        var secondFailed = new AtomicBoolean();

        var first = executorService.submit(() -> {
            try {
                service.notify(id);
            }
            catch (Exception e) {
                firstFailed.set(true);
            }
        });
        var second = executorService.submit(() -> {
            try {
                service.notify(id);
            }
            catch (Exception e) {
                secondFailed.set(true);
            }
        });
        first.get();
        second.get();

        var oneFailedTheOtherWorked = firstFailed.get() ^ secondFailed.get();
        assertThat(oneFailedTheOtherWorked).isTrue();

    }
}