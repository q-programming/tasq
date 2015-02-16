package com.qprogramming.tasq.task.watched;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.qprogramming.tasq.account.Account;

@Entity
public class WatchedTask {

	@Id
	private String id;

	@Column
	private String name;

	@Column
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Account> watchers;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Set<Account> getWatchers() {
		return watchers;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWatchers(Set<Account> watchers) {
		this.watchers = watchers;
	}

	@Override
	public String toString() {
		StringBuilder watchedTask = new StringBuilder("[");
		watchedTask.append(getId());
		watchedTask.append("] ");
		watchedTask.append(getName());
		return watchedTask.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((watchers == null) ? 0 : watchers.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		WatchedTask other = (WatchedTask) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (watchers == null) {
			if (other.watchers != null) {
				return false;
			}
		} else if (!watchers.equals(other.watchers)) {
			return false;
		}
		return true;
	}

}
