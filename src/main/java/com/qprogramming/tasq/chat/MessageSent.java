package com.qprogramming.tasq.chat;

/**
 * Created by Khobar on 08.01.2017.
 */
public class MessageSent {

    private String message;
    private String username;
    private String project;

    public MessageSent() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
}
