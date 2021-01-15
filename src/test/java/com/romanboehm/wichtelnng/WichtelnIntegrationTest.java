package com.romanboehm.wichtelnng;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.romanboehm.wichtelnng.TestData;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class WichtelnIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldDoGetFormSaveProvideLinkFlow() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/wichteln"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(TestData.event().asFormParams())
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.stringContainsInOrder(
                        "Provide this link to everyone you wish to participate in your Wichteln event <span>\"AC/DC Secret Santa\"</span>",
                        "http://localhost/wichteln/"
                )));
    }
}