package com.qprogramming.tasq.task.link;

import com.qprogramming.tasq.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class TaskLinkService {

    private TaskLinkRepository linkRepo;

    @Autowired
    public TaskLinkService(TaskLinkRepository linkRepo) {
        this.linkRepo = linkRepo;
    }

    public void save(TaskLink link) {
        linkRepo.save(link);
    }

    public void delete(TaskLink link) {
        linkRepo.delete(link);
    }

    /**
     * Searches for existing link
     *
     * @param taskA
     * @param taskB
     * @param type
     * @return link if it was found ( or counter link was found )
     */
    public TaskLink findLink(String taskA, String taskB, TaskLinkType type) {
        if (type.equals(TaskLinkType.RELATES_TO)) {
            TaskLink link = linkRepo.findByTaskAAndTaskBAndLinkType(taskA, taskB, type);
            if (link == null) {
                link = linkRepo.findByTaskAAndTaskBAndLinkType(taskB, taskA, type);
            }
            return link;
        } else {
            return searchLinkAndCounterLink(taskA, taskB, type);
        }
    }

    public Map<TaskLinkType, List<String>> findTaskLinks(String taskID) {
        Map<TaskLinkType, List<String>> result = new LinkedHashMap<>();
        Map<TaskLinkType, List<String>> finalResult = new LinkedHashMap<>();
        // Prepopulate map to maintain order
        result.put(TaskLinkType.RELATES_TO, new LinkedList<>());
        result.put(TaskLinkType.BLOCKS, new LinkedList<>());
        result.put(TaskLinkType.IS_BLOCKED_BY, new LinkedList<>());
        result.put(TaskLinkType.DUPLICATES, new LinkedList<>());
        result.put(TaskLinkType.IS_DUPLICATED_BY, new LinkedList<>());

        List<TaskLink> listA = linkRepo.findByTaskA(taskID);
        for (TaskLink taskLink : listA) {
            List<String> tasks = result.get(taskLink.getLinkType());
            tasks.add(taskLink.getTaskB());
            result.put(taskLink.getLinkType(), tasks);
        }
        List<TaskLink> listB = linkRepo.findByTaskB(taskID);
        for (TaskLink taskLink : listB) {
            List<String> tasks = result.get(switchType(taskLink.getLinkType()));
            if (tasks == null) {
                tasks = new LinkedList<>();
            }
            tasks.add(taskLink.getTaskA());
            result.put(switchType(taskLink.getLinkType()), tasks);
        }
        // clean all potential empty results
        result.entrySet().stream().filter(entry -> !entry.getValue().isEmpty()).forEachOrdered(entry -> finalResult.put(entry.getKey(), entry.getValue()));
        return finalResult;
    }

    public void deleteTaskLinks(Task task) {
        List<TaskLink> listA = linkRepo.findByTaskA(task.getId());
        listA.addAll(linkRepo.findByTaskB(task.getId()));
        linkRepo.delete(listA);
    }

    public List<TaskLink> findAllTaskLinks(Task task) {
        List<TaskLink> list = linkRepo.findByTaskA(task.getId());
        list.addAll(linkRepo.findByTaskB(task.getId()));
        return list;
    }

    /**
     * Searches if link exists, then checks if there is counter link ( block ->
     * is blocked by ) or if taskB don't have link either
     *
     * @param taskA
     * @param taskB
     * @param type
     * @return
     */
    private TaskLink searchLinkAndCounterLink(String taskA, String taskB, TaskLinkType type) {
        TaskLink link;
        link = linkRepo.findByTaskAAndTaskBAndLinkType(taskA, taskB, type);
        if (link == null) {
            link = linkRepo.findByTaskAAndTaskBAndLinkType(taskB, taskA, switchType(type));
            if (link == null) {
                return linkRepo.findByTaskBAndTaskAAndLinkType(taskA, taskB, type);
            }
        }
        return link;
    }

    private TaskLinkType switchType(TaskLinkType type) {
        switch (type) {
            case BLOCKS:
                return TaskLinkType.IS_BLOCKED_BY;
            case IS_BLOCKED_BY:
                return TaskLinkType.BLOCKS;
            case DUPLICATES:
                return TaskLinkType.IS_DUPLICATED_BY;
            case IS_DUPLICATED_BY:
                return TaskLinkType.DUPLICATES;
            default:
                return type;
        }
    }

}
