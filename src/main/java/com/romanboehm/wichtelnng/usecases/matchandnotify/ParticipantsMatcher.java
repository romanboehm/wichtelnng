package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Participant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.Collections.rotate;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

@Slf4j
@Component
public class ParticipantsMatcher {

    List<Match> match(List<Participant> participants) throws TooFewParticipants {
        if (participants.size() < 2) {
            throw new TooFewParticipants("Matching needs at least two participants.");
        }
        List<Participant> copy = new ArrayList<>(participants);
        Random random = new Random();
        do {
            rotate(copy, random.nextInt());
        } while (areNotMatchedCorrectly(participants, copy));

        return range(0, participants.size())
                .mapToObj(i -> {
                    Match match = new Match(
                            new Donor(participants.get(i)),
                            new Recipient(copy.get(i))
                    );
                    log.debug("Created match {}", match);
                    return match;
                }).collect(toList());
    }

    private boolean areNotMatchedCorrectly(List<Participant> participants, List<Participant> copy) {
        boolean areNotMatchedCorrectly = range(0, participants.size())
                .anyMatch(i -> participants.get(i).equals(copy.get(i)));
        if (areNotMatchedCorrectly) {
            log.debug("Failed to provide valid matches by rotating collection");
        }
        return areNotMatchedCorrectly;
    }

}