package z_test_junit;

import org.junit.Test;

public class TestTryFinally {

	@Test(timeout = 1000)
	public void test00() {
		
		try {
			System.out.println(foo(new String[]{"41"}));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test(timeout = 1000)
	public void test01() {
		
		try {
			System.out.println(foo(null));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static int foo(String[] args) {
		try {
			int n = Integer.parseInt(args[0]);
			return n;
		}
		finally {
			System.out.println("here is final");
			//return 42;
		}
	}

}
