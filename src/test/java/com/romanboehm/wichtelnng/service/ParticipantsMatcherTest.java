package com.romanboehm.wichtelnng.service;


import com.romanboehm.wichtelnng.model.Participant;
import com.romanboehm.wichtelnng.model.ParticipantsMatch;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ParticipantsMatcherTest {

    @Test
    public void shouldShuffle() {
        ParticipantsMatcher participantsMatcher = new ParticipantsMatcher();
        Participant angusYoung = new Participant();
        angusYoung.setName("Angus Young");
        Participant malcolmYoung = new Participant();
        malcolmYoung.setName("Malcolm Young");
        Participant philRudd = new Participant();
        philRudd.setName("Phil Rudd");

        List<ParticipantsMatch> participantsMatches = participantsMatcher.match(
                List.of(angusYoung, malcolmYoung, philRudd)
        );

        assertThat(participantsMatches).allSatisfy(match ->
                assertThat(match.getDonor().getParticipant()).isNotEqualTo(match.getRecipient().getParticipant())
        );
    }
}