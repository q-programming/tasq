package com.qprogramming.tasq.home;

import com.qprogramming.tasq.manage.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class PageAdvice {
    private AppService appSrv;
    private String applicationName;

    @Autowired
    public PageAdvice(AppService appSrv) {
        this.appSrv = appSrv;
        applicationName = appSrv.getProperty(AppService.APPLICATION_NAME);
    }

    /**
     * Add requestedLink to model for purpose of marking active page
     *
     * @param request
     * @return
     */
    @ModelAttribute("requestedLink")
    public String link(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("applicationName")
    public String appName() {
        return applicationName;
    }

}
