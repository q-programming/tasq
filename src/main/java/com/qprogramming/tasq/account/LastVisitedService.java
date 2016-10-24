package com.qprogramming.tasq.account;

import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.sorters.VisitedSorter;
import com.qprogramming.tasq.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Khobar on 04.10.2016.
 */
@Service
public class LastVisitedService {

    private LastVisitedRepository lastVisitedRepository;

    @Autowired
    public LastVisitedService(LastVisitedRepository lastVisitedRepository) {
        this.lastVisitedRepository = lastVisitedRepository;
    }

    public void addLastVisited(Long accountId, Task task) {
        updateLastVisited(accountId, new LastVisited(task, accountId));
    }

    public void addLastVisited(Long accountId, Project project) {
        updateLastVisited(accountId, new LastVisited(project, accountId));
    }


    private void updateLastVisited(Long accountId, LastVisited lastVisited) {
        Set<LastVisited> accountLastVisited;
        if (lastVisited.getType() == null) {
            accountLastVisited = new HashSet<>(getAccountLastProjects(accountId));
        } else {
            accountLastVisited = new HashSet<>(getAccountLastTasks(accountId));
        }
        //check if this is revisit
        if (accountLastVisited.contains(lastVisited)) {
            accountLastVisited.stream().filter(visited -> visited.equals(lastVisited)).forEach(visited -> visited.setTime(new Date()));
        } else {
            accountLastVisited.add(lastVisited);
        }
        if (accountLastVisited.size() > 5) {
            List<LastVisited> sortMe = new ArrayList<>(accountLastVisited);
            Collections.sort(sortMe, new VisitedSorter());
            LastVisited oldest = sortMe.get(sortMe.size() - 1);
            lastVisitedRepository.delete(oldest);
            accountLastVisited = sortMe.stream().limit(4).collect(Collectors.toSet());
        }
        lastVisitedRepository.save(accountLastVisited);
    }

    public void updateName(Task task) {
        List<LastVisited> list = lastVisitedRepository.findByItemIdAndTypeNotNull(task.getId());
        list.stream().forEach(lastVisited -> lastVisited.setItemName(task.getName()));
        lastVisitedRepository.save(list);

    }

    public void updateName(Project project) {
        List<LastVisited> list = lastVisitedRepository.findByItemIdAndTypeNull(project.getProjectId());
        list.stream().forEach(lastVisited -> lastVisited.setItemName(project.getName()));
        lastVisitedRepository.save(list);

    }


    public Set<LastVisited> getAccountLastVisited(Long id) {
        return new HashSet<>(lastVisitedRepository.findByAccountOrderByTimeDesc(id));
    }

    public List<LastVisited> getAccountLastTasks(Long id) {
        return lastVisitedRepository.findByAccountAndTypeNotNullOrderByTimeDesc(id);
    }

    public List<LastVisited> getAccountLastProjects(Long id) {
        return lastVisitedRepository.findByAccountAndTypeNullOrderByTimeDesc(id);
    }


    public void delete(Task task) {
        List<LastVisited> list = lastVisitedRepository.findByItemIdAndTypeNotNull(task.getId());
        lastVisitedRepository.delete(list);
    }
}
