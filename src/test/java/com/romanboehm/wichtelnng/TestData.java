package com.romanboehm.wichtelnng;

import com.romanboehm.wichtelnng.model.dto.EventCreation;
import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.model.entity.Host;
import com.romanboehm.wichtelnng.model.entity.MonetaryAmount;
import com.romanboehm.wichtelnng.model.entity.Participant;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TestData {

    public static Event event() {
        return new Event()
                .setTitle("AC/DC Secret Santa")
                .setDescription("There's gonna be some santa'ing")
                .setMonetaryAmount(monetaryAmount())
                .setZonedDateTime(
                        ZonedDateTime.of(
                                LocalDate.of(2666, Month.JUNE, 7),
                                LocalTime.of(6, 6),
                                ZoneId.of("Australia/Sydney"))
                )
                .setHost(host());
    }

    public static Host host() {
        return new Host()
                .setName("George Young")
                .setEmail("georgeyoung@acdc.net");
    }

    public static MonetaryAmount monetaryAmount() {
        return new MonetaryAmount()
                .setCurrency(Monetary.getCurrency("AUD").getCurrencyCode())
                .setNumber(BigDecimal.valueOf(78.50));
    }

    public static Participant participant() {
        return new Participant()
                .setName("Angus Young")
                .setEmail("angusyoung@acdc.net");
    }

    public static EventCreation eventCreation() {
        return EventCreation.from(event());
    }

    public static MultiValueMap<String, String> eventFormParams() {
        EventCreation eventCreation = eventCreation();
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("title", eventCreation.getTitle());
        map.add("description", eventCreation.getDescription());
        map.add("number", eventCreation.getNumber().toString());
        map.add("currency", eventCreation.getCurrency().toString());
        map.add("localDate", eventCreation.getLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        map.add("localTime", eventCreation.getLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        map.add("timezone", eventCreation.getTimezone().toString());
        map.add("hostName", eventCreation.getHostName());
        map.add("hostEmail", eventCreation.getHostEmail());
        return map;
    }

    public static ParticipantRegistration participantRegistration() {
        Participant participant = participant();
        return ParticipantRegistration.with(eventCreation())
                .setParticipantName(participant.getName())
                .setParticipantEmail(participant.getEmail());
    }

    public static MultiValueMap<String, String> participantFormParams() {
        Participant participant = participant();
        MultiValueMap<String, String> participantFormParams = new LinkedMultiValueMap<>();
        participantFormParams.add("participantName", participant.getName());
        participantFormParams.add("participantEmail", participant.getEmail());
        return participantFormParams;
    }
}
