package com.romanboehm.wichtelnng.usecases.createevent;

import com.romanboehm.wichtelnng.utils.TestEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;

import static com.romanboehm.wichtelnng.usecases.createevent.CreateEventTestData.createEvent;
import static java.time.Month.JUNE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
@RecordApplicationEvents
class CreateEventServiceTest {

    @Autowired
    private TestEventRepository eventRepository;

    @Autowired
    private CreateEventService service;

    @Autowired
    private ApplicationEvents applicationEvents;

    @BeforeEach
    public void cleanup() {
        applicationEvents.clear();
        eventRepository.deleteAll();
    }

    @Test
    void savesEventAndNotifiesOfCreation() throws DuplicateEventException {
        var eventId = service.save(new CreateEvent()
                .setTitle("AC/DC Secret Santa")
                .setDescription("There's gonna be some santa'ing")
                .setNumber(new BigDecimal("78.50"))
                .setCurrency(Currency.getInstance("AUD"))
                .setHostName("George Young")
                .setHostEmail("georgeyoung@acdc.net")
                .setLocalDate(LocalDate.of(LocalDate.now().getYear() + 1, JUNE, 7))
                .setLocalTime(LocalTime.of(6, 6))
                .setTimezone(ZoneId.of("Australia/Sydney")));

        assertThat(eventRepository.findById(eventId))
                .hasValueSatisfying(event -> {
                    assertThat(event.getId()).isNotNull();

                    assertThat(event.getTitle()).isEqualTo("AC/DC Secret Santa");
                    assertThat(event.getDescription()).isEqualTo("There's gonna be some santa'ing");
                    assertThat(event.getMonetaryAmount().getNumber()).isEqualByComparingTo("78.50");
                    assertThat(event.getMonetaryAmount().getCurrency()).isEqualTo(Currency.getInstance("AUD"));
                    assertThat(event.getHost().getName()).isEqualTo("George Young");
                    assertThat(event.getHost().getEmail()).isEqualTo("georgeyoung@acdc.net");
                    assertThat(event.getDeadline().getZoneId()).isEqualTo("Australia/Sydney");
                    assertThat(event.getDeadline().getLocalDateTime()).isEqualTo(LocalDateTime.of(
                            LocalDate.of(LocalDate.now().getYear() + 1, JUNE, 7),
                            LocalTime.of(6, 6)));
                });
        assertThat(applicationEvents.stream(EventCreatedEvent.class))
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.getEventId()).isEqualTo(eventId);
                    assertThat(e.getEventDeadline()).isEqualTo(ZonedDateTime.of(
                            LocalDate.of(LocalDate.now().getYear() + 1, JUNE, 7),
                            LocalTime.of(6, 6),
                            ZoneId.of("Australia/Sydney")).toInstant());
                });
    }

    @Test
    void preventsDuplicateEvent() throws DuplicateEventException {
        service.save(createEvent());
        assertThat(applicationEvents.stream(EventCreatedEvent.class)).hasSize(1);
        applicationEvents.clear();

        assertThatThrownBy(() -> service.save(createEvent())).isInstanceOf(DuplicateEventException.class);

        assertThat(applicationEvents.stream(EventCreatedEvent.class)).isEmpty();
    }

}