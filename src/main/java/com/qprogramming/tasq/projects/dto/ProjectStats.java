package com.qprogramming.tasq.projects.dto;

import java.util.HashMap;
import java.util.Map;

public class ProjectStats extends ProjectDataBase {
    private Map<String, Integer> commented;

    public ProjectStats() {
        super();
        this.commented = new HashMap<>();
    }

    public Map<String, Integer> getCommented() {
        return commented;
    }

    public void putCommented(String key, Integer value) {
        commented.put(key, value);
    }
}

