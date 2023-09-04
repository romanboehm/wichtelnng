package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Deadline;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * This class essentially deals with re-scheduling the point-in-time-triggered notifications after redeployment.
 */
@Component
class RescheduleNotificationEventsListener {

    static class RescheduleNotificationEvent {
    }

    private final Logger log = LoggerFactory.getLogger(RescheduleNotificationEventsListener.class);
    private final Session session;
    private final NotifyService service;
    private final TaskScheduler scheduler;

    RescheduleNotificationEventsListener(EntityManager em, NotifyService service, TaskScheduler scheduler) {
        this.session = em.unwrap(Session.class);
        this.service = service;
        this.scheduler = scheduler;
    }

    @EventListener({ ApplicationReadyEvent.class, RescheduleNotificationEvent.class })
    @Transactional
    public void scheduleOutstandingEventNotifications() {
        var count = session.createSelectionQuery("select count(e) from Event e", Long.class).getSingleResult();
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

        record EventForNotification(UUID eventId, Deadline deadline) {
        }
        var eventsPendingNotification = session.createSelectionQuery("select e.id, e.deadline from Event e", EventForNotification.class).getResultList();
        eventsPendingNotification.forEach(e -> scheduler.schedule(() -> service.notify(e.eventId()), e.deadline().asInstant()));
        log.debug("Rescheduled notifications for {} events", count);
    }
}
