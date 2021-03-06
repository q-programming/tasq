package com.qprogramming.tasq.support;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.task.Task;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Period;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Utils {
    public static final int MILLIS_PER_SECOND = DateTimeConstants.MILLIS_PER_SECOND;
    public static final int SECONDS_PER_HOUR = DateTimeConstants.SECONDS_PER_HOUR;
    public static final String TABLE = "<table class=\"table worklog_table\">";
    public static final String TABLE_END = "</table>";
    public static final String REDIRECT_TASK = "redirect:/task/";
    public static final String REDIRECT = "redirect:";
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String DATE_FORMAT_TIME = "dd-MM-yyyy HH:mm";
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);
    private static final String HTML_TAG_PATTERN = "<(\\/)?([A-Za-z][A-Za-z0-9]*)\\b[^>]*>";
    private static final String ESTIMATES_PATTENR = "\\s?(\\d*d)?\\s?(\\d*h)?\\s?(\\d*m)?";
    private static final int[] VALID_STORYPOINTS_VALUES = new int[]{0, 1, 2, 3, 5, 8, 13, 20, 40, 100};
    private static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;
    private static final String TD_TR = "</td></tr>";
    private static final String TD_TD = "</td><td>";
    private static final String TR_TD = "<tr><td>";
    private static String baseURL;
    private static HttpServletRequest request;
    @Value("${default.locale}")
    private String defaultLang;

    public static Account getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return (Account) authentication.getPrincipal();
        }
        return null;
    }

    public static void setHttpRequest(HttpServletRequest httpServRequest) {
        request = httpServRequest;
    }

    /**
     * Returns milis for timestamp of given uuid
     *
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
        return matcher.matches() || matcher.find();
    }

    public static boolean correctEstimate(String estimate) {
        Pattern pattern = Pattern.compile(ESTIMATES_PATTENR);
        Matcher matcher = pattern.matcher(estimate);
        return matcher.matches();
    }

    public static String getBaseURL() {
        // TODO null port and server scheme
        if (baseURL == null) {
            int port = request.getServerPort();
            if (port == 80) {
                baseURL = String.format("%s://%s/tasq", request.getScheme(), request.getServerName());
            } else {
                baseURL = String.format("%s://%s:%d/tasq", request.getScheme(), request.getServerName(), port);
            }
        }
        return baseURL;
    }

    public static Locale getCurrentLocale() {
        Account currentAccount = getCurrentAccount();
        if (currentAccount == null) {
            return getDefaultLocale();
        }
        return new Locale(currentAccount.getLanguage());
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
     *
     * @param s
     * @return
     */
    public static String capitalizeFirst(String s) {
        s = s.replaceAll("_", " ");
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /**
     * Returns date in simple format
     *
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

    public static boolean endsWithIgnoreCase(String input, String substring) {
        return StringUtils.endsWithIgnoreCase(input, substring);

    }

    /**
     * Returns date in simple format
     *
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
     *
     * @param date
     * @return
     */
    public static String convertDateTimeToString(DateTime date) {
        return new SimpleDateFormat(DATE_FORMAT_TIME).format(date);
    }

    /**
     * Returns strng with date and time
     *
     * @param date
     * @return
     */
    public static String convertDateTimeToString(Date date) {
        return new SimpleDateFormat(DATE_FORMAT_TIME).format(date);
    }

    public static String convertDateToString(Date date) {
        return new SimpleDateFormat(DATE_FORMAT).format(date);
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
        return (float) (PeriodHelper.toStandardDuration(value).getMillis() / MILLIS_PER_SECOND) / SECONDS_PER_HOUR;
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
        return new Period(0, 0, (int) (timelogged * Utils.SECONDS_PER_HOUR), 0);
    }

    public static StringBuilder changedFromTo(String what, String from, String to) {
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

    public static String stripHtml(String string) {
        return Jsoup.parse(string).text();
    }

    public static Integer round(double number) {
        return (int) Math.round(number);
    }

    /**
     * Coppy file from source path to destination file Used mostly for getting
     * files from resources etc. and coping to some destFile
     *
     * @param sc
     * @param sourcePath
     * @param destFile
     */
    public static void copyFile(ServletContext sc, String sourcePath, File destFile) {
        try {
            InputStream in = new FileInputStream(sc.getRealPath(sourcePath));
            OutputStream out = new FileOutputStream(destFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    /**
     * Check if time ( logged or estimated ) is number and add default time uni if missing
     *
     * @param time - time to be checked
     * @return
     */
    public static String matchTimeFormat(String time) {
        if (StringUtils.isNotBlank(time) && time.matches("[0-9]+")) {
            time += "h";
        }
        return time;
    }

    /**
     * If user is logged in he will be forecebly logged out
     *
     * @param request HttpServletRequest
     */
    public static void forceLogout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        }
    }

    public static boolean validStoryPoint(final Integer sp) {
        return IntStream.of(VALID_STORYPOINTS_VALUES).anyMatch(x -> x == sp);
    }

}