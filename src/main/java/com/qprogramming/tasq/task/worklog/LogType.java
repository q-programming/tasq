/**
 *
 */
package com.qprogramming.tasq.task.worklog;

public enum LogType {
    CREATE("log.type.create"), CHANGE("log.type.change"), LOG("log.type.log"), STATUS(
            "log.type.status"), ESTIMATE("log.type.estimate"), REOPEN(
            "log.type.reopen"), CLOSED("log.type.closed"), COMMENT(
            "log.type.comment"), EDITED("log.type.edited"), ASSIGNED(
            "log.type.assign"), PRIORITY("log.type.priority"), DELETED(
            "log.type.delete"),CLONED("log.type.clone"), SPRINT_START("log.type.sprintstart"), SPRINT_STOP(
            "log.type.sprintstop"), TASK_LINK("log.type.taskLink"), TASK_LINK_DEL(
            "log.type.taskLink.delete"), TASKSPRINTADD("log.type.taskSprintAdd"), TASKSPRINTREMOVE(
            "log.type.taskSprintRemove"), SUBTASK("log.type.subtask"), SUBTASK2TASK("log.type.subtask2task"), ASSIGN_TO_PROJ(
            "log.type.assignProj"), REMOVE_FROM_PROJ("log.type.removeProj"), OTHER("log.type.other"),PROJ_REMOVE("log.type.project.removed");

    private String code;

    LogType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return code;
    }

    public String getString() {
        return super.toString().toLowerCase().replaceAll("_", "");
    }
}
