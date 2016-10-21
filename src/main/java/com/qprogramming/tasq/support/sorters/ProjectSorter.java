package com.qprogramming.tasq.support.sorters;

import com.qprogramming.tasq.projects.Project;

import java.util.Comparator;

public class ProjectSorter implements Comparator<Project> {

    private SORTBY sortBy;
    private boolean isDescending;
    private String active_project;

    public ProjectSorter(SORTBY sortBy, boolean isDescending) {
        this.sortBy = sortBy;
        this.isDescending = isDescending;
    }

    /**
     * @param sortBy
     * @param active_project
     * @param isDescending
     */
    public ProjectSorter(SORTBY sortBy, String active_project,
                         boolean isDescending) {
        this.active_project = active_project;
        this.isDescending = isDescending;
        this.sortBy = sortBy;
    }

    public int compare(Project a, Project b) {
        int result;
        switch (sortBy) {
            // sort by active , then just by name
            case LAST_VISIT:
                if (a.getProjectId().equals(active_project)) {
                    result = 1;
                } else if (b.getProjectId().equals(active_project)) {
                    result = -1;
                } else {
                    result = a.getName().compareTo(b.getName());
                }
                return isDescending ? -result : result;
            case NAME:
                result = a.getName().compareTo(b.getName());
                return isDescending ? -result : result;
            default:
                result = 0;
                break;
        }
        return isDescending ? -result : result;
    }

    public enum SORTBY {
        NAME, START_DATE, LAST_VISIT
    }
}
