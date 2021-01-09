package com.romanboehm.wichtelnng.service;

import com.romanboehm.wichtelnng.model.Participant;
import com.romanboehm.wichtelnng.model.ParticipantsMatch;
import com.romanboehm.wichtelnng.model.ParticipantsMatch.Donor;
import com.romanboehm.wichtelnng.model.ParticipantsMatch.Recipient;
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
public class ParticipantsMatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantsMatcher.class);

    public List<ParticipantsMatch> match(List<Participant> participants) {
        List<Participant> copy = new ArrayList<>(participants);
        Random random = new Random();
        do {
            Collections.rotate(copy, random.nextInt());
        } while (areNotMatchedCorrectly(participants, copy));

        return IntStream.range(0, participants.size())
                .mapToObj(i -> {
                    ParticipantsMatch participantsMatch = new ParticipantsMatch(
                            new Donor(participants.get(i)),
                            new Recipient(copy.get(i))
                    );
                    LOGGER.debug("Created match {}", participantsMatch);
                    return participantsMatch;
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