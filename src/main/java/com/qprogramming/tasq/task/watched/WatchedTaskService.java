package com.qprogramming.tasq.task.watched;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskType;

@Service
public class WatchedTaskService {

	private WatchedTaskRepository watchRepo;

	@Autowired
	public WatchedTaskService(WatchedTaskRepository watchRepo) {
		this.watchRepo = watchRepo;
	}

	public WatchedTask getByTask(String taskID) {
		return watchRepo.findById(taskID);
	}

	public WatchedTask getByTask(Task task) {
		return watchRepo.findById(task.getId());
	}

	/**
	 * Returns all task watched by given account
	 * 
	 * @param account
	 *            - account for which watchers will be returned
	 * @return List of WatchedTasks
	 */
	public List<WatchedTask> findByWatcher(Account account) {
		return watchRepo.findByWatchersId(account.getId());
	}

	/**
	 * Returns watchers for given project
	 * 
	 * @param project
	 * @return
	 */
	public Set<Account> getWatchers(String task) {
		WatchedTask watchedTask = getByTask(task);
		if (watchedTask != null) {
			return watchedTask.getWatchers();
		}
		return null;
	}

	/**
	 * Starts watching as given account
	 * 
	 * @param task
	 */
	@Transactional
	public WatchedTask addToWatchers(Task task, Account account) {
		WatchedTask watchedTask = getWatchedTask(task);
		watchedTask.getWatchers().add(account);
		return watchRepo.save(watchedTask);
	}

	/**
	 * Starts watching as currently logged-in user Current user will be added to
	 * watchers list
	 * 
	 * @param task
	 */
	@Transactional
	public WatchedTask startWatching(Task task) {
		WatchedTask watchedTask = getWatchedTask(task);
		watchedTask.getWatchers().add(Utils.getCurrentAccount());
		return watchRepo.save(watchedTask);
	}

	/**
	 * Removes current account from project watch.
	 * 
	 * @param project_id
	 */
	@Transactional
	public WatchedTask stopWatching(Task task) {
		WatchedTask watchedTask = getByTask(task);
		if (watchedTask != null) {
			Account current_user = Utils.getCurrentAccount();
			Set<Account> watchers = watchedTask.getWatchers();
			if (watchers != null) {
				watchers.remove(current_user);
				watchedTask.setWatchers(watchers);
			}
			return watchRepo.save(watchedTask);
		}
		return null;
	}

	/**
	 * Checks if currently logged account is watching task
	 * 
	 * @param taskID
	 * @return true if account is watching this task
	 */
	public boolean isWatching(String taskID) {
		WatchedTask watched = getByTask(taskID);
		return watched != null ? watched.getWatchers().contains(
				Utils.getCurrentAccount()) : false;
	}

	/**
	 * Removes whole watcher. Overwrites WatchedTask with new instance and then
	 * deletes it
	 * 
	 * @param wpDelete
	 */
	@Transactional
	public void deleteWatchedTask(String taskID) {
		WatchedTask wpDelete = new WatchedTask();
		wpDelete.setId(taskID);
		watchRepo.delete(wpDelete);
	}

	private WatchedTask getWatchedTask(Task task) {
		WatchedTask watchedTask = getByTask(task);
		if (watchedTask == null) {
			watchedTask = new WatchedTask();
		}
		Set<Account> watchers = watchedTask.getWatchers();
		if (watchers == null) {
			watchers = new HashSet<Account>();
		}
		watchedTask.setId(task.getId());
		watchedTask.setType((TaskType) task.getType());
		watchedTask.setWatchers(watchers);
		watchedTask.setName(task.getName());
		watchedTask.setWatchers(watchers);
		return watchedTask;
	}

	public Page<WatchedTask> findByWatcher(Account currentAccount, Pageable p) {
		return watchRepo.findByWatchersId(currentAccount.getId(), p);
	}
}
