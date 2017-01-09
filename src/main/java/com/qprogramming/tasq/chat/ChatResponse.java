package com.qprogramming.tasq.chat;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.DisplayAccount;
import org.springframework.beans.BeanUtils;

/**
 * Created by Khobar on 07.01.2017.
 */
public class ChatResponse {
    private DisplayAccount user;
    private String time;
    private String message;
    private ChatEvent event;

    public ChatResponse() {
    }

    public ChatResponse(ResponseBuilder builder) {
        this.user = builder.user;
        this.time = builder.time;
        this.message = builder.message;
        this.event = builder.event;
    }

    /**
     * Create new message response out of chatmessage
     *
     * @param message
     */
    public ChatResponse(ChatMessage message) {
        BeanUtils.copyProperties(message, this);
        if (message.getAccount() != null) {
            user = new DisplayAccount(message.getAccount());
        }
        this.event = ChatEvent.MESSAGE;
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

    public DisplayAccount getUser() {
        return user;
    }

    public void setUser(DisplayAccount user) {
        this.user = user;
    }

    public ChatEvent getEvent() {
        return event;
    }

    public void setEvent(ChatEvent event) {
        this.event = event;
    }

    public static class ResponseBuilder {
        private DisplayAccount user;
        private String time;
        private String message;
        private ChatEvent event;

        public ResponseBuilder author(DisplayAccount author) {
            this.user = author;
            return this;
        }

        public ResponseBuilder user(Account author) {
            if (author != null) {
                this.user = new DisplayAccount(author);
            }
            return this;
        }

        public ResponseBuilder time(String time) {
            this.time = time;
            return this;
        }

        public ResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ResponseBuilder event(ChatEvent event) {
            this.event = event;
            return this;
        }

        public ChatResponse build() {
            return new ChatResponse(this);
        }

    }
}
