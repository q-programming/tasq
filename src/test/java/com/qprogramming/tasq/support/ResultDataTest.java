package com.qprogramming.tasq.support;

import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.qprogramming.tasq.support.PeriodHelper;

@RunWith(MockitoJUnitRunner.class)
public class ResultDataTest {

	@Test
	public void getInFormatTest() {
		String input = "2d 2h 2m";
		Period expected = new Period(0, 0, 0, 2, 2, 2, 0, 0);
		Period result = PeriodHelper.inFormat(input);
		Period result2 = PeriodHelper.inFormat(null);
		Assert.assertEquals(expected, result);
		Assert.assertEquals(new Period(), result2);
	}

	@Test
	public void getOutFormatTest() {
		Period input = new Period(0, 0, 0, 2, 2, 2, 0, 0);
		String result = PeriodHelper.outFormat(input);
		String expected = "2d 2h 2m";
		Assert.assertEquals(expected, result);

	}

	@Test
	public void plusPeriodTest() {
		Period input = new Period(0, 0, 0, 2, 2, 2, 0, 0);
		Period input2 = new Period(0, 0, 0, 2, 2, 2, 0, 0);
		Period expected = new Period(0, 0, 0, 4, 4, 4, 0, 0);
		Period result = PeriodHelper.plusPeriods(input, input2);
		Assert.assertEquals(expected, result);
	}

	@Test
	public void minusPeriodTest() {
		Period input = new Period(0, 0, 0, 4, 4, 4, 0, 0);
		Period input2 = new Period(0, 0, 0, 2, 2, 2, 0, 0);
		Period expected = new Period(0, 0, 0, 2, 2, 2, 0, 0);
		Period result = PeriodHelper.minusPeriods(input, input2);
		Assert.assertEquals(expected, result);
	}

}
