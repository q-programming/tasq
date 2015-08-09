package com.qprogramming.tasq.support;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Hibernate;
import org.joda.time.DateTimeConstants;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.task.Task;

public class Utils {
	private static final String DATE_FORMAT = "dd-MM-yyyy";
	private static final String DATE_FORMAT_TIME = "dd-MM-yyyy HH:mm";
	public static final int MILLIS_PER_SECOND = DateTimeConstants.MILLIS_PER_SECOND;
	public static final int SECONDS_PER_HOUR = DateTimeConstants.SECONDS_PER_HOUR;
	private static final Logger LOG = LoggerFactory.getLogger(Utils.class);
	private static final String HTML_TAG_PATTERN = "<([A-Za-z][A-Za-z0-9]*)\\b[^>]*>(.*?)</\\1>";
	private static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;
	private static final String TD_TR = "</td></tr>";
	private static final String TD_TD = "</td><td>";
	private static final String TR_TD = "<tr><td>";
	private static String baseURL;
	private static HttpServletRequest request;
	
	public static final String TABLE = "<table class=\"worklog_table\">";
	public static final String TABLE_END = "</table>";
	

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
	 * Returns milis for timestamp of given uuid
	 * @param uuid
	 * @return
	 */
	public static long getTimeFromUUID(UUID uuid) {
		return (uuid.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000;
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
	/**
	 * Eliminates underscores and capitalizes first letter of given string
	 * @param s
	 * @return
	 */
	public static String capitalizeFirst(String s){
		s = s.replaceAll("_", " ");
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
	
	/**
	 * Returns date in simple format
	 * @param date
	 * @return
	 */
	public static Date convertStringToDate(String date) {
		Date result = null;
		try {
			result = new SimpleDateFormat(DATE_FORMAT).parse(date);
		} catch (ParseException e) {
			LOG.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * Returns date in simple format
	 * @param date
	 * @return
	 */
	public static Date convertStringToDateAndTime(String date) {
		Date result = null;
		try {
			result = new SimpleDateFormat(DATE_FORMAT_TIME).parse(date);
		} catch (ParseException e) {
			LOG.error(e.getMessage());
		}
		return result;
	}
	/**
	 * Returns strng with date and time 
	 * @param date
	 * @return
	 */
	public static String convertDateTimeToString(Date date) {
		String result = null;
		result = new SimpleDateFormat(DATE_FORMAT_TIME).format(date);
		return result;
	}
	
	
	public static String convertDateToString(Date date) {
		String result = null;
		result = new SimpleDateFormat(DATE_FORMAT).format(date);
		return result;
	}
	
	/**
	 * Helper method to get float value from Period ( hours )
	 * 
	 * @param value
	 * @return
	 */
	public static Float getFloatValue(Period value) {
		if (value == null) {
			value = new Period();
		}
		Float result = Float.valueOf((float) (PeriodHelper.toStandardDuration(
				value).getMillis() / MILLIS_PER_SECOND)
				/ SECONDS_PER_HOUR);
		return result;
	}

	/**
	 * Round to certain number of decimals
	 * 
	 * @param d
	 * @param decimalPlace
	 * @return
	 */
	public static float round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}

	/**
	 * Helper method to get Period value (hours ) from float value
	 * 
	 * @param timelogged
	 * @return
	 */
	public static Period getPeriodValue(Float timelogged) {
		Period value = new Period(0, 0, (int) (timelogged * Utils.SECONDS_PER_HOUR),
				0);
		return value;
	}
	
	public static  StringBuilder changedFromTo(String what, String from, String to) {
		StringBuilder message = new StringBuilder();
		if (what != null) {
			message.append("<tr><td colspan=2><b>");
			message.append(what);
			message.append(" :</b>");
			message.append(TD_TR);
		}
		message.append(TR_TD);
		message.append(from);
		message.append(TD_TD);
		message.append(to);
		message.append(TD_TR);
		return message;
	}
	
	public static String changedFromTo(String previous, String current) {
		StringBuilder message = new StringBuilder("<strike>");
		message.append(previous);
		message.append("</strike>");
		message.append(" &#10151; ");
		message.append(current);
		return message.toString();
	}

	





}