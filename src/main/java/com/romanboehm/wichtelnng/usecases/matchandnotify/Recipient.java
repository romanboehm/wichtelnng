package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Participant;
import lombok.Value;
import lombok.experimental.Delegate;

@Value
public class Recipient {

    @Delegate
    Participant participant;
}
