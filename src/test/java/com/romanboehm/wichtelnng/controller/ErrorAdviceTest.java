package com.romanboehm.wichtelnng.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ErrorAdviceTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ThrowingController throwingController;

    @ParameterizedTest
    // Even though a test for `Throwable` technically covers the other cases we keep them as a cheap and simple sanity
    // check.
    @ValueSource(classes = {RuntimeException.class, Exception.class, Throwable.class})
    public <T extends Throwable> void shouldHandleAllThrowables(Class<T> throwableClass) throws Throwable {
        doThrow(throwableClass).when(throwingController).get();

        mockMvc.perform(MockMvcRequestBuilders.get("/throw"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Whoops")));
    }

    @Controller
    private static class ThrowingController {

        @GetMapping(path = "throw")
        void get() throws Throwable {
        }
    }

}