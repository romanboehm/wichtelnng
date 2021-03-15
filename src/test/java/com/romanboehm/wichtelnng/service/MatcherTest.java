package com.romanboehm.wichtelnng.service;


import com.romanboehm.wichtelnng.exception.TooFewParticipantsException;
import com.romanboehm.wichtelnng.model.Match;
import com.romanboehm.wichtelnng.model.entity.Participant;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        assertThat(matches).allSatisfy(match ->
                assertThat(match.getDonor().getParticipant())
                        .isNotEqualTo(match.getRecipient().getParticipant())
        );
    }

    @Test
    public void shouldNotShuffleIfTooFewParticipants() {
        List<Participant> oneTooFew = List.of(
                new Participant()
                        .setName("Angus Young")
        );

        assertThatThrownBy(() -> new Matcher().match(oneTooFew))
                .isInstanceOf(TooFewParticipantsException.class);
    }
}