package com.romanboehm.wichtelnng.model.util;

import com.romanboehm.wichtelnng.model.dto.ParticipantDto;
import com.romanboehm.wichtelnng.model.entity.Participant;

public class ParticipantBuilder {
    public static Participant fromDto(ParticipantDto dto) {
        return new Participant()
                .setName(dto.getName())
                .setEmail(dto.getEmail());
    }
}
