package com.romanboehm.wichtelnng.usecases.createevent;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.romanboehm.wichtelnng.data.TestEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.icegreen.greenmail.configuration.GreenMailConfiguration.aConfig;
import static com.icegreen.greenmail.util.ServerSetupTest.SMTP_IMAP;
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

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP_IMAP)
            .withConfiguration(aConfig().withDisabledAuthentication());

    @BeforeEach
    public void cleanup() {
        applicationEvents.clear();
        eventRepository.deleteAll();
        eventRepository.flush();
    }

    @Test
    void shouldSaveEvent() {
        service.save(new CreateEvent()
                .setTitle("AC/DC Secret Santa")
                .setDescription("There's gonna be some santa'ing")
                .setNumber(new BigDecimal("78.50"))
                .setCurrency(Monetary.getCurrency("AUD"))
                .setHostName("George Young")
                .setHostEmail("georgeyoung@acdc.net")
                .setLocalDate(LocalDate.of(2666, JUNE, 7))
                .setLocalTime(LocalTime.of(6, 6))
                .setTimezone(ZoneId.of("Australia/Sydney"))
        );

        assertThat(eventRepository.findAll())
                .singleElement()
                .satisfies(event -> {
                    assertThat(event.getId()).isNotNull();

                    assertThat(event.getTitle()).isEqualTo("AC/DC Secret Santa");
                    assertThat(event.getDescription()).isEqualTo("There's gonna be some santa'ing");
                    assertThat(event.getMonetaryAmount().getNumber()).isEqualByComparingTo("78.50");
                    assertThat(event.getMonetaryAmount().getCurrency()).isEqualTo("AUD");
                    assertThat(event.getHost().getName()).isEqualTo("George Young");
                    assertThat(event.getHost().getEmail()).isEqualTo("georgeyoung@acdc.net");
                    assertThat(event.getMonetaryAmount().getCurrency()).isEqualTo("AUD");
                    assertThat(event.getMonetaryAmount().getCurrency()).isEqualTo("AUD");
                    assertThat(event.getDeadline().getInstant()).isEqualTo(
                            ZonedDateTime.of(
                                    LocalDate.of(2666, JUNE, 7),
                                    LocalTime.of(6, 6),
                                    ZoneId.of("Australia/Sydney")
                            ).toInstant()
                    );
                });
    }

    @Test
    void shouldNotifyOfEventCreation() {
        var createEvent = createEvent();

        var eventId = service.save(createEvent);

        assertThat(applicationEvents.stream(EventCreatedEvent.class))
                .singleElement()
                .satisfies(e -> {
                    assertThat(e.getEventId()).isEqualTo(eventId);
                    assertThat(e.getEventDeadline()).isEqualTo(createEvent.getInstant());
                });
    }

    @Test
    void shouldThrowOnDuplicateEvent() {
        service.save(createEvent().setLocalTime(LocalTime.MIDNIGHT));
        assertThat(applicationEvents.stream(EventCreatedEvent.class)).hasSize(1);
        applicationEvents.clear();

        assertThatThrownBy(() -> service.save(createEvent().setLocalTime(LocalTime.MIDNIGHT))).isInstanceOf(RuntimeException.class);

        assertThat(applicationEvents.stream(EventCreatedEvent.class)).isEmpty();
    }

}