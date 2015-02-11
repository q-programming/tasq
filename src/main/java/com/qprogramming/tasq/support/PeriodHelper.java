/**
 * 
 */
package com.qprogramming.tasq.support;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * @author romanjak
 * @date 29 maj 2014
 */
public class PeriodHelper {

	private static final PeriodFormatter IN_FORMATER = new PeriodFormatterBuilder()
			.appendWeeks().appendSuffix("w").appendDays().appendSuffix("d")
			.appendHours().appendSuffix("h").appendMinutes().appendSuffix("m")
			.toFormatter();
	private static final PeriodFormatter OUT_FORMATER = new PeriodFormatterBuilder()
			.appendWeeks().appendSuffix("w ").appendDays().appendSuffix("d ")
			.appendHours().appendSuffix("h ").appendMinutes().appendSuffix("m")
			.toFormatter();

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
		return IN_FORMATER.parsePeriod(time.replaceAll("\\s+", ""))
				.normalizedStandard();
	}

	/**
	 * Returns string from period formated in OUT_FORMATER form
	 * 
	 * @param period
	 * @return
	 */
	public static String outFormat(Period period) {
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
		return result.normalizedStandard();
	}

	/**
	 * Subtracts two periods ( plus standardize based on normals ) TODO change
	 * to 8h based work day ?
	 * 
	 * @param period1
	 * @param period2
	 * @return
	 */
	public static Period minusPeriods(Period period1, Period period2) {
		Period result = period1.minus(period2);
		return result.normalizedStandard();
	}

}
