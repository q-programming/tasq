package com.qprogramming.tasq.support;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PeriodHelperTest {

	@Test
	public void createResultDatatest() {
		ResultData result = new ResultData(ResultData.OK, "message");
		Assert.assertNotNull(result);
	}

}
