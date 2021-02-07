package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.Match;
import com.romanboehm.wichtelnng.model.Donor;
import com.romanboehm.wichtelnng.model.Recipient;
import com.romanboehm.wichtelnng.model.entity.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class Matcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Matcher.class);

    public List<Match> match(List<Participant> participants) {
        if (participants.size() < 2) {
            throw new IllegalArgumentException("Matching needs at least two participants.");
        }
        List<Participant> copy = new ArrayList<>(participants);
        Random random = new Random();
        do {
            Collections.rotate(copy, random.nextInt());
        } while (areNotMatchedCorrectly(participants, copy));

        return IntStream.range(0, participants.size())
                .mapToObj(i -> {
                    Match match = new Match(
                            new Donor(participants.get(i)),
                            new Recipient(copy.get(i))
                    );
                    LOGGER.debug("Created match {}", match);
                    return match;
                        }
                ).collect(Collectors.toList());
    }

    private boolean areNotMatchedCorrectly(List<Participant> participants, List<Participant> copy) {
        boolean areNotMatchedCorrectly = IntStream.range(0, participants.size())
                .anyMatch(i -> participants.get(i).equals(copy.get(i)));
        if (areNotMatchedCorrectly) {
            LOGGER.debug("Failed to provide valid matches by rotating collection");
        }
        return areNotMatchedCorrectly;
    }

}