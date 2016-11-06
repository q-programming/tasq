package com.qprogramming.tasq.task.worklog;

/**
 * Created by Khobar on 06.11.2016.
 */
public enum TaskResolution {
    FINISHED("task.resolution.finished"), FIXED("task.resolution.fixed"), WONT_FIX("task.resolution.wontfix"),
    DUPLICATE("task.resolution.duplicate"), INCOMPLETE("task.resolution.incomplete"),
    CANNOT_REPRODUCE("task.resolution.cannotreproduce"), UNRESOLVED("task.resolution.unresolved");

    private String code;

    TaskResolution(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
