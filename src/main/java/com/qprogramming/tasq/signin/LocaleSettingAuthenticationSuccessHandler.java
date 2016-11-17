package com.qprogramming.tasq.signin;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.support.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
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

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private SessionLocaleResolver localeResolver;
    @Autowired
    private AccountService accountSrv;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        //Force user to watch tour.
        Account currentAccount = Utils.getCurrentAccount();
        if (!currentAccount.hadTour()) {
            if (response.isCommitted()) {
                logger.debug("Response has already been committed. Unable to redirect to tour");
            } else {
                currentAccount.setTour(true);
                accountSrv.save(currentAccount, false);
                redirectStrategy.sendRedirect(request, response, "/tour");
            }
        }
        // Eliminate signin error on saved request
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        if (savedRequest != null) {
            String[] redirectUlr = savedRequest.getRedirectUrl().split("/");
            String redirectParam = redirectUlr[redirectUlr.length - 1];
            if ("sigin".equals(redirectParam) || "generalError".equals(redirectParam) || "eventCount".equals(redirectParam)) {
                new HttpSessionRequestCache().removeRequest(request, response);
            }
        }
        String locale = currentAccount.getLanguage();
        Locale loc = new Locale(locale);
        localeResolver.setLocale(request, response, loc);
        super.onAuthenticationSuccess(request, response, authentication);
    }

}
