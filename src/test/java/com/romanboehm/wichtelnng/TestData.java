package com.romanboehm.wichtelnng;

import com.romanboehm.wichtelnng.model.dto.EventDto;
import com.romanboehm.wichtelnng.model.dto.HostDto;
import com.romanboehm.wichtelnng.model.dto.MonetaryAmountDto;
import com.romanboehm.wichtelnng.model.dto.ParticipantDto;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.model.entity.Participant;
import com.romanboehm.wichtelnng.model.util.EventBuilder;
import com.romanboehm.wichtelnng.model.util.ParticipantBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class TestData {

    public static EventTypeStage event() {
        return new EventTypeStage(
                new EventDto()
                        .setTitle("AC/DC Secret Santa")
                        .setDescription("There's gonna be some santa'ing")
                        .setMonetaryAmount(monetaryAmount())
                        .setLocalDate(LocalDate.of(2666, Month.JUNE, 7))
                        .setLocalTime(LocalTime.of(6, 6))
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

    public static ParticipantTypeStage participant() {
        return new ParticipantTypeStage(
                new ParticipantDto()
                        .setName("Angus Young")
                        .setEmail("angusyoung@acdc.net")
        );
    }

    public static class ParticipantTypeStage {
        private final ParticipantDto participant;

        public ParticipantTypeStage(ParticipantDto participant) {
            this.participant = participant;
        }

        public Participant entity() {
            return ParticipantBuilder.fromDto(participant);
        }

        public ParticipantDto dto() {
            return participant;
        }

        public MultiValueMap<String, String> formParams() {
            LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("participant.name", participant.getName());
            map.add("participant.email", participant.getEmail());
            return map;
        }
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
            return EventBuilder.fromDto(event);
        }

        public MultiValueMap<String, String> formParams() {
            LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("event.title", event.getTitle());
            map.add("event.description", event.getDescription());
            map.add("event.monetaryAmount.number", event.getMonetaryAmount().getNumber().toString());
            map.add("event.monetaryAmount.currency", event.getMonetaryAmount().getCurrency().toString());
            map.add("event.localDate", event.getLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            map.add("event.localTime", event.getLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            map.add("event.host.name", event.getHost().getName());
            map.add("event.host.email", event.getHost().getEmail());
            return map;
        }
    }
}
