/**
 * 
 */
package com.qprogramming.tasq.task.worklog;

import java.util.Date;

import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;

/**
 * @author romanjak
 * @date 28 maj 2014
 */
@Service
public class WorkLogService {

	@Autowired
	WorkLogRepository wlRepo;

	@Autowired
	TaskService taskSrv;

	public void addWorkLog(Task task, LogType type, String msg, Period activity) {
		task = taskSrv.findById(task.getId());
		if (task != null) {
			WorkLog wl = new WorkLog();
			wl.setAccount(Utils.getCurrentAccount());
			wl.setTime(new Date());
			wl.setType(type);
			wl.setMessage(msg);
			wl.setActivity(activity);
			wl = wlRepo.save(wl);
			task.addWorkLog(wl);
			taskSrv.save(task);
		}

	}

	/**
	 * @param task
	 */
	public void findAllByTask(Task task) {
		// TODO Auto-generated method stub

	}

}
