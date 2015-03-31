package com.qprogramming.tasq.task.watched;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;

@Controller
public class WatchedTaskController {

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
		ResultData result = new ResultData();
		// check if not admin or user
		if (!Roles.isReporter()) {
			String role = msg.getMessage(Utils.getCurrentAccount().getRole()
					.getCode(), null, Utils.getCurrentLocale());
			String message = msg.getMessage("role.error.auth",
					new Object[] { role }, Utils.getCurrentLocale());
			result = new ResultData(ResultData.ERROR, message);
		} else {
			Task task = taskSrv.findById(id);
			WatchedTask watched = watchSrv.getByTask(id);
			if (watched != null
					&& watched.getWatchers()
							.contains(Utils.getCurrentAccount())) {
				watched = watchSrv.stopWatching(task);
				result.code = ResultData.OK;
				result.message = msg.getMessage("task.watch.stoped", null,
						Utils.getCurrentLocale());
			} else {
				watched = watchSrv.startWatching(task);
				result.code = ResultData.OK;
				result.message = msg.getMessage("task.watch.started", null,
						Utils.getCurrentLocale());
			}
		}
		return result;
	}

	/**
	 * Get list of all tasks watched by currently logged user
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/watching", method = RequestMethod.GET)
	public String watching(Model model) {
		List<WatchedTask> watched = watchSrv.findByWatcher(Utils
				.getCurrentAccount());
		model.addAttribute("watching", watched);
		return "user/watching";
	}

}
