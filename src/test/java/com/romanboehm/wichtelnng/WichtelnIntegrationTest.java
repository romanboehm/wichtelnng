package com.romanboehm.wichtelnng;

import com.romanboehm.wichtelnng.repository.EventRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class WichtelnIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventRepository eventRepository;

    @Test
    public void shouldDoGetFormSaveProvideLinkFlow() throws Exception {
        UUID id = UUID.nameUUIDFromBytes("acdc-secret-santa".getBytes(StandardCharsets.UTF_8));
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(TestData.event().asEntity().setId(id));

        mockMvc.perform(MockMvcRequestBuilders.get("/wichteln"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(TestData.event().asFormParams())
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.stringContainsInOrder(
                        "Provide this link to everyone you wish to participate in your Wichteln event:",
                        "https://wichtelnng.romanboehm.com/wichteln/" + id
                )));
    }
}