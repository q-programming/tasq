package com.qprogramming.tasq.task.link;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskLinkService {

	@Autowired
	private TaskLinkRepository linkRepo;

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
		TaskLink link = new TaskLink();
		switch (type) {
		case RELATES_TO:
			return link = linkRepo.findByTaskAAndLinkType(taskA, type);
		case BLOCKS:
			link = linkRepo.findByTaskAAndLinkType(taskA, type);
			if (link == null) {
				link = linkRepo.findByTaskAAndLinkType(taskB,
						TaskLinkType.IS_BLOCKED_BY);
				if (link == null) {
					return linkRepo.findByTaskBAndLinkType(taskA,
							TaskLinkType.BLOCKS);
				}
			}
			return link;
		case IS_BLOCKED_BY:
			link = linkRepo.findByTaskAAndLinkType(taskA, type);
			if (link == null) {
				link = linkRepo.findByTaskAAndLinkType(taskB,
						TaskLinkType.BLOCKS);
				if (link == null) {
					return linkRepo.findByTaskBAndLinkType(taskA,
							TaskLinkType.IS_BLOCKED_BY);
				}

			}
			return link;
		case DUPLICATES:
			link = linkRepo.findByTaskAAndLinkType(taskA, type);
			if (link == null) {
				link = linkRepo.findByTaskAAndLinkType(taskB,
						TaskLinkType.IS_DUPLICATED_BY);
				if (link == null) {
					return linkRepo.findByTaskBAndLinkType(taskA,
							TaskLinkType.DUPLICATES);
				}

			}
			return link;
		case IS_DUPLICATED_BY:
			link = linkRepo.findByTaskAAndLinkType(taskA, type);
			if (link == null) {
				link = linkRepo.findByTaskAAndLinkType(taskA,
						TaskLinkType.DUPLICATES);
				if (link == null) {
					return linkRepo.findByTaskBAndLinkType(taskA,
							TaskLinkType.IS_DUPLICATED_BY);
				}

			}
			return link;
		default:
			return link;
		}
	}

	public Map<TaskLinkType, List<String>> findTaskLink(String taskID) {
		Map<TaskLinkType, List<String>> result = new HashMap();
		List<TaskLink> listA = linkRepo.findByTaskA(taskID);
		for (TaskLink taskLink : listA) {
			// get value if empty fill
		}
		List<TaskLink> listB = linkRepo.findByTaskA(taskID);
		for (TaskLink taskLink : listB) {
			// get value if empty fill
		}
		return result;
	}

}
