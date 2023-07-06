package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.usecases.createevent.EventCreatedEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class EventCreatedEventListener {

    private final TaskScheduler scheduler;
    private final NotifyService notifyService;

    EventCreatedEventListener(TaskScheduler scheduler, NotifyService notifyService) {
        this.scheduler = scheduler;
        this.notifyService = notifyService;
    }

    @TransactionalEventListener(value = EventCreatedEvent.class, phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void scheduleEventNotification(EventCreatedEvent eventCreatedEvent) {
        scheduler.schedule(
                () -> notifyService.notify(eventCreatedEvent.getEventId()),
                eventCreatedEvent.getEventDeadline());
    }

}
