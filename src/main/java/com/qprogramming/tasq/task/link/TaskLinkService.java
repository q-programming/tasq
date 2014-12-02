package com.qprogramming.tasq.task.link;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qprogramming.tasq.task.DisplayTask;
import com.qprogramming.tasq.task.TaskService;

@Service
public class TaskLinkService {

	@Autowired
	private TaskLinkRepository linkRepo;

	@Autowired
	private TaskService taskSrv;

	public void save(TaskLink link) {
		linkRepo.save(link);
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
			return linkRepo.findByTaskAAndTaskBAndLinkType(taskA, taskB, type);
		} else {
			return searchLinkAndCounterLink(taskA, taskB, type);
		}
	}

	public Map<TaskLinkType, List<DisplayTask>> findTaskLinks(String taskID) {
		Map<TaskLinkType, List<DisplayTask>> result = new HashMap<TaskLinkType, List<DisplayTask>>();
		List<TaskLink> listA = linkRepo.findByTaskA(taskID);
		for (TaskLink taskLink : listA) {
			List<DisplayTask> tasks = result.get(taskLink.getLinkType());
			if (tasks == null) {
				tasks = new LinkedList<DisplayTask>();
			}
			DisplayTask displayTask = new DisplayTask(taskSrv.findById(taskLink
					.getTaskB()));
			tasks.add(displayTask);
			result.put(taskLink.getLinkType(), tasks);
		}
		List<TaskLink> listB = linkRepo.findByTaskB(taskID);
		for (TaskLink taskLink : listB) {
			List<DisplayTask> tasks = result.get(switchType(taskLink.getLinkType()));
			if (tasks == null) {
				tasks = new LinkedList<DisplayTask>();
			}
			DisplayTask displayTask = new DisplayTask(taskSrv.findById(taskLink
					.getTaskA()));
			tasks.add(displayTask);
			result.put(switchType(taskLink.getLinkType()), tasks);
		}
		return result;
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
	private TaskLink searchLinkAndCounterLink(String taskA, String taskB,
			TaskLinkType type) {
		TaskLink link;
		link = linkRepo.findByTaskAAndTaskBAndLinkType(taskA, taskB, type);
		if (link == null) {
			link = linkRepo.findByTaskAAndTaskBAndLinkType(taskB, taskA,
					switchType(type));
			if (link == null) {
				return linkRepo.findByTaskBAndTaskAAndLinkType(taskA, taskB,
						type);
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
			return TaskLinkType.IS_BLOCKED_BY;
		case IS_DUPLICATED_BY:
			return TaskLinkType.DUPLICATES;
		default:
			return type;
		}
	}

}
