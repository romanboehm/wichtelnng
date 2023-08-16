package com.romanboehm.wichtelnng.usecases.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class essentially deals with re-scheduling the point-in-time-triggered notifications after redeployment.
 */
@Component
class RescheduleNotificationEventsListener {

    static class RescheduleNotificationEvent {
    }

    private final Logger log = LoggerFactory.getLogger(RescheduleNotificationEventsListener.class);
    private final NotifyRepository repository;
    private final NotifyService service;
    private final TaskScheduler scheduler;

    RescheduleNotificationEventsListener(NotifyRepository repository, NotifyService service, TaskScheduler scheduler) {
        this.repository = repository;
        this.service = service;
        this.scheduler = scheduler;
    }

    @EventListener({ ApplicationReadyEvent.class, RescheduleNotificationEvent.class })
    @Transactional
    public void scheduleOutstandingEventNotifications() {
        var count = repository.count();
        // FIXME:
        // Use paging, for example.
        // For now, safeguard, so we don't have `findAll` go crazy.
        if (count > 200) {
            log.warn("Too many events ({}) still pending notification, skipping back-filling scheduler from database.", count);
            return;
        }
        if (count == 0) {
            return;
        }
        var eventsPendingNotification = repository.findAll();
        eventsPendingNotification.forEach(e -> {
            var deadlineInstant = e.getDeadline().localDateTime().atZone(e.getDeadline().zoneId()).toInstant();
            scheduler.schedule(() -> service.notify(e.getId()), deadlineInstant);
        });
        log.debug("Rescheduled notifications for {} events", count);
    }
}
