package security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.BasicTextEncryptor;

import com.ibm.icu.text.SimpleDateFormat;

import security.bo.Str;
import security.iface.KeyValidator;

/*
 * 
 * This is a new key generator, version: 2012.1
 * Author by Kan
 * 
 */

public class KeyManager {
	
	//optional fields
	public static final String OPTIONAL_NOOFLICENSE = "noOfLicense";
	
	//date format
	public static final String DATETIME_FORMAT = "yyyyMMdd";
	
	//TODO
	//this is the version of keygen, use for validate the key version
	//yyyy.reversion
	private static final String version = "2012.2";
	
	//license private key
	private static final String licStr = Str.getString();
	
	private boolean isValidKey = false;
	private String keyType = "00000000";
	private LinkedList<String> keyList = new LinkedList<String>();
	private Map<String, String> optionalField = new ConcurrentHashMap<String, String>();
	private Calendar expiryDate;
	private boolean canExpiry = true;
	
	
	public KeyManager(String path)
	{
		try{
			File file = new File(path);
			if(!file.exists())
			{
				throw new Exception("Key not found");
			}
			if(!file.isFile())
			{
				throw new Exception("file path is dir");
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
			          new FileInputStream(file)));
			
			BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
			textEncryptor.setPassword(licStr);
			
			String data = br.readLine();
			String rawKey = textEncryptor.decrypt(data);
			
			keyType = rawKey.substring(0, 8);
			
			int ksize = ValidatorBeans.countOfValidator(keyType);
			
			int index = 8;
			for(int i=0;i<ksize;i++)
			{
				keyList.add(rawKey.substring(index, index+50).trim());
				index += 50;
			}
			
//			int year = Integer.parseInt(rawKey.substring(index, index+4));
//			index += 4;
//			int month = Integer.parseInt(rawKey.substring(index, index+2));
//			index += 2;
//			int day = Integer.parseInt(rawKey.substring(index, index+2));
//			index += 2;
			
			String expiryDateStr = rawKey.substring(index, index+8);
			index += 8;
			if(expiryDateStr.equals("00000000"))
			{
				canExpiry = false;
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat(KeyManager.DATETIME_FORMAT);
			expiryDate = Calendar.getInstance();
			Date d = sdf.parse(expiryDateStr);
			expiryDate.setTime(d);
			
			String optionalFieldStr = rawKey.substring(index);
			this.optionalField = getOptionalFields(optionalFieldStr);
			
			this.isValidKey = true;
		}
		catch (EncryptionOperationNotPossibleException e)
		{
			System.out.println("Wrong key version");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Map<String, String> getOptionalField() {
		return optionalField;
	}

	public void setOptionalField(Map<String, String> optionalField) {
		this.optionalField = optionalField;
	}
	
	private Map<String, String> getOptionalFields(String o)
	{
		Map<String, String> result = new ConcurrentHashMap<String, String>();
		try{
			String[] splits = o.split("\n");
			for(String s : splits)
			{
				String[] item = s.split("=");
				result.put(item[0], item[1]);
			}
		}
		catch(Exception e)
		{}
		
		return result;
	}
	
	public boolean isValid()
	{
		if(this.isValidKey)
		{
			boolean valid = true;
			
			List<KeyValidator> vs = ValidatorBeans.getValidators(keyType);
			for(int i=0;i<vs.size();i++)
			{
				KeyValidator v = vs.get(i);
				String key = keyList.get(i);
				if(!v.isValid(key))
				{
					valid = false;
				}
			}
			
			return valid;
		
		}
		return false;
	}
	
	public boolean isExpiry()
	{
		if(!canExpiry)
		{
			return false;
		}
		Calendar now = Calendar.getInstance();
		if(expiryDate.before(now))
		{
			return true;
		}
		return false;
	}
}
