package com.qprogramming.tasq.task.importexport.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class ProjectXML {

    private String name;
    private List<TaskXML> taskList;

    public String getName() {
        return name;
    }

    @XmlElement(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public List<TaskXML> getTaskList() {
        return taskList;
    }

    @XmlElementWrapper(name = "taskList")
    @XmlElement(name = "task")
    public void setTaskList(List<TaskXML> taskList) {
        this.taskList = taskList;
    }

}
