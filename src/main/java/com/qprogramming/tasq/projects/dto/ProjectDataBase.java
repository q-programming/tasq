package com.qprogramming.tasq.projects.dto;

import com.qprogramming.tasq.agile.StartStop;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProjectDataBase {
    protected Map<String, Integer> closed;
    protected List<StartStop> freeDays;

    protected ProjectDataBase() {
        closed = new LinkedHashMap<>();
        freeDays = new LinkedList<>();
    }

    public Map<String, Integer> getClosed() {
        return closed;
    }

    public void putClosed(String key, Integer value) {
        closed.put(key, value);
    }

    public List<StartStop> getFreeDays() {
        return freeDays;
    }


    public void setFreeDays(List<StartStop> freeDays) {
        this.freeDays = freeDays;
    }
}
