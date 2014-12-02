package com.qprogramming.tasq.task.link;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class TaskLink {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "link_seq_gen")
	@SequenceGenerator(name = "link_seq_gen", sequenceName = "link_id_seq", allocationSize = 1)
	private Long id;

	@Column
	private String taskA;
	@Column
	private String taskB;
	@Enumerated(EnumType.STRING)
	private TaskLinkType linkType;

	public TaskLink() {
	}
	public TaskLink(String a, String b, TaskLinkType type ) {
		setTaskA(a);
		setTaskB(b);
		setLinkType(type);
	}
	
	
	public String getTaskA() {
		return taskA;
	}

	public String getTaskB() {
		return taskB;
	}

	public TaskLinkType getLinkType() {
		return linkType;
	}

	public void setTaskA(String taskA) {
		this.taskA = taskA;
	}

	public void setTaskB(String taskB) {
		this.taskB = taskB;
	}

	public void setLinkType(TaskLinkType linkType) {
		this.linkType = linkType;
	}

}
