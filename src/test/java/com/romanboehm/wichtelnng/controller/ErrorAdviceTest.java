package com.romanboehm.wichtelnng.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;

@WebMvcTest(controllers = ErrorAdviceTest.ThrowingController.class)
@AutoConfigureMockMvc
public class ErrorAdviceTest {

    @Controller
    public static class ThrowingController {

        @GetMapping(path = "throw")
        void get() throws Throwable { }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ThrowingController throwingController;

    @ParameterizedTest
    // Even though a test for `Throwable` technically covers the other cases we keep them as a cheap and simple sanity
    // check.
    @ValueSource(classes = { RuntimeException.class, Exception.class, Throwable.class })
    public <T extends Throwable> void shouldHandleAllThrowables(Class<T> throwableClass) throws Throwable {
        Mockito.doThrow(throwableClass).when(throwingController).get();

        mockMvc.perform(MockMvcRequestBuilders.get("/throw"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Whoops")));
    }

}