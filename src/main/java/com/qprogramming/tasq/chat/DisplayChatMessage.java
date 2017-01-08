package com.qprogramming.tasq.chat;

import com.qprogramming.tasq.account.DisplayAccount;
import com.qprogramming.tasq.support.Utils;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * Created by Khobar on 07.01.2017.
 */
public class DisplayChatMessage {
    private DisplayAccount author;
    private String time;
    private String message;

    public DisplayChatMessage(ChatMessage message) {
        BeanUtils.copyProperties(message, this);
        if (message.getAccount() != null) {
            author = new DisplayAccount(message.getAccount());
        }
    }

    public DisplayAccount getAccount() {
        return author;
    }

    public void setAccount(DisplayAccount account) {
        this.author = account;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
