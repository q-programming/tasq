package com.qprogramming.tasq.account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.task.Task;

@SuppressWarnings("serial")
@Entity
@Table(name = "account")
public class Account implements java.io.Serializable, UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq_gen")
	@SequenceGenerator(name = "account_seq_gen", sequenceName = "account_id_seq", allocationSize = 1)
	private Long id;

	@Column(unique = true)
	@NotEmpty
	private String username;

	@Column(unique = true)
	private String email;

	@JsonIgnore
	private String password;

	@Column
	private boolean confirmed = false;

	@Column
	private String language = "en";

	@Column
	private String name;

	@Column
	private String surname;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Column
	private byte[] avatar;

	@Column
	private String uuid;

	@Column
	private boolean email_notifications;

	@ManyToMany(fetch = FetchType.EAGER)
	@IndexColumn(name = "INDEX_COL")
	@JoinTable(name = "last_visited_t")
	private List<Task> last_visited_t = new LinkedList<Task>();
	
	@ManyToMany(fetch = FetchType.EAGER)
	@IndexColumn(name = "INDEX_COL")
	@JoinTable(name = "last_visited_p")
	private List<Project> last_visited_p = new LinkedList<Project>();
	
	@Column
	private String theme;
	
	@Transient
	private Collection<GrantedAuthority> authorities;

	/**
	 * [0] Task ID
	 * [1] active task start time
	 */
	@Column
	private Object[] active_task;
	
	@Column
	private Long active_project;

	// @Transient
	// private long active_task_seconds;

	public enum Role {
		ROLE_USER, ROLE_ADMIN
	};

	protected Account() {

	}

	public Account(String email, String password, Role role) {
		this.email = email;
		this.password = password;
		this.role = role;
		this.username = email.split("@")[0];
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean approved) {
		this.confirmed = approved;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}
	public void addAuthority(GrantedAuthority authority) {
		if(this.authorities==null){
			this.authorities = new ArrayList<GrantedAuthority>();
		}
		this.authorities.add(authority);
	}


	public byte[] getAvatar() {
		return avatar;
	}

	public void setLast_visited_t(List<Task> last_visited) {
		this.last_visited_t = last_visited;
	}

	public List<Task> getLast_visited_t() {
		return last_visited_t;
	}

	public List<Project> getLast_visited_p() {
		return last_visited_p;
	}

	public void setLast_visited_p(List<Project> last_visited_p) {
		this.last_visited_p = last_visited_p;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#getUsername()
	 */
	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean getEmail_notifications() {
		return email_notifications;
	}

	public void setEmail_notifications(boolean email_notifications) {
		this.email_notifications = email_notifications;
	}

	public Object[] getActive_task() {
		return active_task;
	}

	public Object getActive_task_time() {
		return active_task[1];
	}

	public void startTimerOnTask(String taskID) {
		active_task = new Object[] { taskID, new DateTime() };
	}

	public void setActive_task(Object[] task) {
		active_task = task;
	}

	public void clearActive_task() {
		active_task = new Object[] {};
	}

	public long getActive_task_seconds() {
		if (active_task != null && active_task.length > 0
				&& !active_task[0].equals("")) {
			return ((DateTime) active_task[1]).getMillis() / 1000;
		}
		return 0;
	}

	public Long getActive_project() {
		return active_project;
	}

	public void setActive_project(Long active_project) {
		this.active_project = active_project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired
	 * ()
	 */
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked
	 * ()
	 */
	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetails#
	 * isCredentialsNonExpired()
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.userdetails.UserDetails#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (name == null || surname == null) {
			return username;
		} else {
			return name + " " + surname;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	public class ActiveTask {
		private String taskID;
		private DateTime started;

		public ActiveTask(String taskID) {
			this.taskID = taskID;
			this.started = new DateTime();
		}

		public String getTaskID() {
			return taskID;
		}

		public DateTime getStarted() {
			return started;
		}

		public void setTaskID(String taskID) {
			this.taskID = taskID;
		}

		public void setStarted(DateTime started) {
			this.started = started;
		}

	}
}
