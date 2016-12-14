package com.qprogramming.tasq.home;

import com.qprogramming.tasq.account.LastVisited;
import com.qprogramming.tasq.account.LastVisitedService;
import com.qprogramming.tasq.support.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@Secured("ROLE_USER")
@ControllerAdvice
public class HomeControllerAdvice {
    private static final List<LastVisited> emptyList = Collections.emptyList();
    private LastVisitedService visitedSrv;

    @Autowired
    public HomeControllerAdvice(LastVisitedService visitedSrv) {
        this.visitedSrv = visitedSrv;
    }

    @Transactional
    @ModelAttribute("lastProjects")
    public List<LastVisited> getLastProjects() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return visitedSrv.getAccountLastProjects(Utils.getCurrentAccount().getId());
        }
        return emptyList;
    }

    @Transactional
    @ModelAttribute("lastTasks")
    public List<LastVisited> getLastTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return visitedSrv.getAccountLastTasks(Utils.getCurrentAccount().getId());
        }
        return emptyList;
    }
}
