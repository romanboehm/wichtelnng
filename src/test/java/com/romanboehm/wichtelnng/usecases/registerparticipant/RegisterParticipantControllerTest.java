package com.romanboehm.wichtelnng.usecases.registerparticipant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.romanboehm.wichtelnng.usecases.registerparticipant.RegisterParticipantTestData.eventForRegistration;
import static com.romanboehm.wichtelnng.utils.GlobalTestData.eventFormParams;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RegisterParticipantController.class)
class RegisterParticipantControllerTest {

    @MockBean
    private RegisterParticipantService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validatesParticipant() throws Exception {
        var id = randomUUID();
        when(service.getEvent(id)).thenReturn(eventForRegistration());

        var params = eventFormParams();
        params.add("eventId", id.toString());
        params.add("participantName", "Angus Young");
        params.add("participantEmail", "notavalidemail.address");

        mockMvc.perform(post(format("/event/%s/registration", id))
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(params))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Must be a valid email address.")));
    }

    @Test
    void highlightsRegistrationAttemptTooLate() throws Exception {
        var id = randomUUID();

        doThrow(RegistrationAttemptTooLateException.class).when(service).getEventOpenForRegistration(id);

        mockMvc.perform(get(format("/event/%s/registration", id))
                .contentType(APPLICATION_FORM_URLENCODED))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("It's too late to register")));
    }

    @Test
    void highlightsDuplicateParticipant() throws Exception {
        var id = randomUUID();

        var params = eventFormParams();
        params.add("participantName", "Angus Young");
        params.add("participantEmail", "angusyoung@acdc.net");
        params.add("id", id.toString());

        doThrow(DuplicateParticipantException.class).when(service).register(eq(id), any(RegisterParticipant.class));

        mockMvc.perform(post(format("/event/%s/registration", id))
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(params))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Duplicate Participant")));
    }
}