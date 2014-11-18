package z_test_junit;

import module.dao.master.Stf;

import org.junit.Test;

public class TestStf {

	@Test(timeout = 1000)
	public void testStf01() {
		
		System.out.println("indexof 1 = " + Stf.StfOpe.FAILA.toString().indexOf( Stf.StfOpe.FAIL.toString() ));
		System.out.println("indexof 2 = " + "FAILA".indexOf( Stf.StfOpe.FAIL.toString() ));
		System.out.println("indexof 3 = " + "FAIL".indexOf( Stf.StfOpe.FAIL.toString() ));
		
		System.out.println("Stf.StfOpe.INSERT = " + Stf.StfOpe.INSERT);
		System.out.println("Stf.StfOpe.INSERT = " + Stf.StfOpe.INSERT.toString());

		Exception exception = new Exception("More than one staff found when STF = ");
		System.out.println("exception = " + exception.getMessage());
		
		
		System.out.println("valueof = " + Stf.StfSts.valueOf("A") );
		
		try {
			System.out.println("valueof = " + Stf.StfSts.valueOf("B") );
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
