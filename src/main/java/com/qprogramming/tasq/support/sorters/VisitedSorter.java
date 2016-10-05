package com.qprogramming.tasq.support.sorters;

import com.qprogramming.tasq.account.LastVisited;

import java.util.Comparator;

public class VisitedSorter implements Comparator<LastVisited> {


    public int compare(LastVisited a, LastVisited b) {
        if (a.getTime().before(b.getTime())) {
            return 1;
        } else if (a.getTime().after(b.getTime())) {
            return -1;
        }
        return 0;
    }
}
