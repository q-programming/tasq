package com.qprogramming.tasq.agile;

import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.DisplayTask;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.tag.Tag;
import com.qprogramming.tasq.task.worklog.DisplayWorkLog;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Controller
public class KanbanController {
    private static final Logger LOG = LoggerFactory
            .getLogger(KanbanController.class);
    private static final String TODO_ONGOING = "<strike>To do</strike> &#10151; Ongoing";
    private static final String COMPLETED_ONGOING = "<strike>Complete</strike> &#10151; Ongoing";
    // TODO depreciated
    private static final String TODO_ONGOING_DEPR = "To do -> Ongoing";
    private static final String ONGOING = "ongoing";
    private TaskService taskSrv;
    private ProjectService projSrv;
    private WorkLogService wrkLogSrv;
    private MessageSource msg;
    private AgileService agileSrv;
    private DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

    // private ReleaseRepository releaseRepo;

    @Autowired
    public KanbanController(TaskService taskSrv, ProjectService projSrv,
                            WorkLogService wlSrv, MessageSource msg, AgileService agileSrv) {
        this.taskSrv = taskSrv;
        this.projSrv = projSrv;
        this.wrkLogSrv = wlSrv;
        this.msg = msg;
        this.agileSrv = agileSrv;
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "{id}/kanban/board", method = RequestMethod.GET)
    public String showBoard(@PathVariable String id, Model model,
                            HttpServletRequest request, RedirectAttributes ra) {
        Project project = projSrv.findByProjectId(id);
        if (project != null) {
            if (!projSrv.canEdit(project)) {
                throw new TasqAuthException(msg);
            }
            model.addAttribute("project", project);
            List<Task> taskList = taskSrv.findAllWithoutRelease(project);
            Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ORDER,
                    true));
            Set<Tag> tags = new HashSet<Tag>();
            for (Task task : taskList) {
                Hibernate.initialize(task.getTags());
                tags.addAll(task.getTags());
            }
            List<DisplayTask> resultList = taskSrv.convertToDisplay(taskList, true,true);
            model.addAttribute("tags", tags);
            model.addAttribute("tasks", resultList);
            return "/kanban/board";
        }
        return "";
    }

    @Transactional
    @RequestMapping(value = "/kanban/release", method = RequestMethod.POST)
    public String newRelease(@RequestParam(value = "id") String id,
                             @RequestParam(value = "release") String releaseNo,
                             @RequestParam(value = "comment", required = false) String comment,
                             HttpServletRequest request, RedirectAttributes ra) {
        Project project = projSrv.findByProjectId(id);
        if (project != null) {
            if (!projSrv.canAdminister(project)) {
                throw new TasqAuthException(msg);
            }
            // search if name unique for project
            Release unique = agileSrv.findByProjectIdAndRelease(
                    project.getId(), releaseNo);
            if (unique != null) {
                StringBuilder projectName = new StringBuilder("[");
                projectName.append(project.getProjectId());
                projectName.append("] ");
                projectName.append(project.getName());
                MessageHelper.addWarningAttribute(
                        ra,
                        msg.getMessage("agile.release.exists", new Object[]{
                                        releaseNo, projectName.toString()},
                                Utils.getCurrentLocale()));
                return "redirect:" + request.getHeader("Referer");
            }

            List<Task> taskList = taskSrv.findAllToRelease(project);
            if (taskList.isEmpty()) {
                MessageHelper.addWarningAttribute(
                        ra,
                        msg.getMessage("agile.newRelease.noTasks", null,
                                Utils.getCurrentLocale()));
                return "redirect:" + request.getHeader("Referer");
            }
            Release release = new Release(project, releaseNo, comment);
            List<Release> releases = agileSrv
                    .findReleaseByProjectIdOrderByDateDesc(project.getId());
            if (!releases.isEmpty()) {
                release.setStartDate(releases.get(releases.size() - 1)
                        .getEndDate());
            }
            release = agileSrv.save(release);
            int count = 0;
            for (Task task : taskList) {
                task.setRelease(release);
                count++;
            }
            MessageHelper.addSuccessAttribute(
                    ra,
                    msg.getMessage("agile.newRelease.success", new Object[]{
                            releaseNo, count}, Utils.getCurrentLocale()));
        }
        return "redirect:" + request.getHeader("Referer");
    }

    @RequestMapping(value = "{id}/kanban/reports", method = RequestMethod.GET)
    public String showReport(
            @PathVariable String id,
            @RequestParam(value = "release", required = false) String releaseNo,
            Model model, HttpServletRequest request, RedirectAttributes ra) {
        Project project = projSrv.findByProjectId(id);
        if (project != null) {
            List<Release> releases = agileSrv
                    .findReleaseByProjectIdOrderByDateDesc(project.getId());
            model.addAttribute("project", project);
            model.addAttribute("releases", releases);
        }
        return "/kanban/reports";
    }

    @RequestMapping(value = "/getReleases", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Release>> showProjectReleases(@RequestParam Long projectID,
                                                             HttpServletResponse response) {
        response.setContentType("application/json");
        Project project = projSrv.findById(projectID);
        return ResponseEntity.ok(agileSrv
                .findReleaseByProjectIdOrderByDateDesc(project.getId()));
    }

    @Transactional
    @RequestMapping(value = "/{id}/release-data", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<KanbanData> showBurndownChart(@PathVariable String id,
                                                        @RequestParam(value = "release", required = false) String releaseNo) {
        KanbanData result = new KanbanData();
        Project project = projSrv.findByProjectId(id);
        if (project != null) {
            Release release;
            DateTime startTime;
            DateTime endTime;
            List<Task> releaseTasks;
            if (releaseNo == null || "".equals(releaseNo)) {
                release = agileSrv.findLastReleaseByProjectId(project.getId());
                if (release == null) {
                    startTime = new DateTime(project.getRawStartDate());
                } else {
                    startTime = release.getEndDate();
                    release = null;
                }
                releaseTasks = taskSrv.findAllByRelease(project, release);
                endTime = new DateTime();
                release = new Release();
                release.setProject(project);
                release.setStartDate(startTime);
                release.setActive(true);
            } else {
                release = agileSrv.findByProjectIdAndRelease(project.getId(),
                        releaseNo);
                startTime = release.getStartDate();
                endTime = release.getEndDate();
                releaseTasks = taskSrv.findAllByRelease(release);
            }
            List<LocalDate> freeDays = projSrv.getFreeDays(project, startTime, endTime);
            LocalTime nearMidnight = new LocalTime(23, 59);
            result.setFreeDays(freeDays.stream()
                    .map(localDate -> new StartStop(fmt.print(localDate.minusDays(1).toDateTime(nearMidnight)), fmt.print(localDate.toDateTime(nearMidnight))))
                    .collect(Collectors.toList()));
            List<WorkLog> wrkList = wrkLogSrv.getAllReleaseEvents(release);
            agileSrv.setTasksByStatus(result,endTime,releaseTasks);
            result.setWorklogs(DisplayWorkLog.convertToDisplayWorkLogs(wrkList));
            result.setTimeBurned(agileSrv.fillTimeBurndownMap(wrkList,
                    startTime, endTime));
            Period totalTime = new Period();
            for (Map.Entry<String, Float> entry : result.getTimeBurned()
                    .entrySet()) {
                totalTime = PeriodHelper.plusPeriods(totalTime,
                        Utils.getPeriodValue(entry.getValue()));
            }
            result.setTotalTime(String.valueOf(Utils.round(
                    Utils.getFloatValue(totalTime), 2)));
            return ResponseEntity.ok(fillOpenAndClosed(result, release, wrkList));
        } else {
            return ResponseEntity.ok(result);
        }
    }

    private KanbanData fillOpenAndClosed(KanbanData result, Release release,
                                         List<WorkLog> wrkList) {
        KanbanData data = result;
        Map<LocalDate, Integer> openMap = new LinkedHashMap<>();
        Map<LocalDate, Integer> closedMap = new LinkedHashMap<>();
        Map<LocalDate, Integer> progressMap = new LinkedHashMap<>();
        for (WorkLog workLog : wrkList) {
            LocalDate dateLogged = new LocalDate(workLog.getRawTime());
            if (LogType.CREATE.equals(workLog.getType())) {
                increaseMap(openMap, dateLogged);
            } else if (LogType.CLOSED.equals(workLog.getType())) {
                increaseMap(closedMap, dateLogged);
                decreaseMap(openMap, dateLogged);
                decreaseMap(progressMap, dateLogged);
            } else if (LogType.REOPEN.equals(workLog.getType())) {
                decreaseMap(closedMap, dateLogged);
                increaseMap(progressMap, dateLogged);
            } else if (LogType.STATUS.equals(workLog.getType()) && isOngoing(workLog)) {
                increaseMap(progressMap, dateLogged);
                decreaseMap(openMap, dateLogged);
            }
        }
        LocalDate startTime = new LocalDate(release.getStartDate());
        LocalDate endTime = new LocalDate(release.getEndDate());
        data.setStart(startTime.toString());
        data.setStop(endTime.toString());
        fillChartData(data, openMap, closedMap, progressMap, startTime, endTime);
        normalizeProgressLabels(data, progressMap);
        return data;
    }

    private boolean isOngoing(WorkLog workLog) {
        String message = workLog.getMessage();
        if (StringUtils.isNotBlank(message)) {
            return message.contains(TODO_ONGOING) || message.contains(COMPLETED_ONGOING);
        }
        return false;
    }

    private void fillChartData(KanbanData data,
                               Map<LocalDate, Integer> openMap, Map<LocalDate, Integer> closedMap,
                               Map<LocalDate, Integer> progressMap, LocalDate startTime,
                               LocalDate endTime) {
        Integer open = 0;
        Integer closed = 0;
        Integer progress = 0;
        int releaseDays = Days.daysBetween(startTime, endTime).getDays() + 1;
        for (int i = 0; i < releaseDays; i++) {
            LocalDate date = startTime.plusDays(i);
            Integer openValue = openMap.get(date);
            Integer closedValue = closedMap.get(date);
            Integer progressValue = progressMap.get(date);
            openValue = openValue == null ? 0 : openValue;
            closedValue = closedValue == null ? 0 : closedValue;
            progressValue = progressValue == null ? 0 : progressValue;
            open += openValue;
            open = open < 0 ? 0 : open;
            closed += closedValue;
            progress += progressValue;
            progress = progress < 0 ? 0 : progress;
            data.putToClosed(date.toString(), closed);
            data.putToInProgress(date.toString(), closed + progress);
            data.putToOpen(date.toString(), closed + progress + open);
            data.putToInProgressLabel(date.toString(), progress);
            data.putToOpenLabel(date.toString(), open);
        }
    }

    private void normalizeProgressLabels(KanbanData data,
                                         Map<LocalDate, Integer> progressMap) {
        for (Entry<LocalDate, Integer> entry : progressMap.entrySet()) {
            Integer value = entry.getValue();
            if (value < 0) {
                value = 0;
                data.putToInProgressLabel(entry.getKey().toString(), value);
            }
        }
    }

    private void increaseMap(Map<LocalDate, Integer> map, LocalDate dateLogged) {
        Integer value = map.get(dateLogged);
        value = value == null ? 0 : value;
        value++;
        map.put(dateLogged, value);
    }

    private void decreaseMap(Map<LocalDate, Integer> map, LocalDate dateLogged) {
        Integer value = map.get(dateLogged);
        value = value == null ? 0 : value;
        value--;
        map.put(dateLogged, value);
    }

    @Deprecated
    private String getToDoOngoing() {
        return TODO_ONGOING_DEPR;
    }


}
