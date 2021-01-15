package com.romanboehm.wichtelnng;

import com.romanboehm.wichtelnng.model.Event;
import com.romanboehm.wichtelnng.model.Host;
import com.romanboehm.wichtelnng.model.MonetaryAmount;
import com.romanboehm.wichtelnng.model.Participant;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class TestData {

    public static EventTypeStage event() {
        Event acdcSanta = new Event();
        acdcSanta.setTitle("AC/DC Secret Santa");
        acdcSanta.setDescription("There's gonna be some santa'ing");
        acdcSanta.setMonetaryAmount(monetaryAmount());
        acdcSanta.setLocalDate(LocalDate.of(2666, Month.JUNE, 7));
        acdcSanta.setLocalTime(LocalTime.of(6, 6));
        acdcSanta.setPlace("Sydney Harbor");
        acdcSanta.setHost(host());
        return new EventTypeStage(acdcSanta);
    }

    public static Host host() {
        Host georgeYoung = new Host();
        georgeYoung.setName("George Young");
        georgeYoung.setEmail("georgeyoung@acdc.net");
        return georgeYoung;
    }

    public static MonetaryAmount monetaryAmount() {
        MonetaryAmount monetaryAmount = new MonetaryAmount();
        monetaryAmount.setCurrency(Monetary.getCurrency("AUD"));
        monetaryAmount.setNumber(BigDecimal.valueOf(78.50));
        return monetaryAmount;
    }

    public static Participant participant() {
        Participant angusYoung = new Participant();
        angusYoung.setName("Angus Young");
        angusYoung.setEmail("angusyoung@acdc.net");
        return angusYoung;
    }

    public static class EventTypeStage {
        private final Event event;

        public EventTypeStage(Event event) {
            this.event = event;
        }

        public Event asObject() {
            return event;
        }

        public EventTypeStage modifying(Consumer<Event> eventModifier) {
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
    }
}
