package com.romanboehm.wichtelnng.controller;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.service.WichtelnService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@WebMvcTest
@AutoConfigureMockMvc
public class WichtelnControllerTest {

    @MockBean
    private WichtelnService wichtelnService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldValidate() throws Exception {
        MultiValueMap<String, String> params = TestData.event().formParams();
        params.set(
                "localDate",
                LocalDate
                        .now().minus(1, ChronoUnit.DAYS)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params)
        )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(
                        "Must take place in the future."
                )));
    }
}