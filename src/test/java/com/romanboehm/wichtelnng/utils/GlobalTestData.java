package com.romanboehm.wichtelnng.utils;

import com.romanboehm.wichtelnng.common.data.Deadline;
import com.romanboehm.wichtelnng.common.data.Event;
import com.romanboehm.wichtelnng.common.data.Host;
import com.romanboehm.wichtelnng.common.data.MonetaryAmount;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Currency;

import static java.time.Month.JUNE;

public class GlobalTestData {

    public static Event event() {
        return new Event()
                .setTitle("AC/DC Secret Santa")
                .setDescription("There's gonna be some santa'ing")
                .setMonetaryAmount(
                        new MonetaryAmount(BigDecimal.valueOf(78.50), Currency.getInstance("AUD")))
                .setDeadline(
                        new Deadline(LocalDateTime.of(
                                LocalDate.of(LocalDate.now().getYear() + 1, JUNE, 7),
                                LocalTime.of(6, 6)), ZoneId.of("Australia/Sydney")))
                .setHost(
                        new Host("George Young", "georgeyoung@acdc.net"));
    }

    public static MultiValueMap<String, String> eventFormParams() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("title", "AC/DC Secret Santa");
        map.add("description", "There's gonna be some santa'ing");
        map.add("number", "78.50");
        map.add("currency", "AUD");
        map.add("localDate", "%s-06-07".formatted(LocalDate.now().getYear() + 1));
        map.add("localTime", "06:06");
        map.add("timezone", "Australia/Sydney");
        map.add("hostName", "George Young");
        map.add("hostEmail", "georgeyoung@acdc.net");
        return map;
    }

}
