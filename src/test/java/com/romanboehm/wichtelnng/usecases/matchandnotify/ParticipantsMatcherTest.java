package com.romanboehm.wichtelnng.usecases.matchandnotify;


import com.romanboehm.wichtelnng.data.Participant;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParticipantsMatcherTest {

    @Test
    void shouldShuffle() throws TooFewParticipants {
        List<Match> matches = new ParticipantsMatcher().match(List.of(
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
    void shouldNotShuffleIfTooFewParticipants() {
        List<Participant> oneTooFew = List.of(
                new Participant()
                        .setName("Angus Young")
        );

        assertThatThrownBy(() -> new ParticipantsMatcher().match(oneTooFew))
                .isInstanceOf(TooFewParticipants.class);
    }
}