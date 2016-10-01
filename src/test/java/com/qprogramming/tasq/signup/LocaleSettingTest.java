package com.qprogramming.tasq.signup;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountRepository;
import com.qprogramming.tasq.signin.LocaleSettingAuthenticationSuccessHandler;
import com.qprogramming.tasq.test.MockSecurityContext;
import com.qprogramming.tasq.test.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocaleSettingTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @InjectMocks
    private LocaleSettingAuthenticationSuccessHandler localeSettter;
    @Mock
    private AccountRepository accRepoMock;
    @Mock
    private MockSecurityContext securityMock;
    @Mock
    private Authentication authMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private SessionLocaleResolver localeResolver;
    @Mock
    private HttpSession sessionMock;
    @Mock
    private SavedRequest savedRequestMock;
    private Account testAccount = TestUtils.createAccount();

    @Before
    public void setUp() {
        when(securityMock.getAuthentication()).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(testAccount);
        SecurityContextHolder.setContext(securityMock);
    }

    @Test
    public void testLoggin() {
        try {
            when(requestMock.getSession(false)).thenReturn(sessionMock);
            when(sessionMock.getAttribute(anyString())).thenReturn(savedRequestMock);
            when(savedRequestMock.getRedirectUrl()).thenReturn("projects/show");
            localeSettter.onAuthenticationSuccess(requestMock, responseMock, authMock);

        } catch (ServletException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}
