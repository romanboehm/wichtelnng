package com.romanboehm.wichtelnng.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@WebMvcTest(controllers = ErrorAdviceTest.ThrowingController.class)
public class ErrorAdviceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final RequestMappingInfo mapping = RequestMappingInfo.paths("/throw").methods(RequestMethod.GET).build();

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        requestMappingHandlerMapping.registerMapping(
                mapping,
                new ErrorAdviceTest.ThrowingController(),
                ErrorAdviceTest.ThrowingController.class.getDeclaredMethod("get", String.class));
    }

    @AfterEach
    void tearDown() {
        requestMappingHandlerMapping.unregisterMapping(mapping);
    }

    @ParameterizedTest
    // Even though a test for `Throwable` technically covers the other cases we keep them as a cheap and simple sanity
    // check.
    @ValueSource(classes = { RuntimeException.class, Exception.class, Throwable.class })
    public void shouldHandleAllThrowables(Class<?> throwableClass) throws Throwable {
        mockMvc.perform(MockMvcRequestBuilders.get("/throw").queryParam("throwable", throwableClass.getSimpleName()))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Whoops")));
    }

    static class ThrowingController {

        public void get(@RequestParam("throwable") String throwableName) throws Throwable {
            switch (throwableName) {
                case "RuntimeException" -> throw new RuntimeException();
                case "Exception" -> throw new Exception();
                case "Throwable" -> throw new Throwable();
            }
        }
    }

}