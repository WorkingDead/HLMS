package utils.convertor;

import java.math.BigInteger;

public class BinaryConvertor {
	
	public static String toBinaryString(byte n) {
		BigInteger bi = new BigInteger(new byte[]{n});
		return bi.toString(2);
	}
	
	public static byte fromBinaryString(String str) {
		byte b = Byte.parseByte(str, 2);  
		return b;
	}

}
