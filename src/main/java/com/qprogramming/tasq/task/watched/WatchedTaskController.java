package com.qprogramming.tasq.task.watched;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class WatchedTaskController {

	private WatchedTaskService watchSrv;

	@Autowired
	public WatchedTaskController(WatchedTaskService watchSrv) {
		this.watchSrv = watchSrv;
	}
}
