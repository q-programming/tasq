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
	
	private static final PeriodFormatter IN_FORMATER = new PeriodFormatterBuilder().appendWeeks().appendSuffix("w").appendDays()
			.appendSuffix("d").appendHours().appendSuffix("h")
			.appendMinutes().appendSuffix("m").toFormatter();
	private static final PeriodFormatter OUT_FORMATER = new PeriodFormatterBuilder().appendWeeks().appendSuffix("w ").appendDays()
			.appendSuffix("d ").appendHours().appendSuffix("h ")
			.appendMinutes().appendSuffix("m").toFormatter();

	
	/** Returns new Period object fortmated from input ( for example 1w 2d 3h 30m )
	 * @param time
	 * @return
	 */
	public static Period inFormat(String time) {
		return IN_FORMATER.parsePeriod(time.replaceAll("\\s+",""));
	}
	
	/** Returns string from period formated in OUT_FORMATER form
	 * @param period
	 * @return
	 */
	public static String outFormat(Period period) {
		return period.toString(OUT_FORMATER);
	}
	
	
	public static Period plusPeriods(Period period1, Period period2){
		Period result  = period1.plus(period2);
		return result.normalizedStandard();
	}

	public static Period minusPeriods(Period period1, Period period2){
		Period result  = period1.minus(period2);
		return result.normalizedStandard();
	}

	
	

	
	

}
