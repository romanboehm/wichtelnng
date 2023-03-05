package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.utils.GlobalTestData;

class RegisterParticipantTestData {
    static RegisterParticipant participantRegistration() {
        return RegisterParticipant.registerFor(GlobalTestData.event())
                .setParticipantName("Angus Young")
                .setParticipantEmail("angusyoung@acdc.net");
    }
}
