package com.qprogramming.tasq.projects;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.DisplayAccount;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.agile.StartStop;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.dto.DisplayProject;
import com.qprogramming.tasq.projects.dto.ProjectChart;
import com.qprogramming.tasq.projects.dto.ProjectStats;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.worklog.DisplayWorkLog;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import org.joda.time.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ProjectRestController {
    private static final String APPLICATION_JSON = "application/json";
    public static final int ACTIVE_DAYS = 14;
    private ProjectService projSrv;
    private AccountService accSrv;
    private MessageSource msg;
    private WorkLogService wrkLogSrv;

    @Autowired
    public ProjectRestController(ProjectService projSrv, AccountService accSrv, @Qualifier("messageSource") MessageSource msg, WorkLogService wrkLogSrv) {
        this.projSrv = projSrv;
        this.accSrv = accSrv;
        this.msg = msg;
        this.wrkLogSrv = wrkLogSrv;
    }


    @RequestMapping(value = "/usersProjectsEvents", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Page<DisplayWorkLog>> getProjectsLogs(
            @PageableDefault(size = 25, page = 0, sort = "time", direction = Sort.Direction.DESC) Pageable p) {
        Account account = Utils.getCurrentAccount();
        List<Project> usersProjects = projSrv.findAllByUser(account.getId());
        if (!usersProjects.isEmpty()) {
            List<Long> ids = usersProjects.stream().map(Project::getId).collect(Collectors.toCollection(LinkedList::new));
            Page<WorkLog> page = wrkLogSrv.findByProjectIdIn(ids, p);
            List<DisplayWorkLog> list = page.getContent().stream().map(DisplayWorkLog::new).collect(Collectors.toList());
            return ResponseEntity.ok(new PageImpl<>(list, p, page.getTotalElements()));
        }
        return null;
    }

    @RequestMapping(value = "projectEvents", method = RequestMethod.GET)
    public ResponseEntity<Page<DisplayWorkLog>> getProjectEvents(@RequestParam(value = "id") String id,
                                                                 @PageableDefault(size = 25, page = 0, sort = "time", direction = Sort.Direction.DESC) Pageable p) {
        Project project = projSrv.findByProjectId(id);
        if (project == null) {
            // NULL
            return null;
        }
        if (!project.getParticipants().contains(Utils.getCurrentAccount()) && !Roles.isAdmin()) {
            throw new TasqAuthException(msg, "role.error.project.permission");
        }
        // Fetch events
        Page<WorkLog> page = wrkLogSrv.findByProjectId(project.getId(), p);
        List<DisplayWorkLog> list = new LinkedList<>();
        for (WorkLog workLog : page) {
            list.add(new DisplayWorkLog(workLog));
        }
        return ResponseEntity.ok(new PageImpl<>(list, p, page.getTotalElements()));
    }

    /**
     * Returns DisplayProject - minified version of project detials to get all
     * default values etc.
     *
     * @param id id of project
     * @return display project with defaults for it
     */
    @RequestMapping(value = "/project/getDefaults", method = RequestMethod.GET)
    public ResponseEntity<DisplayProject> getDefaults(@RequestParam String id, HttpServletResponse response) {
        response.setContentType(APPLICATION_JSON);
        Project project = projSrv.findByProjectId(id);
        if (project == null) {
            return ResponseEntity.badRequest().body(null);
        }
        DisplayProject result = new DisplayProject(project);
        Account account = accSrv.findById(project.getDefaultAssigneeID());
        if (account != null) {
            result.setDefaultAssignee(new DisplayAccount(account));
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/project/getParticipants", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<List<DisplayAccount>> listParticipants(@RequestParam String id, @RequestParam String term,
                                                          @RequestParam(required = false) boolean userOnly, HttpServletResponse response) {
        response.setContentType(APPLICATION_JSON);
        List<Account> accounts = projSrv.getProjectAccounts(id, term);
        if (userOnly) {
            return ResponseEntity.ok(accounts.stream().filter(Account::getIsUser).map(DisplayAccount::new).collect(Collectors.toList()));
        }
        return ResponseEntity.ok(accounts.stream().map(DisplayAccount::new).collect(Collectors.toList()));
    }


    @Transactional
    @RequestMapping(value = "/project/getChart", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ProjectChart> getProjectChart(@RequestParam String id,
                                                        @RequestParam(required = false) boolean all, HttpServletResponse response) {
        response.setContentType(APPLICATION_JSON);
        Project project = projSrv.findByProjectId(id);
        ProjectChart result = new ProjectChart();
        List<WorkLog> events = wrkLogSrv.findProjectCreateCloseEvents(project, all);
        // Fill maps
        if (events.size() > 0) {
            ProcessedEvents processedEvents = processEvents(events, true);
            // Look for the first event ever (they are sorted)
            LocalDate start = new LocalDate(events.get(0).getRawTime());
            LocalDate end = new LocalDate().plusDays(1);
            LocalDate counter = start;
            result.setFreeDays(getFreeDays(project, start, end));
            Integer taskCreated = 0;
            Integer taskClosed = 0;
            while (counter.isBefore(end)) {
                Integer createValue = processedEvents.getCreated(counter);
                taskCreated += createValue;
                result.putCreated(counter.toString(), taskCreated);
                Integer closeValue = processedEvents.getClosed(counter);
                taskClosed += closeValue;
                result.putClosed(counter.toString(), taskClosed);
                counter = counter.plusDays(1);
            }
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Returns statistics for all live of project
     *
     * @param id ID of project
     * @return {@link ProjectStats}
     */
    @Transactional
    @RequestMapping(value = "/project/getStats", method = RequestMethod.GET)
    public ResponseEntity<ProjectStats> getProjectStats(@RequestParam String id) {
        Project project = projSrv.findByProjectId(id);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        ProjectStats stats = new ProjectStats();
        //process events
        setEventsAndDates(project, stats);
        //get task count
        long taskCount = project.getTasks().size();
        long subTaskCount = project.getTasks().stream().filter(Task::isSubtask).count();
        stats.setTaskCount(taskCount - subTaskCount);
        stats.setSubTaskCount(subTaskCount);
        //get total estimated/logged/remaining
        setPeriodsTotals(project, stats);
        //active members
        setActiveMembers(project, stats);
        return ResponseEntity.ok(stats);
    }

    private void setEventsAndDates(Project project, ProjectStats stats) {
        List<WorkLog> events = wrkLogSrv.findProjectCreateCloseLogEvents(project);
        ProcessedEvents processedEvents = processEvents(events, false);
        int allEvents = processedEvents.days.values().stream().reduce(0, Integer::sum);
        Map<String, Integer> eventPerWeekday = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : processedEvents.days.entrySet()) {
            eventPerWeekday.put(e.getKey(), calcPercentage(e.getValue(), allEvents));
        }
        stats.setDaysOfWeek(eventPerWeekday);
        LocalDate start = new LocalDate(events.get(0).getRawTime());
        LocalDate end = new LocalDate().plusDays(1);
        WorkLog lastEvent = events.get(events.size() - 1);
        LocalDate lastActiveDay = new LocalDate(lastEvent.getRawTime());
        LocalDate counter = start;
        stats.setFreeDays(getFreeDays(project, start, end));
        Integer taskClosed = 0;
        while (counter.isBefore(end)) {
            Period logged = processedEvents.getLogged(counter);
            stats.putLogged(counter.toString(), Utils.getFloatValue(logged));
            Integer closeValue = processedEvents.getClosed(counter);
            taskClosed += closeValue;
            stats.putClosed(counter.toString(), taskClosed);
            counter = counter.plusDays(1);
        }
        //dates
        stats.setStartDate(start);
        stats.setLastEventDate(lastActiveDay);
        stats.setActive(Days.daysBetween(lastActiveDay, new LocalDate()).getDays() < ACTIVE_DAYS);
        //days of week
    }

    private int calcPercentage(Integer val, Integer total) {
        return Math.round((val.floatValue() / total.floatValue()) * 100);
    }

    private void setPeriodsTotals(Project project, ProjectStats stats) {
        Period totalEstimate = new Period();
        Period totalLogged = new Period();
        Period totalRemaining = new Period();
        for (Task task : project.getTasks()) {
            totalEstimate = PeriodHelper.plusPeriodsInHours(totalEstimate, task.getRawEstimate());
            totalLogged = PeriodHelper.plusPeriodsInHours(totalLogged, task.getRawLoggedWork());
            totalRemaining = PeriodHelper.plusPeriodsInHours(totalRemaining, task.getRawRemaining());
        }
        stats.setTotalEstimate(String.valueOf(totalEstimate.getHours()));
        stats.setTotalLogged(String.valueOf(totalLogged.getHours()));
        stats.setTotalRemaining(String.valueOf(totalRemaining.getHours()));
    }

    private void setActiveMembers(Project project, ProjectStats stats) {
        Map<DisplayAccount, Long> assignees = project.getTasks()
                .stream()
                .filter(task -> task.getAssignee() != null)
                .collect(Collectors.groupingBy((Task t) -> new DisplayAccount(t.getAssignee()), Collectors.counting()));

        stats.setTopActive(assignees.entrySet()
                .stream()
                .sorted(Map.Entry.<DisplayAccount, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> new ProjectStats.ActiveAccount(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
    }

    private ProcessedEvents processEvents(List<WorkLog> events, boolean noSubtask) {
        ProcessedEvents result = new ProcessedEvents();
        if (noSubtask) {
            events.stream()
                    // Don't calculate for subtask ( not important )
                    .filter(workLog -> workLog.getTask() != null && !workLog.getTask().isSubtask())
                    .forEach(workLog -> evaluateEvent(result, workLog));
        } else {
            events.stream()
                    .filter(workLog -> workLog.getTask() != null)
                    .forEach(workLog -> evaluateEvent(result, workLog));
        }
        return result;
    }

    private void evaluateEvent(ProcessedEvents result, WorkLog workLog) {
        LocalDate date = new LocalDate(workLog.getRawTime());
        if (LogType.CREATE.equals(workLog.getType())) {
            Integer value = result.getCreated(date);
            result.setCreated(date, ++value);
        } else if (LogType.REOPEN.equals(workLog.getType())) {
            Integer value = result.getClosed(date);
            result.setClosed(date, --value);
        } else if (LogType.CLOSED.equals(workLog.getType())) {
            Integer value = result.getClosed(date);
            result.setClosed(date, ++value);
        } else if (LogType.LOG.equals(workLog.getType())) {
            Period value = result.getLogged(date);
            result.setLogged(date, PeriodHelper.plusPeriods(value, workLog.getActivity()));
        } else {
            return;
        }
        Integer day = result.getDay(date);
        result.setDays(date, ++day);
    }

    private List<StartStop> getFreeDays(Project project, LocalDate start, LocalDate end) {
        LocalTime nearMidnight = new LocalTime(23, 59);
        DateTime startTime = start.toDateTime(new LocalTime(0, 0));
        DateTime endTime = end.toDateTime(nearMidnight);
        List<LocalDate> freeDays = projSrv.getFreeDays(project, startTime, endTime);
        return freeDays.stream()
                .map(dateTime -> new StartStop(Utils.convertDateTimeToString(dateTime.minusDays(1).toDateTime(nearMidnight).toDate())
                        , Utils.convertDateTimeToString(dateTime.toDateTime(nearMidnight).toDate())))
                .collect(Collectors.toList());

    }

    static class ProcessedEvents {
        Map<String, Integer> created = new HashMap<>();
        Map<String, Integer> closed = new HashMap<>();
        Map<String, Period> logged = new HashMap<>();
        Map<String, Integer> days = new LinkedHashMap<>();

        /**
         * Init class with days map pre-filled to maintain days order
         */
        ProcessedEvents() {
            for (int i = 1; i <= 7; i++) {
                LocalDate day = new LocalDate().dayOfWeek().setCopy(i);
                setDays(day, 0);
            }
        }

        Integer getClosed(LocalDate key) {
            return closed.getOrDefault(key.toString(), 0);
        }

        Integer getCreated(LocalDate key) {
            return created.getOrDefault(key.toString(), 0);
        }

        Period getLogged(LocalDate key) {
            return logged.getOrDefault(key.toString(), new Period());
        }

        Integer getDay(LocalDate key) {
            return days.getOrDefault(key.dayOfWeek().getAsText(Utils.getCurrentLocale()), 0);
        }

        void setDays(LocalDate key, Integer value) {
            days.put(key.dayOfWeek().getAsText(Utils.getCurrentLocale()), value);
        }

        void setCreated(LocalDate key, Integer value) {
            created.put(key.toString(), value);
        }

        void setClosed(LocalDate key, Integer value) {
            closed.put(key.toString(), value);
        }

        void setLogged(LocalDate key, Period value) {
            logged.put(key.toString(), value);
        }
    }
}
