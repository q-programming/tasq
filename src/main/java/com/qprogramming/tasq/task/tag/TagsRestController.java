package com.qprogramming.tasq.task.tag;

import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

@RestController
public class TagsRestController {

    private TagsRepository tagsRepo;
    private TaskService taskSrv;

    @Autowired
    public TagsRestController(TagsRepository tagsRepo, TaskService taskSrv) {
        this.tagsRepo = tagsRepo;
        this.taskSrv = taskSrv;
    }

    public TagsRestController() {
    }

    @RequestMapping(value = "/getTags", method = RequestMethod.GET)
    public List<Tag> getTags(@RequestParam String term,
                             HttpServletResponse response) {
        response.setContentType("application/json");
        List<Tag> result;
        if (term == null) {
            result = tagsRepo.findAll();
        } else {
            result = tagsRepo.findByNameContainingIgnoreCase(term);
        }
        return result;
    }

    @Transactional
    @RequestMapping(value = "/getTaskTags", method = RequestMethod.GET)
    public Set<Tag> getTaskTags(@RequestParam String taskID,
                                HttpServletResponse response) {
        response.setContentType("application/json");
        Task task = taskSrv.findById(taskID);
        Hibernate.initialize(task.getTags());
        return task.getTags();
    }

    @Transactional
    @RequestMapping(value = "/addTaskTag", method = RequestMethod.GET)
    public ResultData.Code addTag(@RequestParam String name, @RequestParam String taskID) {
        if (Utils.containsHTMLTags(name)) {
            return ResultData.Code.ERROR;
        }
        Task task = taskSrv.findById(taskID);
        if (task == null) {
            return ResultData.Code.ERROR;
        }
        Set<Tag> tags = task.getTags();
        Tag tag = tagsRepo.findByName(name);
        if (tag == null) {
            tag = new Tag(name);
            tag = tagsRepo.save(tag);
        }
        if (tags.contains(tag)) {
            return ResultData.Code.ERROR;
        }
        tags.add(tag);
        task.setTags(tags);
        taskSrv.save(task);
        return ResultData.Code.OK;
    }

    @Transactional
    @RequestMapping(value = "/removeTaskTag", method = RequestMethod.GET)
    public ResultData.Code removeTag(@RequestParam String name,
                                     @RequestParam String taskID) {
        Task task = taskSrv.findById(taskID);
        if (task == null) {
            return ResultData.Code.ERROR;
        }
        Tag tag = tagsRepo.findByName(name);
        Set<Tag> tags = task.getTags();
        if (tag == null || !tags.contains(tag)) {
            return ResultData.Code.ERROR;
        }
        tags.remove(tag);
        task.setTags(tags);
        taskSrv.save(task);
        List<Task> tasks = taskSrv.findByTag(name);
        if (tasks.isEmpty()) {
            tagsRepo.delete(tag);
        }
        return ResultData.Code.OK;
    }

}
