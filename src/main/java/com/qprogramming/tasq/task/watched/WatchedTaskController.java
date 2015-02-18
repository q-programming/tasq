package com.qprogramming.tasq.task.watched;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;

@Controller
public class WatchedTaskController {

	private static final String WATCH = "WATCH";
	private static final String UNWATCH = "UNWATCH";
	private WatchedTaskService watchSrv;
	private MessageSource msg;
	private TaskService taskSrv;

	@Autowired
	public WatchedTaskController(WatchedTaskService watchSrv,
			MessageSource msg, TaskService taskSrv) {
		this.watchSrv = watchSrv;
		this.msg = msg;
		this.taskSrv = taskSrv;
	}

	@RequestMapping(value = "/task/watch", method = RequestMethod.POST)
	@ResponseBody
	public ResultData watch(@RequestParam(value = "id") String id) {
		// check if not admin or user
		if (!Roles.isUser()) {
			throw new TasqAuthException(msg);
		}
		ResultData result = new ResultData();
		Task task = taskSrv.findById(id);
		WatchedTask watched = watchSrv.getByTask(id);
		if (watched != null
				&& watched.getWatchers().contains(Utils.getCurrentAccount())) {
			watched = watchSrv.stopWatching(task);
			result.code = ResultData.OK;
			result.message = msg.getMessage("task.watch.stoped", null, Utils.getCurrentLocale());
		} else {
			watched = watchSrv.startWatching(task);
			result.code = ResultData.OK;
			result.message = msg.getMessage("task.watch.started", null, Utils.getCurrentLocale());
		}
		return result;
	}

	@RequestMapping(value = "/task/watchersCount", method = RequestMethod.GET)
	@ResponseBody
	public int watchersCount(@RequestParam(value = "id") String id) {
		WatchedTask watched = watchSrv.getByTask(id);
		return watched != null ? watched.getWatchers().size() : 0;
	}

}
