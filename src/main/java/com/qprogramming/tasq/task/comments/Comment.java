/**
 * 
 */
package com.qprogramming.tasq.task.comments;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.qprogramming.tasq.account.Account;


@Entity
@Table(name = "comments")
public class Comment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4161639839117869343L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE, generator="generatorName")  
	@TableGenerator(name="generatorName", allocationSize=1)  
	private Long id;

	@ManyToOne
	private Account author;

	@Column(length = 4000)
	private String message;

	@Column
	private Date date;

	@Column
	private Date date_edited;

	SimpleDateFormat sdf = new SimpleDateFormat("dd.M.yyyy HH:mm");

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Account getAuthor() {
		return author;
	}

	public void setAuthor(Account author) {
		this.author = author;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDate() {
		return sdf.format(date);
	}

	public Date getRawDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDate_edited() {
		if (date_edited != null) {
			return sdf.format(date_edited);
		} else {
			return null;
		}
	}

	public void setDate_edited(Date date_edited) {
		this.date_edited = date_edited;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
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
		Comment other = (Comment) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

}
