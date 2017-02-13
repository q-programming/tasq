package com.qprogramming.tasq.chat;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.test.MockSecurityContext;
import com.qprogramming.tasq.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChatControllerTest {

    public static final String SOME_MESSAGE = "Some message";
    protected Account testAccount;
    @Mock
    private ChatService chatSrvMock;
    @Mock
    private ProjectService projSrvMock;
    @Mock
    private AccountService accSrvMock;
    @Mock
    private MessageSource msgMock;
    @Mock
    private Authentication authMock;
    @Mock
    private MockSecurityContext securityMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private RedirectAttributes raMock;
    @Mock
    private Model modelMock;

    private ChatController chatController;

    @Before
    public void setUp() throws Exception {
        testAccount = TestUtils.createAccount();
        testAccount.setLanguage("en");
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
        when(securityMock.getAuthentication()).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(testAccount);
        SecurityContextHolder.setContext(securityMock);
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
        chatController = new ChatController(accSrvMock, chatSrvMock, projSrvMock, msgMock);
    }

    @Test
    public void sendMessageNothing() throws Exception {
        ChatResponse chatResponse = chatController.sendMessage(TestUtils.PROJECT_ID, new MessageSent());
        assertEquals(chatResponse.getEvent(), ChatEvent.ERROR);
    }

    @Test
    public void sendMessageNoMessage() throws Exception {
        MessageSent messageSent = new MessageSent();
        messageSent.setEvent(ChatEvent.MESSAGE);
        ChatResponse chatResponse = chatController.sendMessage(TestUtils.PROJECT_ID, messageSent);
        assertEquals(chatResponse.getEvent(), ChatEvent.ERROR);
    }

    @Test
    public void sendMessagWithHtml() throws Exception {
        when(accSrvMock.findByUsername(TestUtils.USERNAME)).thenReturn(testAccount);
        MessageSent messageSent = new MessageSent();
        messageSent.setEvent(ChatEvent.MESSAGE);
        messageSent.setUsername(TestUtils.USERNAME);
        messageSent.setMessage("<html>tag</html>");
        ChatResponse chatResponse = chatController.sendMessage(TestUtils.PROJECT_ID, messageSent);
        assertEquals(chatResponse.getEvent(), ChatEvent.ERROR);
    }

    @Test
    public void sendMessag() throws Exception {
        MessageSent messageSent = new MessageSent();
        messageSent.setEvent(ChatEvent.MESSAGE);
        messageSent.setUsername(TestUtils.USERNAME);
        messageSent.setMessage(SOME_MESSAGE);

        ChatMessage message = new ChatMessage(SOME_MESSAGE, testAccount, TestUtils.PROJECT_ID);
        when(accSrvMock.findByUsername(TestUtils.USERNAME)).thenReturn(testAccount);
        when(chatSrvMock.save(any(ChatMessage.class))).thenReturn(message);
        ChatResponse chatResponse = chatController.sendMessage(TestUtils.PROJECT_ID, messageSent);
        assertEquals(chatResponse.getEvent(), ChatEvent.MESSAGE);
    }


    @Test
    public void sendOnlineOffline() throws Exception {
        when(accSrvMock.findByUsername(TestUtils.USERNAME)).thenReturn(testAccount);
        MessageSent messageSent = new MessageSent();
        messageSent.setEvent(ChatEvent.ONLINE);
        messageSent.setUsername(TestUtils.USERNAME);
        ChatResponse chatResponse = chatController.sendMessage(TestUtils.PROJECT_ID, messageSent);
        assertEquals(chatResponse.getEvent(), ChatEvent.ONLINE);
    }

    @Test
    public void sendOnlineOfflineNoUsername() throws Exception {
        MessageSent messageSent = new MessageSent();
        messageSent.setEvent(ChatEvent.ONLINE);
        ChatResponse chatResponse = chatController.sendMessage(TestUtils.PROJECT_ID, messageSent);
        assertEquals(chatResponse.getEvent(), ChatEvent.ERROR);
    }


    @Test(expected = TasqAuthException.class)
    public void chatCannotView() throws Exception {
        Project project = TestUtils.createProject();
        when(projSrvMock.findByProjectId(TestUtils.PROJECT_ID)).thenReturn(project);
        when(projSrvMock.canView(project)).thenReturn(false);
        chatController.chat(TestUtils.PROJECT_ID, modelMock, raMock);
    }

    @Test
    public void chatNoProject() throws Exception {
        Project project = TestUtils.createProject();
        chatController.chat(TestUtils.PROJECT_ID, modelMock, raMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void chat() {
        Project project = TestUtils.createProject();
        when(projSrvMock.findByProjectId(TestUtils.PROJECT_ID)).thenReturn(project);
        when(projSrvMock.canView(project)).thenReturn(true);
        chatController.chat(TestUtils.PROJECT_ID, modelMock, raMock);
        verify(modelMock, times(1)).addAttribute("chatProject", project);
    }

    @Test
    public void getProjectMessages() throws Exception {
        ChatMessage chat = new ChatMessage("test", testAccount, TestUtils.PROJECT_ID);
        ChatMessage chat2 = new ChatMessage("test", testAccount, TestUtils.PROJECT_ID);
        ChatMessage chat3 = new ChatMessage("test", testAccount, TestUtils.PROJECT_ID);
        ChatMessage chat4 = new ChatMessage("test", testAccount, TestUtils.PROJECT_ID);
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(chat);
        messages.add(chat2);
        messages.add(chat3);
        messages.add(chat4);
        when(chatSrvMock.findByProject(TestUtils.PROJECT_ID)).thenReturn(messages);
        List<ChatResponse> projectMessages = chatController.getProjectMessages(TestUtils.PROJECT_ID);
        assertTrue(projectMessages.size() == 4);


    }

    @Test
    public void validateMessageHTML() throws Exception {
        String message = "String with <html>tags</html>";
        ResponseEntity responseEntity = chatController.validateMessage(message);
        assertTrue(ResultData.Code.ERROR.equals(((ResultData) responseEntity.getBody()).code));
    }

    @Test
    public void validateMessageEmpty() throws Exception {
        String message = "";
        ResponseEntity responseEntity = chatController.validateMessage(message);
        assertEquals(ResultData.Code.ERROR, ((ResultData) responseEntity.getBody()).code);
    }

    @Test
    public void validateMessage4k() throws Exception {
        String message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut quis sem in augue sagittis imperdiet id in arcu. Mauris non metus vulputate, pulvinar ante et, placerat tellus. Integer maximus bibendum lobortis. Donec eros sem, vulputate commodo ligula sit amet, rhoncus bibendum turpis. Pellentesque sed urna placerat, suscipit mi vel, rutrum urna. Nullam leo dolor, tempus ac arcu at, dignissim molestie ex. Phasellus mollis egestas odio, at volutpat orci malesuada at. Sed placerat convallis libero, et sagittis turpis feugiat quis. Nullam blandit ornare egestas. Proin quam libero, hendrerit ac convallis a, dignissim ac est. Proin quis ipsum commodo sem luctus suscipit. Sed varius fermentum scelerisque. Nulla rutrum ac orci hendrerit volutpat." +
                "Nulla eleifend dolor et erat commodo, at condimentum erat fringilla. Proin non elit eget libero molestie ultricies quis in velit. Quisque rutrum ligula semper erat mollis, id eleifend massa blandit. Nulla viverra, lorem et imperdiet condimentum, nibh ligula suscipit metus, sed dictum tortor elit et velit. Sed semper velit sed enim aliquam cursus. Phasellus tempus augue eu nisl dapibus ultrices. Praesent vestibulum dapibus mauris. Nullam tempor venenatis nulla. Aenean ac libero porttitor augue accumsan placerat. Cras eget cursus ante.\n" +
                "Integer dolor purus, bibendum in tortor at, imperdiet finibus mauris. Praesent commodo, massa consequat molestie gravida, tellus lacus posuere velit, nec euismod metus orci nec enim. Sed eros elit, finibus at tortor id, congue molestie arcu. In magna elit, bibendum nec sapien a, aliquam venenatis est. Suspendisse imperdiet vel turpis luctus dignissim. Donec id massa sed quam pellentesque tristique. Praesent non egestas velit, eget mattis orci. Cras ex tellus, feugiat a consectetur vitae, varius ut ipsum. Ut luctus in urna non lacinia.\n" +
                "Nulla semper tincidunt sapien, eu sagittis quam condimentum ac. Praesent ac tellus mauris. In tincidunt suscipit dui. Integer fermentum sem eu blandit consectetur. Proin vestibulum tellus vel turpis volutpat, eu iaculis lorem lobortis. Quisque est nisi, rutrum in vehicula nec, lacinia quis sem. Donec tortor ex, dignissim at lorem vitae, sagittis mollis libero. Suspendisse tempor sodales mi, eget sagittis leo. Cras nec fringilla nisl, et suscipit orci. Duis iaculis ligula at vestibulum dignissim. Maecenas feugiat quam ac ante ornare mattis.\n" +
                "Donec vel semper eros. Mauris at urna nec mi tincidunt imperdiet. Aliquam id ligula elit. Interdum et malesuada fames ac ante ipsum primis in faucibus. In vel convallis tortor. Sed in accumsan felis, et convallis nisl. Morbi bibendum placerat sapien, ac aliquam nulla posuere et. Phasellus sagittis quam vel quam pulvinar bibendum. Aenean vel rhoncus eros. In turpis lacus, mattis nec neque lobortis, venenatis pellentesque lorem. Donec orci orci, porta at pulvinar quis, facilisis vitae turpis. Nam molestie arcu nec diam lobortis, eu vehicula lectus viverra. Pellentesque eget erat sit amet nisl fermentum faucibus id eget tortor. Nulla euismod pulvinar turpis, non egestas enim bibendum et. Nam pretium quis eros tincidunt molestie. Aenean orci risus, mattis eget ipsum vitae, tristique lacinia felis.\n" +
                "Maecenas feugiat nisi at lorem pellentesque, ut ornare justo aliquet. Suspendisse quis sapien quis sapien varius porta. Praesent auctor odio ut nibh ornare, at semper lacus pharetra. Vestibulum efficitur congue laoreet. Quisque mattis sodales est sed aliquam. Mauris non faucibus velit. Praesent venenatis justo et nisl varius aliquam ac eget risus. Duis eget tortor ex. Vestibulum aliquam aliquet odio, non consectetur ex venenatis nec. Aenean sapien lacus, tempus sed feugiat vitae, lobortis at odio. Praesent tempor sapien id eleifend sollicitudin. Quisque at venenatis sem. Morbi sit amet neque non ligula faucibus aliquam eu et dui. Nulla facilisi. Sed elementum in ipsum sed rutrum. Ut et auctor ligula.\n" +
                "Maecenas maximus nec sapien ac sagittis. Aenean condimentum dapibus sapien, eu laoreet nunc blandit sit amet. Nunc sed.";
        ResponseEntity responseEntity = chatController.validateMessage(message);
        assertTrue(ResultData.Code.ERROR.equals(((ResultData) responseEntity.getBody()).code));
    }

    @Test
    public void validateMessageLongButOk() throws Exception {
        String message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut quis sem in augue sagittis imperdiet id in arcu. Mauris non metus vulputate, pulvinar ante et, placerat tellus. Integer maximus bibendum lobortis. Donec eros sem, vulputate commodo ligula sit amet, rhoncus bibendum turpis. Pellentesque sed urna placerat, suscipit mi vel, rutrum urna. Nullam leo dolor, tempus ac arcu at, dignissim molestie ex. Phasellus mollis egestas odio, at volutpat orci malesuada at. Sed placerat convallis libero, et sagittis turpis feugiat quis. Nullam blandit ornare egestas. Proin quam libero, hendrerit ac convallis a, dignissim ac est. Proin quis ipsum commodo sem luctus suscipit. Sed varius fermentum scelerisque. Nulla rutrum ac orci hendrerit volutpat." +
                "Nulla eleifend dolor et erat commodo, at condimentum erat fringilla. Proin non elit eget libero molestie ultricies quis in velit. Quisque rutrum ligula semper erat mollis, id eleifend massa blandit. Nulla viverra, lorem et imperdiet condimentum, nibh ligula suscipit metus, sed dictum tortor elit et velit. Sed semper velit sed enim aliquam cursus. Phasellus tempus augue eu nisl dapibus ultrices. Praesent vestibulum dapibus mauris. Nullam tempor venenatis nulla. Aenean ac libero porttitor augue accumsan placerat. Cras eget cursus ante.\n" +
                "Integer dolor purus, bibendum in tortor at, imperdiet finibus mauris. Praesent commodo, massa consequat molestie gravida, tellus lacus posuere velit, nec euismod metus orci nec enim. Sed eros elit, finibus at tortor id, congue molestie arcu. In magna elit, bibendum nec sapien a, aliquam venenatis est. Suspendisse imperdiet vel turpis luctus dignissim. Donec id massa sed quam pellentesque tristique. Praesent non egestas velit, eget mattis orci. Cras ex tellus, feugiat a consectetur vitae, varius ut ipsum. Ut luctus in urna non lacinia.\n" +
                "Nulla semper tincidunt sapien, eu sagittis quam condimentum ac. Praesent ac tellus mauris. In tincidunt suscipit dui. Integer fermentum sem eu blandit consectetur. Proin vestibulum tellus vel turpis volutpat, eu iaculis lorem lobortis. Quisque est nisi, rutrum in vehicula nec, lacinia quis sem. Donec tortor ex, dignissim at lorem vitae, sagittis mollis libero. Suspendisse tempor sodales mi, eget sagittis leo. Cras nec fringilla nisl, et suscipit orci. Duis iaculis ligula at vestibulum dignissim. Maecenas feugiat quam ac ante ornare mattis.\n" +
                "Donec vel semper eros. Mauris at urna nec mi tincidunt imperdiet. Aliquam id ligula elit. Interdum et malesuada fames ac ante ipsum primis in faucibus. In vel convallis tortor. Sed in accumsan felis, et convallis nisl. Morbi bibendum placerat sapien, ac aliquam nulla posuere et. Phasellus sagittis quam vel quam pulvinar bibendum. Aenean vel rhoncus eros. In turpis lacus, mattis nec neque lobortis, venenatis pellentesque lorem. Donec orci orci, porta at pulvinar quis, facilisis vitae turpis. Nam molestie arcu nec diam lobortis, eu vehicula lectus viverra. Pellentesque eget erat sit amet nisl fermentum faucibus id eget tortor. Nulla euismod pulvinar turpis, non egestas enim bibendum et. Nam pretium quis eros tincidunt molestie. Aenean orci risus, mattis eget ipsum vitae, tristique lacinia felis.\n" +
                "Maecenas feugiat nisi at lorem pellentesque, ut ornare justo aliquet. Suspendisse quis sapien quis sapien varius porta. Praesent auctor odio ut nibh ornare, at semper lacus pharetra. Vestibulum efficitur congue laoreet. Quisque mattis sodales est sed aliquam. Mauris non faucibus velit. Praesent venenatis justo et nisl varius aliquam ac eget risus. Duis eget tortor ex. Vestibulum aliquam aliquet odio, non consectetur ex venenatis nec. Aenean sapien lacus, tempus sed feugiat vitae, lobortis at odio. Praesent tempor sapien id eleifend sollicitudin. Quisque at venenatis sem. Morbi sit amet neque non ligula faucibus aliquam eu et dui. Nulla facilisi. Sed elementum in ipsum sed rutrum. Ut et auctor ligula.\n";
        ResponseEntity responseEntity = chatController.validateMessage(message);
        assertTrue(ResultData.Code.OK.equals(((ResultData) responseEntity.getBody()).code));
    }

    @Test
    public void testMessages() {
        ChatMessage chat = new ChatMessage("test", testAccount, TestUtils.PROJECT_ID);
        chat.setId(1L);
        ChatMessage chat2 = new ChatMessage();
        chat2.setTime(chat.getRawTime());
        BeanUtils.copyProperties(chat, chat2);
        assertEquals(chat, chat2);
        assertEquals(chat.hashCode(), chat2.hashCode());
    }


}