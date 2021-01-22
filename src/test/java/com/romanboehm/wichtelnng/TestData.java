package com.romanboehm.wichtelnng;

import com.romanboehm.wichtelnng.model.dto.EventDto;
import com.romanboehm.wichtelnng.model.dto.HostDto;
import com.romanboehm.wichtelnng.model.dto.MonetaryAmountDto;
import com.romanboehm.wichtelnng.model.dto.ParticipantDto;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.model.util.EventBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class TestData {

    public static EventTypeStage event() {
        return new EventTypeStage(
                new EventDto()
                        .setTitle("AC/DC Secret Santa")
                        .setDescription("There's gonna be some santa'ing")
                        .setMonetaryAmount(monetaryAmount())
                        .setLocalDate(LocalDate.of(2666, Month.JUNE, 7))
                        .setLocalTime(LocalTime.of(6, 6))
                        .setPlace("Sydney Harbor")
                        .setHost(host())
        );
    }

    public static HostDto host() {
        return new HostDto()
                .setName("George Young")
                .setEmail("georgeyoung@acdc.net");
    }

    public static MonetaryAmountDto monetaryAmount() {
        return new MonetaryAmountDto()
                .setCurrency(Monetary.getCurrency("AUD"))
                .setNumber(BigDecimal.valueOf(78.50));
    }

    public static ParticipantDto participant() {
        return new ParticipantDto()
                .setName("Angus Young")
                .setEmail("angusyoung@acdc.net");
    }

    public static class EventTypeStage {
        private final EventDto event;

        public EventTypeStage(EventDto event) {
            this.event = event;
        }

        public EventDto dto() {
            return event;
        }

        public Event entity() {
            return EventBuilder.from(event);
        }

        public MultiValueMap<String, String> formParams() {
            LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("title", event.getTitle());
            map.add("description", event.getDescription());
            map.add("monetaryAmount.number", event.getMonetaryAmount().getNumber().toString());
            map.add("monetaryAmount.currency", event.getMonetaryAmount().getCurrency().toString());
            map.add("localDate", event.getLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            map.add("localTime", event.getLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            map.add("place", event.getPlace());
            map.add("host.name", event.getHost().getName());
            map.add("host.email", event.getHost().getEmail());
            return map;
        }
    }
}
