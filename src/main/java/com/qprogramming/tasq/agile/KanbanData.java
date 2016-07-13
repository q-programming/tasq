package com.qprogramming.tasq.agile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KanbanData extends AgileData {
    private Map<String, Integer> open;
    private Map<String, Integer> inProgress;
    private Map<String, Integer> closed;
    private Map<String, Integer> progressLabels;
    private Map<String, Integer> openLabels;
    private List<StartStop> freeDays;
    private String startStop;

    public KanbanData() {
        open = new LinkedHashMap<>();
        closed = new LinkedHashMap<>();
        inProgress = new LinkedHashMap<>();
        progressLabels = new LinkedHashMap<>();
        openLabels = new LinkedHashMap<>();
    }

    public Map<String, Integer> getOpen() {
        return open;
    }

    public void setOpen(Map<String, Integer> open) {
        this.open = open;
    }

    public Map<String, Integer> getClosed() {
        return closed;
    }

    public void setClosed(Map<String, Integer> closed) {
        this.closed = closed;
    }

    public Map<String, Integer> getInProgress() {
        return inProgress;
    }

    public void setInProgress(Map<String, Integer> inProgress) {
        this.inProgress = inProgress;
    }

    public String getStartStop() {
        return startStop;
    }

    public void setStartStop(String startStop) {
        this.startStop = startStop;
    }

    public Map<String, Integer> getProgressLabels() {
        return progressLabels;
    }

    public void setProgressLabels(Map<String, Integer> progressLabels) {
        this.progressLabels = progressLabels;
    }

    public Map<String, Integer> getOpenLabels() {
        return openLabels;
    }

    public void setOpenLabels(Map<String, Integer> openLabels) {
        this.openLabels = openLabels;
    }

    public List<StartStop> getFreeDays() {
        return freeDays;
    }

    public void setFreeDays(List<StartStop> freeDays) {
        this.freeDays = freeDays;
    }

    public void putToOpen(String date, Integer value) {
        this.open.put(date, value);
    }

    public void putToClosed(String date, Integer value) {
        this.closed.put(date, value);
    }

    public void putToInProgress(String date, Integer value) {
        this.inProgress.put(date, value);
    }

    public void putToInProgressLabel(String date, Integer value) {
        this.progressLabels.put(date, value);
    }

    public void putToOpenLabel(String date, Integer value) {
        this.openLabels.put(date, value);
    }

}
