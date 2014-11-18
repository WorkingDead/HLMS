package z_test_junit;

import java.util.Calendar;

import org.junit.Test;

import utils.convertor.DateConverter;

public class TestCalendar {

	
	@Test(timeout = 1000)
	public void testCalendar00() {

		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -3);
		c.getTime();

		System.out.println( "c = " + DateConverter.format(c, DateConverter.DB_DATETIME_FORMAT));
	}
	
}
