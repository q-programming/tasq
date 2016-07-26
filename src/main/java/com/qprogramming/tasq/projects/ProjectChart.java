package com.qprogramming.tasq.projects;

import com.qprogramming.tasq.agile.StartStop;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProjectChart {
    private Map<String, Integer> created;
    private Map<String, Integer> closed;
    private List<StartStop> freeDays;

    public ProjectChart() {
        created = new LinkedHashMap<>();
        closed = new LinkedHashMap<>();
        freeDays = new LinkedList<>();
    }

    public Map<String, Integer> getCreated() {
        return created;
    }

    public Map<String, Integer> getClosed() {
        return closed;
    }

    public List<StartStop> getFreeDays() {
        return freeDays;
    }

    public void setFreeDays(List<StartStop> freeDays) {
        this.freeDays = freeDays;
    }
}
