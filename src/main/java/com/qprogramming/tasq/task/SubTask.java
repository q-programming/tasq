package com.qprogramming.tasq.task;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class SubTask extends AbstractTask implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4305616872550443058L;

	@ManyToOne(optional = false)
	@JoinColumn(name = "task")
	private Task task;
	
	@Column
	private Enum<SubTaskType> type;


	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Enum<SubTaskType> getType() {
		return type;
	}

	public void setType(Enum<SubTaskType> type) {
		this.type = type;
	}

}
