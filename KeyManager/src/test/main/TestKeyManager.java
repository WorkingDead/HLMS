package test.main;

import security.KeyManager;

public class TestKeyManager {
	
	private static String file = "C:\\Sedna\\key_new";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		KeyManager km = new KeyManager(file);
		System.out.println(km.isValid());
		System.out.println(km.isExpiry());
		for(String key : km.getOptionalField().keySet())
		{
			System.out.println(key + " : " + km.getOptionalField().get(key));
		}
	}

}
