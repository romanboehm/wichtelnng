package com.romanboehm.wichtelnng.service;


import com.romanboehm.wichtelnng.model.Match;
import com.romanboehm.wichtelnng.model.entity.Participant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MatcherTest {

    @Test
    public void shouldShuffle() {
        Matcher matcher = new Matcher();
        Participant angusYoung = new Participant();
        angusYoung.setName("Angus Young");
        Participant malcolmYoung = new Participant();
        malcolmYoung.setName("Malcolm Young");
        Participant philRudd = new Participant();
        philRudd.setName("Phil Rudd");

        List<Match> matches = matcher.match(
                List.of(angusYoung, malcolmYoung, philRudd)
        );

        Assertions.assertThat(matches).allSatisfy(match ->
                Assertions.assertThat(match.getDonor().getParticipant())
                        .isNotEqualTo(match.getRecipient().getParticipant())
        );
    }
}