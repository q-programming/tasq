package com.qprogramming.tasq.manage;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Property implements Serializable {

	@Id
	private String key;
	@Column
	private String value;

	public Property() {
	}

	public Property(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public Property(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
