package com.romanboehm.wichtelnng.usecases.createevent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.romanboehm.wichtelnng.utils.GlobalTestData.eventFormParams;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CreateEventController.class)
class CreateEventControllerTest {

    @MockBean
    private CreateEventService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void redirectsFromRootToEvent() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void validatesEvent() throws Exception {
        MultiValueMap<String, String> params = eventFormParams();
        params.set(
                "localDate",
                LocalDate
                        .now().minus(1, DAYS)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        mockMvc.perform(post("/event")
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(params))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString(
                        "Must take place in the future.")));
    }

    @Test
    void redirectsAfterCreateComplete() throws Exception {
        mockMvc.perform(post("/event")
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(eventFormParams()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void highlightsDuplicateEvent() throws Exception {
        MultiValueMap<String, String> params = eventFormParams();
        when(service.save(any(CreateEvent.class)))
                .thenReturn(UUID.randomUUID())
                .thenThrow(DuplicateEventException.class);

        mockMvc.perform(post("/event")
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(params))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/event")
                .contentType(APPLICATION_FORM_URLENCODED)
                .params(params))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString(
                        "You're seeing this page because you tried to create a Wichteln event which already exists.")));
    }
}