package com.qprogramming.tasq.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by Khobar on 08.01.2017.
 */
@Service
public class ChatService {

    private ChatRepository chatRepo;

    @Autowired
    public ChatService(ChatRepository chatRepo) {
        this.chatRepo = chatRepo;
    }


    public List<ChatMessage> findByProject(String id) {
        return chatRepo.findByProject(id);
    }

    public ChatMessage save(ChatMessage chatMessage) {
        return chatRepo.save(chatMessage);
    }
}
