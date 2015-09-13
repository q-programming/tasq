package com.qprogramming.tasq.manage;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Theme implements Serializable {
	private static final long serialVersionUID = 3944316219721274266L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "theme_seq_gen")
	@SequenceGenerator(name = "theme_seq_gen", sequenceName = "theme_id_seq", allocationSize = 1)
	private Long id;
	@Column(unique = true)
	private String name;
	@Column
	private String color;
	@Column
	private String invColor;
	@Enumerated(EnumType.STRING)
	private Font font;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getInvColor() {
		return invColor;
	}

	public void setInvColor(String invColor) {
		this.invColor = invColor;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Color getRgbColor() {
		return new Color(Integer.valueOf(color.substring(1, 3), 16), Integer.valueOf(color.substring(3, 5), 16),
				Integer.valueOf(color.substring(5, 7), 16));
	}
}
