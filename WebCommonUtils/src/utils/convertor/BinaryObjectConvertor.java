package utils.convertor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class BinaryObjectConvertor {
	
	private static Object lockObject = new Object();

	public static byte[] getBytes(Object o) throws IOException
	{
		//Java JDK Bug
		//java.lang.ClassCastException: java.util.SimpleTimeZone cannot be cast to sun.util.calendar.ZoneInfo
		//Multithreaded deserialization of Calendar leads to ClassCastException 
		//http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7017458
		//http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=2220373
		//http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=8001312
		//The following statement should be able to fix this bug.
		//This is because writeObject() would not work concurrently anymore.
		synchronized (lockObject) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(bos);   
			out.writeObject(o);
			byte[] yourBytes = bos.toByteArray();
			out.close();
			bos.close();
			return yourBytes;
		}
	}
	
	public static Object getObject(byte[] yourBytes) throws ClassNotFoundException, IOException{
		ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
		ObjectInput in = new ObjectInputStream(bis);
		Object o = in.readObject(); 
		bis.close();
		in.close();
		return o;
	}

}
