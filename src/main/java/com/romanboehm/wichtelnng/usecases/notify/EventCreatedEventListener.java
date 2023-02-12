package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.usecases.createevent.EventCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
class EventCreatedEventListener {

    private final TaskScheduler scheduler;
    private final NotifyService notifyService;

    EventCreatedEventListener(TaskScheduler scheduler, NotifyService notifyService) {
        this.scheduler = scheduler;
        this.notifyService = notifyService;
    }

    @EventListener(EventCreatedEvent.class)
    public void scheduleEventNotification(EventCreatedEvent eventCreatedEvent) {
        scheduler.schedule(
                () -> notifyService.notify(eventCreatedEvent.getEventId()),
                eventCreatedEvent.getEventDeadline());
    }

}
