package com.qprogramming.tasq.signin;

import com.qprogramming.tasq.support.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component("localeSettingAuthenticationSuccessHandler")
public class LocaleSettingAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private SessionLocaleResolver localeResolver;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        // Eliminate signin error on saved request
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        if (savedRequest != null) {
            String[] redirectUlr = savedRequest.getRedirectUrl().split("/");
            String redirectParam = redirectUlr[redirectUlr.length - 1];
            if ("sigin".equals(redirectParam) || "generalError".equals(redirectParam)) {
                new HttpSessionRequestCache().removeRequest(request, response);
            }
        }
        String locale = Utils.getCurrentAccount().getLanguage();
        Locale loc = new Locale(locale);
        localeResolver.setLocale(request, response, loc);
        super.onAuthenticationSuccess(request, response, authentication);
    }

}
