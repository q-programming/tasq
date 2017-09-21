package com.qprogramming.tasq.projects.dto;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class ProjectChart extends ProjectDataBase {
    private Map<String, Integer> created;


    public ProjectChart() {
        super();
        created = new LinkedHashMap<>();
    }

    public Map<String, Integer> getCreated() {
        return created;
    }

    public void putCreated(String key, Integer value) {
        created.put(key, value);
    }

}
