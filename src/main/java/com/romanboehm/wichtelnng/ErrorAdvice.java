package com.romanboehm.wichtelnng;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.filter.ServerHttpObservationFilter;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ErrorAdvice {

    private final Logger log = LoggerFactory.getLogger(ErrorAdvice.class);

    @ExceptionHandler(value = Throwable.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) {
        log.error("Encountered exception while requesting {}: {}", req.getRequestURI(), e.getMessage());
        // Cf. https://docs.spring.io/spring-framework/reference/integration/observability.html.
        ServerHttpObservationFilter.findObservationContext(req)
                .ifPresent(context -> context.setError(e));
        return new ModelAndView("error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
