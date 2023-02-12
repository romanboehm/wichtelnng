package com.romanboehm.wichtelnng.usecases.registerparticipant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import static com.romanboehm.wichtelnng.GlobalTestData.eventFormParams;
import static com.romanboehm.wichtelnng.GlobalTestData.participantFormParams;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
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
    void shouldValidateParticipant() throws Exception {
        MultiValueMap<String, String> params = eventFormParams();
        params.addAll(participantFormParams());
        params.set("participantEmail", "notavalidemail.address");

        mockMvc.perform(post(format("/event/%s/registration", randomUUID()))
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(params))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Must be a valid email address.")));
    }
}