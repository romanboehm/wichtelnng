package com.romanboehm.wichtelnng.usecases.registerparticipant;

import static com.romanboehm.wichtelnng.utils.GlobalTestData.event;

class RegisterParticipantTestData {
    static RegistrationForm registrationForm() {
        return new RegistrationForm()
                .setParticipantName("Angus Young")
                .setParticipantEmail("angusyoung@acdc.net");
    }

    static EventForRegistration eventForRegistration() {
        return EventForRegistration.from(event());
    }
}
