package com.romanboehm.wichtelnng.controller;

import com.romanboehm.wichtelnng.service.WichtelnService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.romanboehm.wichtelnng.TestData.eventFormParams;
import static com.romanboehm.wichtelnng.TestData.participantFormParams;
import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WichtelnController.class)
public class WichtelnControllerTest {

    @MockBean
    private WichtelnService wichtelnService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldValidateEvent() throws Exception {
        MultiValueMap<String, String> params = eventFormParams();
        params.set(
                "localDate",
                LocalDate
                        .now().minus(1, DAYS)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );

        mockMvc.perform(post("/wichteln/save")
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(params)
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString(
                        "Must take place in the future."
                )));
    }

    @Test
    public void shouldValidateParticipant() throws Exception {
        MultiValueMap<String, String> params = eventFormParams();
        params.addAll(participantFormParams());
        params.set("participantEmail", "notavalidemail.address");

        mockMvc.perform(post(format("/wichteln/%s/register", randomUUID()))
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(params)
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Must be a valid email address.")));
    }
}