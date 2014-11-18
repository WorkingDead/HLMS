package test.main;

import org.jasypt.util.text.BasicTextEncryptor;

import utils.convertor.BinaryConvertor;
import utils.convertor.StringConvertor;

public class BitAndOrTest {

	private static final String licStr = "SEDNA-DATAPLEX-V2";
	private static final String wrongLicStr = "SEDNA-DATAPLEX-V3";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		byte Z = 0;
		
		byte A = (byte) Math.pow(2, 0);
		byte B = (byte) Math.pow(2, 1);
		byte C = (byte) Math.pow(2, 2);
		
//		Z|=A;
		Z|=B;
		Z|=C;
		
		System.out.println("Z: " + (Z));
		
		String binary = StringConvertor.addBefore(BinaryConvertor.toBinaryString(Z), '0', 8);
		System.out.println("binary: " + binary);
		
		System.out.println((Z & A) != 0);
		System.out.println((Z & B) != 0);
		System.out.println((Z & C) != 0);
		
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(licStr);
		String encrypted = textEncryptor.encrypt(binary);
		System.out.println(encrypted);
		String decrypted = textEncryptor.decrypt(encrypted);
		
		System.out.println("decrypted: " + decrypted);
		
		byte Y = BinaryConvertor.fromBinaryString(decrypted);
		
		System.out.println(Y);
		
		System.out.println((Z & A) != 0);
		System.out.println((Z & B) != 0);
		System.out.println((Z & C) != 0);
		
		System.out.println("Using wrong private key");
		BasicTextEncryptor textEncryptor2 = new BasicTextEncryptor();
		textEncryptor2.setPassword(wrongLicStr);
		String wrongDecrypted = textEncryptor2.decrypt(encrypted);
		
		System.out.println("wrongDecrypted: " + wrongDecrypted);
	}
}
