package z_test_junit;

import org.junit.Test;

import utils.convertor.StringConvertor;

public class TestInsertZero {

	@Test(timeout = 1000)
	public void testInsertZero00() {
		
		String test = "0";
		
		System.out.println("testInsertZero00: After inserting = " + StringConvertor.insertZero(test, 5) );
	}
	
	@Test(timeout = 1000)
	public void testInsertZero01() {
		
		String test = "001";
		
		System.out.println("testInsertZero01: After inserting = " + StringConvertor.insertZero(test, 5) );
	}
	
	@Test(timeout = 1000)
	public void testInsertZero02() {
		
		String test = "011";
		
		System.out.println("testInsertZero02: After inserting = " + StringConvertor.insertZero(test, 5) );
	}
	
	@Test(timeout = 1000)
	public void testInsertZero03() {
		
		String test = "111";
		
		System.out.println("testInsertZero03: After inserting = " + StringConvertor.insertZero(test, 5) );
	}
	
	@Test(timeout = 1000)
	public void testInsertZero04() {
		
		String test = "1111";
		
		System.out.println("testInsertZero04: After inserting = " + StringConvertor.insertZero(test, 5) );
	}
	
	@Test(timeout = 1000)
	public void testInsertZero05() {
		
		String test = "11111";
		
		System.out.println("testInsertZero05: After inserting = " + StringConvertor.insertZero(test, 5) );
	}
}
