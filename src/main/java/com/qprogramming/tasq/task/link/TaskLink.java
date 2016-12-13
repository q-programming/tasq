package com.qprogramming.tasq.task.link;

import javax.persistence.*;

@Entity
public class TaskLink {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "link_seq_gen")
    @SequenceGenerator(name = "link_seq_gen", sequenceName = "link_id_seq", allocationSize = 1)
    private Long id;

    @Column
    private String taskA;
    @Column
    private String taskB;
    @Enumerated(EnumType.STRING)
    private TaskLinkType linkType;

    public TaskLink() {
    }

    public TaskLink(String a, String b, TaskLinkType type) {
        setTaskA(a);
        setTaskB(b);
        setLinkType(type);
    }


    public String getTaskA() {
        return taskA;
    }

    public void setTaskA(String taskA) {
        this.taskA = taskA;
    }

    public String getTaskB() {
        return taskB;
    }

    public void setTaskB(String taskB) {
        this.taskB = taskB;
    }

    public TaskLinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(TaskLinkType linkType) {
        this.linkType = linkType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
