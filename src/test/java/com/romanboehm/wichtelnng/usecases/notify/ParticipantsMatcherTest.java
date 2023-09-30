package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Participant;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParticipantsMatcherTest {

    @Test
    void shouldShuffle() {
        List<Match> matches = ParticipantsMatcher.match(
                Set.of(new Participant()
                        .setName("Angus Young")
                        .setEmail("angusyoung@acdc.net"),
                        new Participant()
                                .setName("Malcolm Young")
                                .setEmail("malcolmyoung@acdc.net"),
                        new Participant()
                                .setName("Phil Rudd")
                                .setEmail("philrudd@acdc.net")));

        assertThat(matches).allSatisfy(match -> {
            assertThat(match.donor().name()).isNotEqualTo(match.recipient().name());
            assertThat(match.donor().email()).isNotEqualTo(match.recipient().email());
        });
    }

    @Test
    void shouldNotShuffleIfTooFewParticipants() {
        Set<Participant> oneTooFewParticipants = Set.of(new Participant()
                .setName("Angus Young")
                .setEmail("angusyoung@acdc.net"));

        assertThatThrownBy(() -> ParticipantsMatcher.match(oneTooFewParticipants))
                .isInstanceOf(IllegalArgumentException.class);
    }
}