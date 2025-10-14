package com.romanboehm.wichtelnng.common.web;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.servlet.ModelAndView;

class WebUtils {

    private static final String DEFAULT_LAYOUT = "layouts/default";
    private static final String DEFAULT_VIEW_ATTRIBUTE_NAME = "view";

    private WebUtils() {
        // Utility class
    }

    static void embedViewIntoDefaultLayout(ModelAndView modelAndView) {
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

    private static boolean isRedirectOrForward(String viewName) {
        return viewName.startsWith("redirect:") || viewName.startsWith("forward:");
    }

}
