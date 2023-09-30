package com.romanboehm.wichtelnng.common.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Based on <a href="https://blog.codeleak.pl/2013/11/thymeleaf-template-layouts-in-spring.html">Rafał Borowiec's
 * <em>Thymeleaf template layouts in Spring MVC application with no extensions</em></a>:
 * <em>"ThymeleafLayoutInterceptor gets the original view name returned from the handler's method and replaces it with
 * the layout name (that is defined in WEB-INF/views/layouts/default.html). The original view is placed in the model as
 * a view variable, so it can be used in the layout file. I overrode the postHandle method, as it is executed just
 * before rendering the view."
 * </em>
 */
public class ThymeleafLayoutInterceptor implements HandlerInterceptor {

    private static final String DEFAULT_LAYOUT = "layouts/default";
    private static final String DEFAULT_VIEW_ATTRIBUTE_NAME = "view";

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {
        if (modelAndView == null || !modelAndView.hasView()) {
            return;
        }
        String originalViewName = modelAndView.getViewName();
        if (originalViewName == null) {
            return;
        }
        if (isRedirectOrForward(originalViewName)) {
            return;
        }
        modelAndView.setViewName(DEFAULT_LAYOUT);
        modelAndView.addObject(DEFAULT_VIEW_ATTRIBUTE_NAME, originalViewName);
    }

    private boolean isRedirectOrForward(String viewName) {
        return viewName.startsWith("redirect:") || viewName.startsWith("forward:");
    }
}
