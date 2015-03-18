/**
 * 
 */
package com.qprogramming.tasq.support;

import javax.annotation.PostConstruct;

import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.field.FieldUtils;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author romanjak
 * @date 29 maj 2014
 */
@Component
public class PeriodHelper {

	@Value("${default.hoursPerDay:8}")
	private int hours;
	private static long MILLIS_PER_DAY;
	private static int DEFAULT_HOURS_PER_DAY = 8;

	private static final PeriodFormatter IN_FORMATER = new PeriodFormatterBuilder()
			.appendWeeks().appendSuffix("w").appendDays().appendSuffix("d")
			.appendHours().appendSuffix("h").appendMinutes().appendSuffix("m")
			.toFormatter();
	private static final PeriodFormatter OUT_FORMATER = new PeriodFormatterBuilder()
			.appendWeeks().appendSuffix("w ").appendDays().appendSuffix("d ")
			.appendHours().appendSuffix("h ").appendMinutes().appendSuffix("m")
			.toFormatter();

	/**
	 * Set default hours and millis per day , if hours were somehow not present
	 * in properties , use 8h as default
	 */
	@PostConstruct
	public void init() {
		if (hours == 0) {
			hours = 8;
		}
		DEFAULT_HOURS_PER_DAY = hours;
		MILLIS_PER_DAY = DateTimeConstants.MILLIS_PER_HOUR * hours;
	}

	/**
	 * Returns new Period object fortmated from input ( for example 1w 2d 3h 30m
	 * )
	 * 
	 * @param time
	 * @return
	 */
	public static Period inFormat(String time) {
		if (time == null || "".equals(time)) {
			return new Period();
		}
		Period period = IN_FORMATER.parsePeriod(time.replaceAll("\\s+", ""));
		return normalizedStandard(period);
	}

	/**
	 * Returns string from period formated in OUT_FORMATER form
	 * 
	 * @param period
	 * @return
	 */
	public static String outFormat(Period period) {
		if (period == null) {
			period = new Period();
		}
		return period.toString(OUT_FORMATER);
	}

	/**
	 * Adds to periods ( plus standardize based on normals ) TODO change to 8h
	 * based work day ?
	 * 
	 * @param period1
	 * @param period2
	 * @return
	 */
	public static Period plusPeriods(Period period1, Period period2) {
		Period result = period1.plus(period2);
		return normalizedStandard(result);
	}

	/**
	 * Subtracts two periods ( plus standardize based on normals )
	 * 
	 * @param period1
	 * @param period2
	 * @return
	 */
	public static Period minusPeriods(Period period1, Period period2) {
		Period result = period1.minus(period2);
		return normalizedStandard(result);
	}

	// -----------------------------------------------------------------------
	/**
	 * Converts period to a duration assuming a 7 day week, application default
	 * hours day, 60 minute hour and 60 second minute.
	 * <p>
	 * This method allows you to convert from a period to a duration. However to
	 * achieve this it makes the assumption that all weeks are 7 days, all days
	 * are application default hours, all hours are 60 minutes and all minutes
	 * are 60 seconds. This is not true when daylight savings time is
	 * considered, and may also not be true for some unusual chronologies.
	 * However, it is included as it is a useful operation for many applications
	 * and business rules.
	 * <p>
	 * 
	 * @return a duration equivalent to this period
	 */
	public static Duration toStandardDuration(Period period) {
		long millis = getPeriodMillis(period);
		return new Duration(millis);
	}

	public static Period normalizedStandard(Period period) {
		if (MILLIS_PER_DAY == 0) {
			MILLIS_PER_DAY = DateTimeConstants.MILLIS_PER_HOUR
					* DEFAULT_HOURS_PER_DAY;
		}
		long millis = getPeriodMillis(period);
		Period result = new Period();
		int weeks = (int) (millis / DateTimeConstants.MILLIS_PER_WEEK);
		if (weeks > 0) {
			millis -= (DateTimeConstants.MILLIS_PER_WEEK * weeks);
			result = result.withWeeks(weeks);
		}
		int days = (int) (millis / MILLIS_PER_DAY);
		if (days > 0) {
			millis -= (MILLIS_PER_DAY * days);
			result = result.withDays(days);
		}
		int hours = (int) (millis / DateTimeConstants.MILLIS_PER_HOUR);
		if (hours > 0) {
			millis -= (DateTimeConstants.MILLIS_PER_HOUR * hours);
			result = result.withHours(hours);
		}
		int minutes = (int) (millis / DateTimeConstants.MILLIS_PER_MINUTE);
		result = result.withMinutes(minutes);

		int years = period.getYears();
		int months = period.getMonths();
		if (years != 0 || months != 0) {
			years = FieldUtils.safeAdd(years, months / 12);
			months = months % 12;
			if (years != 0) {
				result = result.withYears(years);
			}
			if (months != 0) {
				result = result.withMonths(months);
			}
		}
		return result;
	}

	/**
	 * Returns milliseconds from period. Taking in account application property
	 * default.hoursPerDay and multiplying it by
	 * DateTimeConstants.MILLIS_PER_HOUR
	 * 
	 * 
	 * @param period
	 * @return
	 */
	private static long getPeriodMillis(Period period) {
		long millis = period.getMillis();
		millis += (((long) period.getSeconds()) * ((long) DateTimeConstants.MILLIS_PER_SECOND));
		millis += (((long) period.getMinutes()) * ((long) DateTimeConstants.MILLIS_PER_MINUTE));
		millis += (((long) period.getHours()) * ((long) DateTimeConstants.MILLIS_PER_HOUR));
		millis += (((long) period.getDays()) * ((long) MILLIS_PER_DAY));
		millis += (((long) period.getWeeks()) * ((long) DateTimeConstants.MILLIS_PER_WEEK));
		return millis;
	}

}
