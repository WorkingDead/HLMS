package z_test_junit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestRegexPattern {

	@Test(timeout = 1000)
	public void test01() {
		
		String test = "300000000000000000000002";
		String pattern = "3[0]{22}1|3[0]{22}2";
		Pattern p = Pattern.compile(pattern);
		
		Matcher m = p.matcher( test );
		if( m.matches() ) {
			System.out.println("Match");
		}
		else
			System.out.println("Not Match");
		
	}
	
}
