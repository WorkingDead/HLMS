package utils.math;

import java.util.Random;

public class RandomBoolean {

	private static RandomBoolean instance = null;
	
	public static RandomBoolean getInstance() {
		
		if ( instance == null )
			instance = new RandomBoolean();
			
		return instance;
	}
	
	private Random random; 
	
	public RandomBoolean() {
		random = new Random();
	}
	
    public boolean getRandomBoolean() {         
    	return random.nextBoolean();     
    } 
}
