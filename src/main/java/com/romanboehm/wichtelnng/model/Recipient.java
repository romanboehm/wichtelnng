package com.romanboehm.wichtelnng.model;

import com.romanboehm.wichtelnng.model.entity.Participant;
import lombok.Value;
import lombok.experimental.Delegate;

@Value
public class Recipient {

    @Delegate
    Participant participant;
}
