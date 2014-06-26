package com.qprogramming.tasq.support;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.task.Task;

public class Utils {

	private static final String HTML_TAG_PATTERN = "<([A-Za-z][A-Za-z0-9]*)\\b[^>]*>(.*?)</\\1>";
	private static String baseURL;
	private static HttpServletRequest request;

	@Value("${default.locale}")
	private String defaultLang;

	public static Account getCurrentAccount() {
		return (Account) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
	}

	public static void setHttpRequest(HttpServletRequest httpServRequest) {
		request = httpServRequest;
	}

	/**
	 * Returns true if contents have at least one html tag
	 * 
	 * @return
	 */
	public static boolean containsHTMLTags(String contents) {
		Pattern pattern = Pattern.compile(HTML_TAG_PATTERN);
		Matcher matcher = pattern.matcher(contents);
		return matcher.matches();

	}

	public static String getBaseURL() {
		// TODO null port and server scheme
		if (baseURL == null) {
			baseURL = String.format("%s://%s:%d/tasq", request.getScheme(),
					request.getServerName(), request.getServerPort());
		}
		return baseURL;
	}

	public static Locale getCurrentLocale() {
		return new Locale(getCurrentAccount().getLanguage());
	}

	/**
	 * Use this method only if locale was previously set!!
	 * 
	 * @return
	 */
	public static Locale getDefaultLocale() {
		return LocaleContextHolder.getLocale();
	}

	public static boolean contains(Collection<?> coll, Object o) {
		return coll.contains(o);
	}

	/**
	 * Initialize fetching of worklogs for each task in taskList
	 * 
	 * @param taskList
	 */
	public static void initializeWorkLogs(List<Task> taskList) {
		for (Task task : taskList) {
			Hibernate.initialize(task.getRawWorkLog());
		}
	}

}