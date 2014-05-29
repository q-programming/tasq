/**
 * 
 */
package com.qprogramming.tasq.account;

import org.joda.time.Period;
import org.junit.BeforeClass;
import org.junit.Test;

import com.qprogramming.tasq.support.PeriodHelper;

/**
 * @author romanjak
 * @date 29 maj 2014
 */
public class SampleTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test() {
		String time = "1w 2d 3h 4m";
		String time2 = "40m";
		String time3 = "20m";
		Period p = PeriodHelper.inFormat(time);
		Period p2 = PeriodHelper.inFormat(time2);
		Period p3 = PeriodHelper.inFormat(time3);
		System.out.println(p2.toStandardDuration().getMillis());
		System.out.println(PeriodHelper.outFormat(p));
		p = PeriodHelper.minusPeriods(p, p2);
		System.out.println(PeriodHelper.outFormat(p));
		p3 = PeriodHelper.minusPeriods(p2, p3);
		System.out.println(PeriodHelper.outFormat(p3));
		System.out.println(p3.toStandardDuration().getMillis()*100/p2.toStandardDuration().getMillis() + "%");
		System.out.println(p3.toStandardDuration().getMillis());
		p2 = PeriodHelper.plusPeriods(p2, p2);
		System.out.println(PeriodHelper.outFormat(p2));
		
		
		
	}

}
