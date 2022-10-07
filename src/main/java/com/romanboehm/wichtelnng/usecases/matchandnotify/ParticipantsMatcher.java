package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Participant;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.util.Collections.rotate;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
class ParticipantsMatcher {

    @Value
    static class Match {
        Donor donor;
        Recipient recipient;

        Match(Donor donor, Recipient recipient) {
            this.donor = requireNonNull(donor);
            this.recipient = requireNonNull(recipient);
            if (donor.getName().equals(recipient.getName()) && donor.getEmail().equals(recipient.getEmail())) {
                throw new IllegalArgumentException("Donor and recipient must not match.");
            }
        }
    }

    static List<Match> match(Set<Participant> participants) {
        List<Participant> original = new ArrayList<>(participants);
        if (original.size() < 2) {
            throw new IllegalArgumentException("Matching needs at least two participants.");
        }
        List<Participant> copy = new ArrayList<>(original);
        Random random = new Random();
        do {
            rotate(copy, random.nextInt());
        } while (areNotMatchedCorrectly(original, copy));

        return range(0, original.size())
                .mapToObj(i -> {
                    Donor donor = new Donor(original.get(i).getName(), original.get(i).getEmail());
                    Recipient recipient = new Recipient(copy.get(i).getName(), copy.get(i).getEmail());
                    Match match = new Match(donor, recipient);
                    log.debug("Created match {}", match);
                    return match;
                }).collect(toList());
    }

    private static boolean areNotMatchedCorrectly(List<Participant> participants, List<Participant> copy) {
        return range(0, participants.size())
                .anyMatch(i -> participants.get(i).equals(copy.get(i)));
    }

}