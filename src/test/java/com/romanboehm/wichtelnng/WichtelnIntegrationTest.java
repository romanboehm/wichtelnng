package com.romanboehm.wichtelnng;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;

@SpringBootTest
@AutoConfigureMockMvc
public class WichtelnIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldDoGetFormSaveProvideLinkFlow() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/wichteln"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        MvcResult saveResult = mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(TestData.event().formParams())
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()).andReturn();

        URI link = (URI) saveResult.getModelAndView().getModelMap().get("link");

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/wichteln/result?link=%s", link)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.stringContainsInOrder(
                        "Provide this link to everyone you wish to participate in your Wichteln event",
                        link.toString()
                )));
    }
}