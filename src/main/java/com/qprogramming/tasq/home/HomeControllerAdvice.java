package com.qprogramming.tasq.home;

import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.LastVisited;
import com.qprogramming.tasq.account.LastVisitedService;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.support.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Secured("ROLE_USER")
@ControllerAdvice
public class HomeControllerAdvice {
    private AccountService accSrv;
    private EventsService eventSrv;

    private LastVisitedService visitedSrv;


    @Autowired
    public HomeControllerAdvice(AccountService accSrv, EventsService eventSrv,LastVisitedService visitedSrv) {
        this.eventSrv = eventSrv;
        this.accSrv = accSrv;
        this.visitedSrv = visitedSrv;
    }

    @Transactional
    @ModelAttribute("lastProjects")
    public List<LastVisited> getLastProjects() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return visitedSrv.getAccountLastProjects(Utils.getCurrentAccount().getId());
        }
        return null;
    }

    @Transactional
    @ModelAttribute("lastTasks")
    public List<LastVisited> getLastTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return visitedSrv.getAccountLastTasks(Utils.getCurrentAccount().getId());
        }
        return null;
    }
}
