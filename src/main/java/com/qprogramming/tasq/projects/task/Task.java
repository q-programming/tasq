package com.qprogramming.tasq.projects.task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.qprogramming.tasq.projects.Project;

@Entity
public class Task {

	@Id
	@GeneratedValue
	Long id;

	@Column
	String name;
	
	@ManyToOne
	private Project project;

}
