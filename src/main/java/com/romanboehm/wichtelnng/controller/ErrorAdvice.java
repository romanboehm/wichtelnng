package com.romanboehm.wichtelnng.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ErrorAdvice {

    @ExceptionHandler(value = Throwable.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) {
        log.error("Encountered exception while requesting {}: {}", req.getRequestURI(), e.getMessage());
        return new ModelAndView("error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
