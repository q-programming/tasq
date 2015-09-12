package com.qprogramming.tasq.signin;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

@Entity
@Table(name = "token")
public class RememberMeToken {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_seq_gen")
	@SequenceGenerator(name = "token_seq_gen", sequenceName = "token_id_seq", allocationSize = 1)
	private Long id;
	@Column
	private String username;
	@Column
	private String series;
	@Column
	private String tokenValue;
	@Column
	private Date date;

	public RememberMeToken() {
	}

	public RememberMeToken(PersistentRememberMeToken token) {
		this.series = token.getSeries();
		this.username = token.getUsername();
		this.tokenValue = token.getTokenValue();
		this.date = token.getDate();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
