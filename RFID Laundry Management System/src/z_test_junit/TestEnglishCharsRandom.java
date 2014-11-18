package z_test_junit;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

public class TestEnglishCharsRandom {

	String allCharacters[] = {
			"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"
			};
	
	Random rn = new Random();
	int minimum = 0;
	int maximum = 25;
	
	Map<String, String> map = new HashMap<String, String>();
	
	@Test(timeout = 100000)
	public void test00() {
		
		int countLoop = 0;
		int maxLoop = 3;
		while ( true ) {

			if ( countLoop > maxLoop ) {
				System.out.println("Duplicated " + maxLoop + " times and map size = " + map.size());
				break;
			}

			String string = allCharacters[minimum + rn.nextInt(maximum - minimum + 1)]
					+ allCharacters[minimum + rn.nextInt(maximum - minimum + 1)]
					+ allCharacters[minimum + rn.nextInt(maximum - minimum + 1)]
					+ allCharacters[minimum + rn.nextInt(maximum - minimum + 1)]
					+ allCharacters[minimum + rn.nextInt(maximum - minimum + 1)];
			
			
			if ( map.containsKey( string ) == false ) {
				map.put(string, string);
				countLoop = 0;
			}
			else {
				countLoop = countLoop + 1;
				continue;
			}
		}
	}
}

//TEST RESULT
//maxLoop = 3
//===============
//661669
//495267
//411354
//542804
//750889
//691662
//580822
//577064
//524793
//774377
//===============
//
//maxLoop = 5
//===============
//1264053
//1509057
//1441612
//1547372
//1368416
//1204202
//903332
//1135814
//1518766
//1233061
//===============
//
//maxLoop = 10
//===============
//4014657
//3892339
//3889267
//3933038
//4173880
//4229799
//3951456
//3921374
//3659883
//3757332
//===============