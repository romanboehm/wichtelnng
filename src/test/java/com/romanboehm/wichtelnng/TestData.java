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
        EventDto acdcSanta = new EventDto();
        acdcSanta.setTitle("AC/DC Secret Santa");
        acdcSanta.setDescription("There's gonna be some santa'ing");
        acdcSanta.setMonetaryAmount(monetaryAmount());
        acdcSanta.setLocalDate(LocalDate.of(2666, Month.JUNE, 7));
        acdcSanta.setLocalTime(LocalTime.of(6, 6));
        acdcSanta.setPlace("Sydney Harbor");
        acdcSanta.setHost(host());
        return new EventTypeStage(acdcSanta);
    }

    public static HostDto host() {
        HostDto georgeYoung = new HostDto();
        georgeYoung.setName("George Young");
        georgeYoung.setEmail("georgeyoung@acdc.net");
        return georgeYoung;
    }

    public static MonetaryAmountDto monetaryAmount() {
        MonetaryAmountDto monetaryAmount = new MonetaryAmountDto();
        monetaryAmount.setCurrency(Monetary.getCurrency("AUD"));
        monetaryAmount.setNumber(BigDecimal.valueOf(78.50));
        return monetaryAmount;
    }

    public static ParticipantDto participant() {
        ParticipantDto angusYoung = new ParticipantDto();
        angusYoung.setName("Angus Young");
        angusYoung.setEmail("angusyoung@acdc.net");
        return angusYoung;
    }

    public static class EventTypeStage {
        private final EventDto event;

        public EventTypeStage(EventDto event) {
            this.event = event;
        }

        public EventDto asDto() {
            return event;
        }

        public EventTypeStage modifying(Consumer<EventDto> eventModifier) {
            eventModifier.accept(this.event);
            return this;
        }

        public MultiValueMap<String, String> asFormParams() {
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

        public Event asEntity() {
            return EventBuilder.from(event);
        }
    }
}
