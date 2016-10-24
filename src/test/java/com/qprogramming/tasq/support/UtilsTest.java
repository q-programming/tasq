package com.qprogramming.tasq.support;

import com.fasterxml.uuid.Generators;
import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.test.MockSecurityContext;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Account testAccount = new Account("user@test.com", "", "user", Roles.ROLE_ADMIN);
    @Mock
    private MockSecurityContext securityMock;
    @Mock
    private Authentication authMock;
    @Mock
    private AccountService accountServiceMock;
    @Mock
    private HttpServletRequest requestMock;

    @Before
    public void setUp() {
        when(securityMock.getAuthentication()).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(testAccount);
        SecurityContextHolder.setContext(securityMock);
    }

    @Test
    public void getCurrentUserAndLocaleTest() {
        Assert.assertEquals(testAccount, Utils.getCurrentAccount());
    }

    @Test
    public void getLocaleTest() {
        Assert.assertEquals(new Locale("en"), Utils.getCurrentLocale());
    }

    @Test
    public void getDefaultLocaleTest() {
        Assert.assertNotNull(Utils.getDefaultLocale().toLanguageTag());
    }

    @Test
    public void containsHTMLTagsTest() {
        String test1 = "<html>test</html>";
        String test2 = "html test html";
        String test3 = "<b>";
        String test4 = "<b>test</b>";
        String test5 = "<script>test</script>";
        String test6 = "< script >test</ script >";
        String test7 = "Some test < script  and >";
        Assert.assertTrue(Utils.containsHTMLTags(test1));
        Assert.assertFalse(Utils.containsHTMLTags(test2));
        Assert.assertTrue(Utils.containsHTMLTags(test3));
        Assert.assertTrue(Utils.containsHTMLTags(test4));
        Assert.assertTrue(Utils.containsHTMLTags(test5));
        Assert.assertFalse(Utils.containsHTMLTags(test6));
        Assert.assertFalse(Utils.containsHTMLTags(test7));
    }

    @Test
    public void getBaseURLTest() {
        when(requestMock.getScheme()).thenReturn("http");
        when(requestMock.getServerName()).thenReturn("testServer");
        when(requestMock.getServerPort()).thenReturn(8080);
        Utils.setHttpRequest(requestMock);
        Assert.assertNotNull(Utils.getBaseURL());
    }

    @Test
    public void capitalizeFirstTest() {
        String test = "test_string";
        String result = "Test string";
        Assert.assertEquals(result, Utils.capitalizeFirst(test));
    }

    @Test
    public void convertDateToStringAndStringToDateTest() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2014);
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        Date testDate = Utils.convertStringToDate("01-12-2014");
        Assert.assertEquals("01-12-2014", Utils.convertDateToString(testDate));
    }

    @Test
    public void containsTest() {
        List<Account> test = new LinkedList<Account>();
        test.add(testAccount);
        Assert.assertTrue(Utils.contains(test, testAccount));
    }

    @Test
    public void rolesTest() {
        Assert.assertTrue(Roles.isUser());
        Assert.assertTrue(Roles.isPowerUser());
        Assert.assertTrue(Roles.isAdmin());
        Assert.assertEquals("role.admin", Roles.ROLE_ADMIN.getCode());
    }

    @Test
    public void uuidTest() {
        UUID uuid = Generators.timeBasedGenerator().generate();
        long timestamp = uuid.timestamp();
        DateTime date = new DateTime(Utils.getTimeFromUUID(uuid));
        DateTime beforedate = new DateTime().minusDays(1);
        Assert.assertTrue(beforedate.isBefore(date));
        Assert.assertNotNull(timestamp);
    }

}
