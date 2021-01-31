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
import org.springframework.util.MultiValueMap;

@SpringBootTest
@AutoConfigureMockMvc
public class WichtelnIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldDoGetFormSaveProvideLinkRegisterFlow() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/wichteln"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        MultiValueMap<String, String> params = TestData.event().formParams();
        MvcResult saveResult = mockMvc.perform(MockMvcRequestBuilders.post("/wichteln/save")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params)
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection()).andReturn();

        String eventLinkUrl = saveResult.getResponse().getHeader("Location");
        String eventRegistrationUrl = eventLinkUrl.replace("link", "register");

        mockMvc.perform(MockMvcRequestBuilders.get(eventLinkUrl))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.stringContainsInOrder(
                        "Provide this link to everyone you wish to participate in your Wichteln event",
                        eventRegistrationUrl
                )));

        mockMvc.perform(MockMvcRequestBuilders.get(eventRegistrationUrl))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        String eventId = eventRegistrationUrl.split("/")[eventRegistrationUrl.split("/").length - 2];
        params.addAll(TestData.participant().formParams());
        params.add("event.id", eventId);
        mockMvc.perform(MockMvcRequestBuilders.post(eventRegistrationUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params)
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/wichteln"));
    }
}