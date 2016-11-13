package com.qprogramming.tasq.test;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.LastVisited;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.TaskType;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Khobar on 01.10.2016.
 */
public class TestUtils {


    public static final String TEST_1 = "TEST-1";
    public static final String TEST_2 = "TEST-2";
    public static final String EMAIL = "user@test.com";
    public static final String TASK_NAME = "taskName";
    public static final String TASK_DESCRIPTION = "Task description";
    public static final String PROJECT_NAME = "TestProject";
    public static final String PROJECT_ID = "TEST";
    public static final String PROJECT_DESCRIPTION = "Description";
    public static final String PASSWORD = "password";
    public static final String LAMB = "Lamb";
    public static final String ZOE = "Zoe";
    public static final String ART = "Art";
    public static final String DOE = "Doe";
    public static final String KATE = "Kate";
    public static final String JOHN = "John";
    public static final String ADAM = "Adam";
    public static final String MARRY = "Marry";
    public static final String USER = "user";
    public static final String USERNAME = USER;


    public static Task createTask(String name, int no, Project project) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(TASK_DESCRIPTION);
        task.setProject(project);
        task.setId(project.getProjectId() + "-" + no);
        task.setPriority(TaskPriority.MAJOR);
        task.setType(TaskType.USER_STORY);
        task.setStory_points(2);
        task.setState(TaskState.TO_DO);
        return task;
    }

    public static Account createAccount() {
        Account testAccount = new Account(EMAIL, "", "user", Roles.ROLE_ADMIN);
        return createAccount("user", "surname");
    }

    public static Account createAccount(String name, String surname) {
        Account account = new Account(name + "@test.com", "", name, Roles.ROLE_POWERUSER);
        account.setName(name);
        account.setSurname(surname);
        account.setLanguage("en");
        account.setId(1L);
        return account;
    }

    public static List<Account> createAccountList() {
        List<Account> accountsList = new LinkedList<Account>();
        accountsList.add(createAccount(JOHN, DOE));
        accountsList.add(createAccount(ADAM, ART));
        accountsList.add(createAccount(ADAM, ZOE));
        accountsList.add(createAccount(MARRY, LAMB));
        accountsList.add(createAccount(KATE, DOE));
        return accountsList;
    }


    public static Project createProject(Long id, String name) {
        Project project = createProject(id);
        project.setName(name);
        return project;
    }

    public static Project createProject(Long id) {
        Project project = new Project(PROJECT_NAME, createAccount());
        project.setDescription(PROJECT_DESCRIPTION);
        project.setProjectId(PROJECT_ID);
        project.setId(id);
        return project;
    }

    public static Project createProject() {
        return createProject(1L);
    }

    public static WorkLog createWorkLog(Task task, LogType type, String msg, Date date) {
        WorkLog wl = new WorkLog();
        wl.setTask(task);
        wl.setProject_id(task.getProject().getId());
        wl.setAccount(Utils.getCurrentAccount());
        wl.setTimeLogged(new Date());
        wl.setTime(new Date());
        wl.setType(type);
        wl.setMessage(msg);
        return wl;
    }

    public static List<LastVisited> createLastVisitedTasks(int count) {
        Project project = TestUtils.createProject();
        List<LastVisited> visited = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            visited.add(new LastVisited(TestUtils.createTask(TestUtils.TASK_NAME, i, project), createAccount().getId()));
        }
        return visited;
    }


}
