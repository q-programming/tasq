package com.qprogramming.tasq.home;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.events.Event;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.manage.AppService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private TaskService taskSrv;
    private ProjectService projSrv;
    private EventsService eventSrv;
    private AppService appSrv;
    private SessionLocaleResolver localeResolver;

    @Value("${skip.landing.page}")
    private String skipLandingPage;

    @Value("1.3.0")
    private String version;

    @Autowired
    public HomeController(TaskService taskSrv, ProjectService projSrv, AppService appSrv, EventsService eventSrv, SessionLocaleResolver localeResolver) {
        this.taskSrv = taskSrv;
        this.projSrv = projSrv;
        this.appSrv = appSrv;
        this.eventSrv = eventSrv;
        this.localeResolver = localeResolver;
    }

    @SuppressWarnings("ConstantConditions")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Account account, Model model) {
        if (account == null) {
            localeResolver.setDefaultLocale(new Locale(appSrv.getProperty(AppService.DEFAULTLANG)));
            if (Boolean.parseBoolean(skipLandingPage)) {
                return "signin";
            } else {
                return "homeNotSignedIn";
            }
        } else {
            List<Project> usersProjects = projSrv.findAllByUser(account.getId());
            if (usersProjects.size() == 0
                    && (account.getRole().equals(Roles.ROLE_VIEWER) || account.getRole().equals(Roles.ROLE_USER))) {
                return "homeNewUser";
            }
            List<Task> allTasks = new LinkedList<>();
            for (Project project : usersProjects) {
                allTasks.addAll(taskSrv.findByProjectAndOpen(project));
            }
            List<Task> currentAccTasks = allTasks.stream().filter(task -> Utils.getCurrentAccount().equals(task.getAssignee())).collect(Collectors.toList());
            List<Task> unassignedTasks = allTasks.stream().filter(task -> task.getAssignee() == null).collect(Collectors.toList());
            Collections.sort(currentAccTasks, new TaskSorter(TaskSorter.SORTBY.PRIORITY, true));
            Collections.sort(unassignedTasks, new TaskSorter(TaskSorter.SORTBY.PRIORITY, true));
            model.addAttribute("myTasks", currentAccTasks);
            model.addAttribute("unassignedTasks", unassignedTasks);
            return "homeSignedIn";
        }
    }

    @RequestMapping(value = "/eventCount", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Integer> getEventCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            List<Event> events = eventSrv.getUnread();
            return ResponseEntity.ok(events.size());
        }
        return ResponseEntity.ok(0);
    }

    @RequestMapping(value = "/help", method = RequestMethod.GET)
    public String help(Model model, HttpServletRequest request) {
        // Utils.setHttpRequest(request);
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String lang = "en";
        // if (!(authentication instanceof AnonymousAuthenticationToken)) {
        // lang = Utils.getCurrentAccount().getLanguage();
        // if (lang == null) {
        // }
        // }
        model.addAttribute("version", version);
        model.addAttribute("projHome", appSrv.getProperty(AppService.TASQROOTDIR));
        return "help/" + lang;
    }

    @RequestMapping(value = "/tour")
    public String taskTour(@RequestParam(required = false) String page) {
        if (StringUtils.isBlank(page)) {
            return "help/tour_tasker";
        } else {
            return "help/tour_" + page;
        }
    }

    /**
     * Special mapping to redirect to some other page with extra messages . Page must be valid mapping
     *
     * @param page    page where it shoud be redirected to. Must be correctly mapped starting with /
     * @param type    {@link com.qprogramming.tasq.support.ResultData.Code}
     * @param message Message to be added in alert
     * @param ra      RedirectAttributes
     * @return redirects
     */
    @RequestMapping(value = "/redirect")
    public String redirect(@RequestParam String page, @RequestParam ResultData.Code type, @RequestParam String message, RedirectAttributes ra) {
        switch (type) {
            case OK:
                MessageHelper.addSuccessAttribute(ra, message);
                break;
            case WARNING:
                MessageHelper.addWarningAttribute(ra, message);
                break;
            case ERROR:
                MessageHelper.addErrorAttribute(ra, message);
                break;
            default:
                return "redirect:/";
        }
        return "redirect:" + page;
    }
}
