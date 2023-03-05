package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.util.Collections.rotate;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

class ParticipantsMatcher {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantsMatcher.class);

    private ParticipantsMatcher() {
    }

    record Match(Donor donor, Recipient recipient) {
        static Match of(Donor donor, Recipient recipient) {
            if (donor.name().equals(recipient.name()) && donor.email().equals(recipient.email())) {
                throw new IllegalArgumentException("Donor and recipient must not match.");
            }
            return new Match(donor, recipient);
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
                    Match match = Match.of(donor, recipient);
                    LOG.debug("Created match {}", match);
                    return match;
                }).collect(toList());
    }

    private static boolean areNotMatchedCorrectly(List<Participant> participants, List<Participant> copy) {
        return range(0, participants.size())
                .anyMatch(i -> participants.get(i).equals(copy.get(i)));
    }

}