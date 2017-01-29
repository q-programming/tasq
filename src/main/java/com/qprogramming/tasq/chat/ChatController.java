package com.qprogramming.tasq.chat;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
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
    private MessageSource msg;

    @Autowired
    public ChatController(AccountService accSrv, ChatService chatSrv, ProjectService projSrv, MessageSource msg) {
        this.accSrv = accSrv;
        this.chatSrv = chatSrv;
        this.projSrv = projSrv;
        this.msg = msg;
    }

    @MessageMapping("/{projectId}/send")
    @SendTo("/chat/{projectId}/messages")
    public ChatResponse sendMessage(@DestinationVariable String projectId, MessageSent message) throws Exception {
        if (ChatEvent.MESSAGE.equals(message.getEvent())) {
            Thread.sleep(500); // simulated delay
            if (StringUtils.isNotBlank(message.getUsername())) {
                ResultData validate = validate(message.getMessage());
                if (ResultData.Code.ERROR.equals(validate.code)) {
                    return new ChatResponse.ResponseBuilder().message(validate.message).event(ChatEvent.ERROR).build();
                } else {
                    Account account = accSrv.findByUsername(message.getUsername());
                    ChatMessage chatMessage = chatSrv.save(new ChatMessage(message.getMessage(), account, projectId));
                    return new ChatResponse(chatMessage);
                }
            }
        } else if (ChatEvent.ONLINE.equals(message.getEvent()) || ChatEvent.OFFLINE.equals(message.getEvent())) {
            if (StringUtils.isNotBlank(message.getUsername())) {
                Account account = accSrv.findByUsername(message.getUsername());
                return new ChatResponse.ResponseBuilder()
                        .user(account)
                        .event(message.getEvent())
                        .time(Utils.convertDateTimeToString(new Date()))
                        .build();
            }
        }
        return new ChatResponse.ResponseBuilder().message("Nothing to process").event(ChatEvent.ERROR).build();
    }

    @RequestMapping("/{projectId}/chat")
    public String chat(@PathVariable String projectId, Model model, RedirectAttributes ra) {
        Project project = projSrv.findByProjectId(projectId);
        if (project != null) {
            if (projSrv.canView(project)) {
                model.addAttribute("chatProject", project);
                return "chat/chat";
            } else {
                throw new TasqAuthException(msg, "role.error.project.permission");
            }
        }
        MessageHelper.addErrorAttribute(ra, msg.getMessage("project.notexists", null, Utils.getCurrentLocale()));
        return "redirect:/projects";
    }

    @ResponseBody
    @RequestMapping("/{projectId}/chat/projectmessages")
    public List<ChatResponse> getProjectMessages(@PathVariable String projectId) {
        return chatSrv.findByProject(projectId).stream().map(ChatResponse::new).collect(Collectors.toList());
    }

    @ResponseBody
    @RequestMapping("/chat/validate")
    public ResponseEntity validateMessage(@RequestParam String message) {
        return ResponseEntity.ok(validate(message));
    }

    private ResultData validate(@RequestParam String message) {
        if (StringUtils.isNotBlank(message)) {
            if (Utils.containsHTMLTags(message)) {
                return new ResultData(ResultData.Code.ERROR, msg.getMessage("chat.error.html", null, Utils.getCurrentLocale()));
            }
            if (message.length() > 4000) {
                return new ResultData(ResultData.Code.ERROR, msg.getMessage("chat.error.length", null, Utils.getCurrentLocale()));
            }
            return new ResultData(ResultData.Code.OK);
        }
        return new ResultData(ResultData.Code.ERROR, msg.getMessage("chat.error.empty", null, Utils.getCurrentLocale()));
    }
}
