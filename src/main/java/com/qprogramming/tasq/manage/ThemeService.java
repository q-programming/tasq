package com.qprogramming.tasq.manage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThemeService {

    public static final String DEFAULT = "Default";
    private static final Logger LOG = LoggerFactory.getLogger(ThemeService.class);

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
            LOG.info("Creating default theme");
            theme = new Theme();
            theme.setName(DEFAULT);
            theme.setFont(Font.OPEN_SANS);
            theme.setColor(defaultColor);
            theme.setInvColor(defaultInvColor);
            theme = themeRepo.save(theme);
        }
        return theme;
    }

    public List<Theme> findAll() {
        return themeRepo.findAll();
    }

    public Theme findById(Long themeID) {
        return themeRepo.findById(themeID);
    }

}
