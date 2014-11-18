package security;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import security.iface.KeyValidator;
import security.impl.HDDSerialKeyVaildator;
import security.impl.MacAddressKeyVaildator;
import utils.convertor.BinaryConvertor;

public class ValidatorBeans {

	public static Map<Byte, KeyValidator> validatorMap = new LinkedHashMap<Byte, KeyValidator>();
	
	static{
		validatorMap.put(indexToByte(0), new MacAddressKeyVaildator());
		validatorMap.put(indexToByte(1), new HDDSerialKeyVaildator());
	}
	
	public static byte indexToByte(int i)
	{
		return (byte) Math.pow(2, i);
	}
	
	public static List<KeyValidator> getValidators(String binary)
	{
		List<KeyValidator> list = new ArrayList<KeyValidator>();
		byte key = BinaryConvertor.fromBinaryString(binary);
		
		for(Byte b : validatorMap.keySet())
		{
			if((key & b)>0)
			{
				list.add(validatorMap.get(b));
			}
		}
		return list;
	}
	
	public static int countOfValidator(String binary)
	{
		int i = 0;
		byte key = BinaryConvertor.fromBinaryString(binary);
		
		for(Byte b : validatorMap.keySet())
		{
			if((key & b)>0)
			{
				i += 1;
			}
		}
		return i;
	}
}
