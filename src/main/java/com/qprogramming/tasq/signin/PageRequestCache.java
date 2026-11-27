package com.qprogramming.tasq.signin;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Saves only real page navigations (GET requests accepting html), so stray requests for resources,
 * ajax calls or malformed urls can't become the post-login redirect target.
 */
public class PageRequestCache extends HttpSessionRequestCache {

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        if (isPageRequest(request)) {
            super.saveRequest(request, response);
        }
    }

    private boolean isPageRequest(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return false;
        }
        String accept = request.getHeader("Accept");
        if (accept == null || !accept.contains("text/html")) {
            return false;
        }
        String uri = request.getRequestURI();
        return !(uri.contains("<") || uri.contains("%3C") || uri.contains("%3c") || uri.endsWith("/undefined"));
    }
}
