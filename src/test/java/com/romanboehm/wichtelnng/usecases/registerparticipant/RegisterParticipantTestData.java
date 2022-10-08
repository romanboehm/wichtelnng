package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.GlobalTestData;

class RegisterParticipantTestData {
    static RegisterParticipant participantRegistration() {
        return RegisterParticipant.registerFor(GlobalTestData.event())
                .setParticipantName("Angus Young")
                .setParticipantEmail("angusyoung@acdc.net");
    }
}
