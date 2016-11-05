package com.qprogramming.tasq.signup;

import com.fasterxml.uuid.Generators;
import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountRepository;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.config.ResourceService;
import com.qprogramming.tasq.mail.MailMail;
import com.qprogramming.tasq.manage.AppService;
import com.qprogramming.tasq.manage.ThemeService;
import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.test.MockSecurityContext;
import com.qprogramming.tasq.test.TestUtils;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.qprogramming.tasq.test.TestUtils.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SignupControllerTest {

    private static final String PASSWORD = "password";
    private static final String EMAIL = "user@test.com";
    private static final String NEW_EMAIL = "newuser@test.com";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private SignupController signupCtr;
    @InjectMocks
    private AccountService accountSrv;
    @Mock
    private EntityManager entityManagerMock;
    @Mock
    private AccountRepository accRepoMock;
    @Mock
    private MockSecurityContext securityMock;
    @Mock
    private RedirectAttributes raMock;
    @Mock
    private Authentication authMock;
    @Mock
    private Model modelMock;
    @Mock
    private MessageSource msgMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private SessionLocaleResolver localeResolver;
    @Mock
    private HttpSession httpSesssionMock;
    @Mock
    private ServletContext scMock;
    @Mock
    private ServletOutputStream outStreamMock;
    @Mock
    private VelocityEngine velocityMock;
    @Mock
    private ResourceService resourceMock;
    @Mock
    private MailMail mailerMock;
    @Mock
    private ThemeService themeSrvMock;
    @Mock
    private PasswordEncoder encoderMock;
    @Mock
    private AppService appSrvMock;
    private Account testAccount;
    private List<Account> accountsList;

    @Before
    public void setUp() {
        accountSrv = new AccountService(accRepoMock, msgMock, velocityMock, resourceMock, mailerMock, encoderMock,
                appSrvMock);
        signupCtr = new SignupController(accountSrv, msgMock, themeSrvMock, appSrvMock);
        testAccount = TestUtils.createAccount();
        testAccount.setLanguage("en");
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
        when(securityMock.getAuthentication()).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(testAccount);
        SecurityContextHolder.setContext(securityMock);
        accountsList = createAccountList();
    }

    @Test
    public void signUpAndConfirmTest() {
        try {
            when(requestMock.getSession()).thenReturn(httpSesssionMock);
            URL fileURL = getClass().getResource("/com/qprogramming/tasq/avatar.png");
            when(httpSesssionMock.getServletContext()).thenReturn(scMock);
            when(scMock.getRealPath(anyString())).thenReturn(fileURL.getFile());
            when(responseMock.getOutputStream()).thenReturn(outStreamMock);
            when(accRepoMock.findAll()).thenReturn(accountsList);
            when(accRepoMock.findByEmail(NEW_EMAIL)).thenReturn(null);
            when(encoderMock.encode(any(CharSequence.class))).thenReturn("encodedPassword");
            when(appSrvMock.getProperty(AppService.URL)).thenReturn("http://dummy.com");
            when(appSrvMock.getProperty(AppService.DEFAULTROLE)).thenReturn("ROLE_USER");
            when(mailerMock.sendMail(anyInt(), anyString(), anyString(), anyString(),
                    anyMapOf(String.class, Resource.class))).thenReturn(true);
            SignupForm form = fillForm();
            Account account = form.createAccount();
            when(accRepoMock.save(any(Account.class))).thenReturn(account);
            Errors errors = new BeanPropertyBindingResult(form, "form");
            signupCtr.signup(form, errors, raMock, requestMock);
            verify(accRepoMock, times(1)).save(any(Account.class));
            when(accRepoMock.findByUuid("confirmMe")).thenReturn(testAccount);
            signupCtr.confirm("confirmMe", raMock, requestMock);
            verify(accRepoMock, times(1)).save(testAccount);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void signUpFormErrorTest() {
        SignupForm form = fillForm();
        Errors errors = new BeanPropertyBindingResult(form, "form");
        errors.rejectValue("name", "error");
        signupCtr.signup(form, errors, raMock, requestMock);
        Assert.assertTrue(errors.hasErrors());
    }

    @Test
    public void signUpEmailFormErrorTest() {
        when(accRepoMock.findByEmail(NEW_EMAIL)).thenReturn(testAccount);
        SignupForm form = fillForm();
        Errors errors = new BeanPropertyBindingResult(form, "form");
        signupCtr.signup(form, errors, raMock, requestMock);
        Assert.assertTrue(errors.hasErrors());
    }

    @Test
    public void confirmErrorTest() throws ServletException {
        when(accRepoMock.findByUuid("confirmMe")).thenReturn(null);
        signupCtr.confirm("don't confirm me", raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));

    }

    @Test
    public void SignupPasswordNotMatchingTest() {
        SignupForm form = fillForm();
        Errors errors = new BeanPropertyBindingResult(form, "form");
        form.setConfirmPassword("wrong");
        signupCtr.signup(form, errors, raMock, requestMock);
        Assert.assertTrue(errors.hasErrors());
    }

    @Test
    public void resetSubmitNotMatchingTest() {
        PasswordResetForm form = fillPasswordForm();
        Errors errors = new BeanPropertyBindingResult(form, "form");
        form.setConfirmPassword("wrong");
        signupCtr.resetSubmit(form, errors, raMock);
        Assert.assertTrue(errors.hasErrors());
    }

    @Test
    public void resetSubmitNoAccountTest() {
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");

        PasswordResetForm form = fillPasswordForm();
        Errors errors = new BeanPropertyBindingResult(form, "form");
        signupCtr.resetSubmit(form, errors, raMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void resetSubmitTest() {
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
        when(accountSrv.findByUuid(anyString())).thenReturn(testAccount);
        when(encoderMock.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        PasswordResetForm form = fillPasswordForm();
        Errors errors = new BeanPropertyBindingResult(form, "form");
        signupCtr.resetSubmit(form, errors, raMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void resetPasswordNoAccountTest() {
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
        signupCtr.resetPassword(EMAIL, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.WARNING, new Object[]{}));
    }

    @Test
    public void resetPasswordTest() {
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
        when(accountSrv.findByEmail(EMAIL)).thenReturn(testAccount);
        when(requestMock.getScheme()).thenReturn("http");
        when(requestMock.getServerName()).thenReturn("testServer");
        when(requestMock.getServerPort()).thenReturn(8080);
        when(appSrvMock.getProperty(AppService.URL)).thenReturn("http://dummy.com");
        when(mailerMock.sendMail(anyInt(), anyString(), anyString(), anyString(),
                anyMapOf(String.class, Resource.class))).thenReturn(true);
        signupCtr.resetPassword(EMAIL, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.WARNING, new Object[]{}));
    }

    private SignupForm fillForm() {
        SignupForm form = signupCtr.signup();
        form.setName(ADAM);
        form.setSurname(DOE);
        form.setEmail(NEW_EMAIL);
        form.setPassword(PASSWORD);
        form.setConfirmPassword(PASSWORD);
        return form;
    }

    private PasswordResetForm fillPasswordForm() {
        PasswordResetForm form = new PasswordResetForm();
        form.setPassword(PASSWORD);
        form.setConfirmPassword(PASSWORD);
        UUID uuid = Generators.timeBasedGenerator().generate();
        form.setId(uuid.toString());
        return form;
    }

}
