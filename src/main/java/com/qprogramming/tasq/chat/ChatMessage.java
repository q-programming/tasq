package com.qprogramming.tasq.chat;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.support.Utils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Khobar on 07.01.2017.
 */
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_seq_gen")
    @SequenceGenerator(name = "chat_seq_gen", sequenceName = "chat_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    private Account account;

    @Column
    private Date time;

    @Column(length = 4000)
    private String message;

    @Column
    private String project;

    public ChatMessage() {
    }

    public ChatMessage(String message) {
        this.message = message;
        this.time = new Date();
    }

    public ChatMessage(String message, Account account, String project) {
        this.account = account;
        this.message = message;
        this.time = new Date();
        this.project = project;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Date getRawTime() {
        return this.time;
    }

    public String getTime() {
        return Utils.convertDateTimeToString(time);
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatMessage that = (ChatMessage) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        return project != null ? project.equals(that.project) : that.project == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (project != null ? project.hashCode() : 0);
        return result;
    }
}
