package com.qprogramming.tasq.support;

import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

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

import com.qprogramming.tasq.MockSecurityContext;
import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;

@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {

	private Account testAccount = new Account("user@test.com", "", Roles.ROLE_USER);

	@Mock
	private MockSecurityContext securityMock;
	
	@Mock
	private Authentication authMock;

	@Mock
	private AccountService accountServiceMock;
	
	@Mock
	private HttpServletRequest requestMock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUp(){
		when(securityMock.getAuthentication()).thenReturn(authMock);
		when(authMock.getPrincipal()).thenReturn(testAccount);
		SecurityContextHolder.setContext(securityMock);
	}
	
	@Test
	public void getCurrentUserAndLocaleTest() {
		Assert.assertEquals(testAccount,Utils.getCurrentAccount());
	}
	@Test
	public void getLocaleTest() {
		Assert.assertEquals(new Locale("en"),Utils.getCurrentLocale());
	}
	@Test
	public void getDefaultLocaleTest() {
		Assert.assertNotNull(Utils.getDefaultLocale().toLanguageTag());
	}

	@Test
	public void containsHTMLTagsTest() {
		String test1 = "<html>test</html>";
		String test2 = "html test html";
		Assert.assertFalse(Utils.containsHTMLTags(test2));
		Assert.assertTrue(Utils.containsHTMLTags(test1));
	}

	
	@Test
	public void getBaseURLTest() {
		when(requestMock.getScheme()).thenReturn("http");
		when(requestMock.getServerName()).thenReturn("testServer");
		when(requestMock.getServerPort()).thenReturn(8080);
		Utils.setHttpRequest(requestMock);
		String result = "http://testServer:8080/tasq";
		Assert.assertEquals(result,Utils.getBaseURL());
	}


	@Test
	public void capitalizeFirstTest() {
		String test = "test_string";
		String result = "Test string";		
		Assert.assertEquals(result,Utils.capitalizeFirst(test));
	}
	
	@Test
	public void convertDateToStringAndStringToDateTest(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2014);
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		Date testDate = Utils.convertStringToDate("01-12-2014");
		Assert.assertEquals("01-12-2014",Utils.convertDateToString(testDate));
	}

}
