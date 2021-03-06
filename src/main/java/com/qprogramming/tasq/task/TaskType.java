package com.qprogramming.tasq.task;

import java.util.LinkedList;
import java.util.List;

public enum TaskType {
    TASK(false), USER_STORY(false), ISSUE(false), BUG(false), CHANGE_REQUEST(false), IDLE(false), SUBTASK(
            true), SUBBUG(true);
    private boolean subtask;

    TaskType(boolean subtask) {
        this.subtask = subtask;
    }

    public static TaskType toType(String string) {
        return valueOf(string.toUpperCase());
    }

    public static List<TaskType> getTypes(boolean subtask) {
        List<TaskType> types = new LinkedList<>();
        for (TaskType type : TaskType.values()) {
            if (type.isSubtask() == subtask) {
                types.add(type);
            }
        }
        return types;
    }

    @Override
    public String toString() {
        // only capitalize the first letter
        String s = super.toString();
        s = s.replaceAll("_", " ");
        return s.substring(0, 1) + s.substring(1).toLowerCase();
    }

    public String getEnum() {
        return super.toString();
    }

    public String getDescription() {
        return toString();
    }

    public String getCode() {
        return super.toString().toLowerCase();
    }

    public boolean isSubtask() {
        return subtask;
    }

    public void setSubtask(boolean subtask) {
        this.subtask = subtask;
    }

}
