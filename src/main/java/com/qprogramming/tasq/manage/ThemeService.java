package com.qprogramming.tasq.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ThemeService {

	private static final String DEFAULT = "Default";
	
	@Value("${default.theme.color}")
	private String defaultColor;
	
	@Value("${default.theme.inverse}")
	private String defaultInvColor;

	private ThemeRepository themeRepo;

	@Autowired
	public ThemeService(ThemeRepository themeRepo) {
		this.themeRepo = themeRepo;
	}

	public Theme save(Theme theme) {
		return themeRepo.save(theme);
	}

	public Theme getDefault() {
		Theme theme = themeRepo.findByName(DEFAULT);
		if (theme == null) {
			theme = new Theme();
			theme.setName(DEFAULT);
			theme.setFont(Font.OPEN_SANS);
			theme.setColor(defaultColor);
			theme.setInvColor(defaultInvColor);
			theme = themeRepo.save(theme);
		}
		return theme;
	}

}
