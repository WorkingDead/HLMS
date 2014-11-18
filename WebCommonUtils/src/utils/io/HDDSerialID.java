package utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class HDDSerialID {
	
	private static List<String> hddserials;
	
	static
	{
		hddserials = HDDSerialID.getHDDSerialIdList();
	}
	
	private static List<String> getHDDSerialIdList()
	{
		String os = System.getProperty("os.name");

		if(os.startsWith("Windows"))
		{
			//TODO windows version
			System.out.println("Not implement at windows.");
		}
		else if(os.startsWith("Linux"))
		{
			try {
				if(HDDSerialID.runProcessAndReturnResult("whoami").equals("root"))
				{
					String[] cmd = {
							"/bin/sh",
							"-c",
							"fdisk -l | grep \"Disk /dev\""
							};
					StringTokenizer tokenizer = new StringTokenizer(HDDSerialID.runProcessAndReturnResult(cmd), "\r\n"); 
					List<String> serials = new ArrayList<String>();
					while(tokenizer.hasMoreTokens())
					{
						String drive = tokenizer.nextToken();
						drive = drive.substring(10, 13);
						cmd = new String[] {
								"/bin/sh",
								"-c",
								"hdparm -i /dev/"+drive+" | grep 'SerialNo='"
								};
						String result = HDDSerialID.runProcessAndReturnResult(cmd);
						int whereIsSerialNo = result.indexOf("SerialNo=");
//						System.out.println(whereIsSerialNo);
						if(whereIsSerialNo != -1)
						{
							String serial = result.substring(result.indexOf("SerialNo=")+9).trim();
							serials.add(serial);
//							System.out.println(drive + " : " + serial);
						}
						return serials;
					}
				}
				else
				{
					System.out.println("Please use root to run server");
				}
			} catch (IOException e) {
				
			}
		}
		else if(os.startsWith("Mac OS X"))
		{
			//TODO mac version
			System.out.println("Not implement at Mac OS X.");
		}
		return new ArrayList<String>();
	}
	
	public static boolean hasHDDSerialID(String serial)
	{
		if (hddserials.contains(serial))
		{
			return true;
		}
		return false;
	}
	
	private static String runProcessAndReturnResult(String cmd) throws IOException
	{
		String result = "";
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(cmd);
		BufferedReader buffIn = new BufferedReader(
					new InputStreamReader(
		                process.getInputStream()));
		String s;
		while((s=buffIn.readLine())!= null)
			result += s + "\r\n";
		buffIn.close();
//		System.out.println("RUNNING COMMAND: " + cmd + " RESULT: " + result);
		return result.trim();
	}
	
	private static String runProcessAndReturnResult(String[] cmd) throws IOException
	{
		String result = "";
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(cmd);
		BufferedReader buffIn = new BufferedReader(
					new InputStreamReader(
		                process.getInputStream()));
		String s;
		while((s=buffIn.readLine())!= null)
			result += s + "\r\n";
		buffIn.close();
		return result.trim();
	}

}
