package com.romanboehm.wichtelnng.usecases.createevent;

import com.romanboehm.wichtelnng.data.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.romanboehm.wichtelnng.TestData.createEvent;
import static java.time.Month.JUNE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CreateEventServiceTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CreateEventService service;

    @AfterEach
    public void cleanup() {
        eventRepository.deleteAll();
    }

    @Test
    void shouldSave() {
        service.save(createEvent());
        assertThat(eventRepository.findAll())
                .hasOnlyOneElementSatisfying(event -> {
                    assertThat(event.getId()).isNotNull();

                    assertThat(event.getTitle()).isEqualTo("AC/DC Secret Santa");
                    assertThat(event.getDescription()).isEqualTo("There's gonna be some santa'ing");
                    assertThat(event.getMonetaryAmount().getNumber()).isEqualByComparingTo("78.50");
                    assertThat(event.getMonetaryAmount().getCurrency()).isEqualTo("AUD");
                    assertThat(event.getHost().getName()).isEqualTo("George Young");
                    assertThat(event.getHost().getEmail()).isEqualTo("georgeyoung@acdc.net");
                    assertThat(event.getMonetaryAmount().getCurrency()).isEqualTo("AUD");
                    assertThat(event.getMonetaryAmount().getCurrency()).isEqualTo("AUD");
                    assertThat(event.getDeadline().asInstant()).isEqualTo(
                            ZonedDateTime.of(
                                    LocalDate.of(2666, JUNE, 7),
                                    LocalTime.of(6, 6),
                                    ZoneId.of("Australia/Sydney")
                            ).toInstant()
                    );
                });
    }

    @Test
    void shouldThrowOnDuplicateEvent() {
        service.save(createEvent());
        assertThatThrownBy(() -> service.save(createEvent()))
                .isInstanceOf(RuntimeException.class);
    }

}