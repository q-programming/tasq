package com.qprogramming.tasq.chat;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.projects.ProjectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Khobar on 07.01.2017.
 */
@Controller
public class ChatController {

    private AccountService accSrv;
    private ChatService chatSrv;
    private ProjectService projSrv;

    @Autowired
    public ChatController(AccountService accSrv, ChatService chatSrv, ProjectService projSrv) {
        this.accSrv = accSrv;
        this.chatSrv = chatSrv;
        this.projSrv = projSrv;
    }

    @MessageMapping("/{projectId}/send")
    @SendTo("/chat/{projectId}/messages")
    public DisplayChatMessage sendMessage(@DestinationVariable String projectId, MessageSent message) throws Exception {
        Thread.sleep(500); // simulated delay
        if (StringUtils.isNotBlank(message.getUsername())) {//TODO secure if something is missing, for ex. no project etc.?
            Account account = accSrv.findByUsername(message.getUsername());
            ChatMessage chatMessage = chatSrv.save(new ChatMessage(message.getMessage(), account, projectId));
            return new DisplayChatMessage(chatMessage);
        } else {//only technical messages, no need to store them
            return new DisplayChatMessage(new ChatMessage(message.getMessage()));
        }
    }


    @RequestMapping("/{projectId}/chat")
    public String chat(@PathVariable String projectId, Model model) {
        //TODO find project
        model.addAttribute("chatProject", projSrv.findByProjectId(projectId));
        return "chat/chat";
    }

    @ResponseBody
    @RequestMapping("/{projectId}/chat/projectmessages")
    public List<DisplayChatMessage> getProjectMessages(@PathVariable String projectId) {
        return chatSrv.findByProject(projectId).stream().map(DisplayChatMessage::new).collect(Collectors.toList());
    }
}
