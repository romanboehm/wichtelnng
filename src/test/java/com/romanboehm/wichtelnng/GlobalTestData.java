package com.romanboehm.wichtelnng;

import com.romanboehm.wichtelnng.data.Deadline;
import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.Host;
import com.romanboehm.wichtelnng.data.MonetaryAmount;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import static java.time.Month.JUNE;
import static javax.money.Monetary.getCurrency;

public class GlobalTestData {

    public static Event event() {
        return new Event()
                .setTitle("AC/DC Secret Santa")
                .setDescription("There's gonna be some santa'ing")
                .setMonetaryAmount(
                        new MonetaryAmount()
                                .setCurrency(getCurrency("AUD").getCurrencyCode())
                                .setNumber(BigDecimal.valueOf(78.50))
                )
                .setDeadline(
                        new Deadline()
                                .setZoneId(ZoneId.of("Australia/Sydney").getId())
                                .setLocalDateTime(
                                        LocalDateTime.of(
                                                LocalDate.of(2666, JUNE, 7),
                                                LocalTime.of(6, 6)
                                        )
                                )
                )
                .setHost(
                        new Host()
                                .setName("George Young")
                                .setEmail("georgeyoung@acdc.net")
                );
    }

    public static MultiValueMap<String, String> eventFormParams() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("title", "AC/DC Secret Santa");
        map.add("description", "There's gonna be some santa'ing");
        map.add("number", "78.50");
        map.add("currency", "AUD");
        map.add("localDate", "2666-06-07");
        map.add("localTime", "06:06");
        map.add("timezone", "Australia/Sydney");
        map.add("hostName", "George Young");
        map.add("hostEmail", "georgeyoung@acdc.net");
        return map;
    }

    public static MultiValueMap<String, String> participantFormParams() {
        MultiValueMap<String, String> participantFormParams = new LinkedMultiValueMap<>();
        participantFormParams.add("participantName", "Angus Young");
        participantFormParams.add("participantEmail", "angusyoung@acdc.net");
        return participantFormParams;
    }
}