package com.romanboehm.wichtelnng.service;


import com.romanboehm.wichtelnng.model.dto.ParticipantDto;
import com.romanboehm.wichtelnng.model.dto.ParticipantsMatch;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ParticipantsMatcherTest {

    @Test
    public void shouldShuffle() {
        ParticipantsMatcher participantsMatcher = new ParticipantsMatcher();
        ParticipantDto angusYoung = new ParticipantDto();
        angusYoung.setName("Angus Young");
        ParticipantDto malcolmYoung = new ParticipantDto();
        malcolmYoung.setName("Malcolm Young");
        ParticipantDto philRudd = new ParticipantDto();
        philRudd.setName("Phil Rudd");

        List<ParticipantsMatch> participantsMatches = participantsMatcher.match(
                List.of(angusYoung, malcolmYoung, philRudd)
        );

        Assertions.assertThat(participantsMatches).allSatisfy(match ->
                Assertions.assertThat(match.getDonor().getParticipant())
                        .isNotEqualTo(match.getRecipient().getParticipant())
        );
    }
}