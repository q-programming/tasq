package com.qprogramming.tasq.account;

import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Khobar on 04.10.2016.
 */

@Entity
@Table(name = "visited")
public class LastVisited implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "visit_seq_gen")
    @SequenceGenerator(name = "visit_seq_gen", sequenceName = "visit_id_seq", allocationSize = 1)
    private Long id;

    @Column
    private String itemId;

    @Column
    private String itemName;

    @Enumerated(EnumType.STRING)
    private TaskType type;

    @Column
    private Date time;

    @Column
    private Long account;

    public LastVisited() {
        //Default constructor
    }

    public LastVisited(Task task, Long account) {
        this.itemId = task.getId();
        this.itemName = task.getName();
        this.account = account;
        this.time = new Date();
        this.type = (TaskType) task.getType();
    }

    public LastVisited(Project project, Long account) {
        this.itemId = project.getProjectId();
        this.itemName = project.getName();
        this.account = account;
        this.time = new Date();
    }
    public LastVisited(Task task, Long account, Date time) {
        this(task,account);
        this.setTime(time);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public Long getAccount() {
        return account;
    }

    public void setAccount(Long account) {
        this.account = account;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LastVisited that = (LastVisited) o;
        return Objects.equals(getItemId(), that.getItemId()) &&
                getType() == that.getType() &&
                Objects.equals(getAccount(), that.getAccount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItemId(), getType(), getAccount());
    }

    @Override
    public String toString() {
        return "[" + itemId + "] " + itemName + ", type=" + type + ", time=" + time + ", account=" + account + '}';
    }
}
