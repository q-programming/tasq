package com.qprogramming.tasq.projects.dto;

import com.qprogramming.tasq.account.DisplayAccount;
import com.qprogramming.tasq.support.Utils;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectStats extends ProjectDataBase {
    private Map<String, Float> logged;
    private LocalDate startDate;
    private LocalDate lastEventDate;
    private boolean active;
    private long taskCount;
    private long subTaskCount;
    private String totalEstimate;
    private String totalLogged;
    private String totalRemaining;
    private List<ActiveAccount> topActive;
    private Map<String, Integer> daysOfWeek;

    public ProjectStats() {
        super();
        this.logged = new HashMap<>();
    }

    public Map<String, Float> getlogged() {
        return logged;
    }

    public void putLogged(String key, Float value) {
        logged.put(key, value);
    }

    public void setStartDate(LocalDate date) {
        startDate = date;
    }

    public void setLastEventDate(LocalDate lastEventDate) {
        this.lastEventDate = lastEventDate;
    }

    public String getLastEventDate() {
        return Utils.convertDateToString(lastEventDate.toDate());
    }

    public String getStartDate() {
        return Utils.convertDateToString(startDate.toDate());
    }

    public int getDaysCount() {
        return Days.daysBetween(startDate, lastEventDate).getDays();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setTaskCount(long taskCount) {
        this.taskCount = taskCount;
    }

    public long getTaskCount() {
        return taskCount;
    }

    public long getSubTaskCount() {
        return subTaskCount;
    }

    public void setSubTaskCount(long subTaskCount) {
        this.subTaskCount = subTaskCount;
    }

    public String getTotalEstimate() {
        return totalEstimate;
    }

    public void setTotalEstimate(String totalEstimate) {
        this.totalEstimate = totalEstimate;
    }

    public String getTotalLogged() {
        return totalLogged;
    }

    public void setTotalLogged(String totalLogged) {
        this.totalLogged = totalLogged;
    }

    public String getTotalRemaining() {
        return totalRemaining;
    }

    public void setTotalRemaining(String totalRemaining) {
        this.totalRemaining = totalRemaining;
    }

    public List<ActiveAccount> getTopActive() {
        return topActive;
    }

    public void setTopActive(List<ActiveAccount> topActive) {
        this.topActive = topActive;
    }

    public void setDaysOfWeek(Map<String, Integer> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public Map<String, Integer> getDaysOfWeek() {
        return daysOfWeek;
    }

    public static class ActiveAccount {
        private DisplayAccount account;
        private Long count;

        public ActiveAccount(DisplayAccount account, Long count) {
            this.account = account;
            this.count = count;
        }

        public DisplayAccount getAccount() {
            return account;
        }

        public void setAccount(DisplayAccount account) {
            this.account = account;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
}

