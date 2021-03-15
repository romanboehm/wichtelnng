package com.romanboehm.wichtelnng.service;


import com.romanboehm.wichtelnng.exception.TooFewParticipantsException;
import com.romanboehm.wichtelnng.model.Match;
import com.romanboehm.wichtelnng.model.entity.Participant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MatcherTest {

    @Test
    public void shouldShuffle() throws TooFewParticipantsException {
        List<Match> matches = new Matcher().match(List.of(
                new Participant()
                        .setName("Angus Young"),
                new Participant()
                        .setName("Malcolm Young"),
                new Participant()
                        .setName("Phil Rudd")
        ));

        Assertions.assertThat(matches).allSatisfy(match ->
                Assertions.assertThat(match.getDonor().getParticipant())
                        .isNotEqualTo(match.getRecipient().getParticipant())
        );
    }

    @Test
    public void shouldNotShuffleIfTooFewParticipants() {
        List<Participant> oneTooFew = List.of(
                new Participant()
                        .setName("Angus Young")
        );

        Assertions.assertThatThrownBy(() -> new Matcher().match(oneTooFew))
                .isInstanceOf(TooFewParticipantsException.class);
    }
}