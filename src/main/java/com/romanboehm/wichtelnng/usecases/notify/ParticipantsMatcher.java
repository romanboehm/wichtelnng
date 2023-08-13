package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

    static List<Match> match(Collection<Participant> participants) {
        var original = new ArrayList<>(participants);
        if (original.size() < 2) {
            throw new IllegalArgumentException("Matching needs at least two participants.");
        }
        var copy = new ArrayList<>(original);
        var distance = ThreadLocalRandom.current().nextInt(1, copy.size() - 1);

        Collections.rotate(copy, distance);

        return range(0, original.size())
                .mapToObj(i -> {
                    var donor = new Donor(original.get(i).getName(), original.get(i).getEmail());
                    var recipient = new Recipient(copy.get(i).getName(), copy.get(i).getEmail());
                    var match = Match.of(donor, recipient);
                    LOG.debug("Created match {}", match);
                    return match;
                }).collect(toList());
    }

}