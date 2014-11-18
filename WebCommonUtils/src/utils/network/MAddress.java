//package com.sedna.common.util;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.InetAddress;
//import java.text.ParseException;
//import java.util.StringTokenizer;
//
//public class MAddress {
//	
//	private final static int MACADDR_LENGTH = 17;
//	private final static String WIN_OSNAME = "Windows";
//	private final static String WIN_MACADDR_REG_EXP = "^[0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2}-[0-9A-F]{2}$";
//	private final static String WIN_MACADDR_EXEC = "ipconfig /all";
//	
//	public final static String getMacAddress() throws IOException {
//	    String os = System.getProperty("os.name");
//	    try {
//	      if (os.startsWith(WIN_OSNAME)) {
//	         return winMacAddress(winIpConfigCommand());
//	      }
//	      else {
//	         throw new IOException("OS not supported : " + os);
//	      }
//	    }
//	    catch(ParseException e) {
//	      e.printStackTrace();
//	      throw new IOException(e.getMessage());
//	    }
//	 }
//
//	 private final static String winMacAddress(String ipConfigOutput) 
//	        throws ParseException {
//		String localHost = null;
//	    try {
//	      localHost = InetAddress.getLocalHost().getHostAddress();
//	    }
//	    catch(java.net.UnknownHostException ex) {
//	      ex.printStackTrace();
//	      throw new ParseException(ex.getMessage(), 0);
//	    }
//	    
//	    StringTokenizer tokenizer = 
//	        new StringTokenizer(ipConfigOutput, "\n");
//	    String lastMacAddress = "";
//	    
//	    //System.out.println("Output ="+ ipConfigOutput);
//
//	    while(tokenizer.hasMoreTokens()) {
//	      String line = tokenizer.nextToken().trim();
//	      // see if line contains IP address
////	      if (line.endsWith(localHost) && lastMacAddress != null) {
////	         return lastMacAddress;
////	      }
//
//	      // see if line contains MAC address
//	      int macAddressPosition = line.indexOf(":");
//	      if(macAddressPosition <= 0) continue;
//
//	      String macAddressCandidate = line.substring(macAddressPosition + 1).trim();
//	      if (winIsMacAddress(macAddressCandidate)) {
//	         lastMacAddress += macAddressCandidate;
//	         continue;
//	      }
//	    }
//
//	    if (!(lastMacAddress.equals("")))
//	    {
//	    	return lastMacAddress;
//	    }
//	    else
//	    {
//		    ParseException ex = new ParseException
//		       ("cannot read MAC address from [" + ipConfigOutput + "]", 0);
//		    ex.printStackTrace();
//		    throw ex;
//	    }
//	  }
//
//
//	  private final static boolean winIsMacAddress(String macAddressCandidate) {
//	    if (macAddressCandidate.length() != MACADDR_LENGTH)    return false;
//	    if (!macAddressCandidate.matches(WIN_MACADDR_REG_EXP)) return false;
//	    return true;
//	  }
//
//
//	  private final static String winIpConfigCommand() throws IOException {
//	    Process p = Runtime.getRuntime().exec(WIN_MACADDR_EXEC);
//	    InputStream stdoutStream = new BufferedInputStream(p.getInputStream());
//
//	    StringBuffer buffer= new StringBuffer();
//	    for (;;) {
//	       int c = stdoutStream.read();
//	       if (c == -1) break;
//	          buffer.append((char)c);
//	    }
//	    String outputText = buffer.toString();
//	    stdoutStream.close();
//	    return outputText;
//	  }
//}

package utils.network;
 
import java.net.InetAddress;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
 
public class MAddress
{
	public final static String getMacAddress()
	{
		String mac = "";
		String os = System.getProperty("os.name");
		try
			{
			if(os.startsWith("Windows"))
				{
				mac = windowsParseMacAddress(windowsRunIpConfigCommand());
				}
			else if(os.startsWith("Linux"))
				{
				mac = linuxParseMacAddress(linuxRunIfConfigCommand());
				}
			else if(os.startsWith("Mac OS X"))
				{
				mac = osxParseMacAddress(osxRunIfConfigCommand());
				}
			}
		catch(Exception ex)
			{
			ex.printStackTrace();
			}
		return mac.replaceAll(":","-");
	}
 
	/*
	 * Linux stuff
	 */
	private final static String linuxParseMacAddress(String ipConfigResponse) throws ParseException
	{
		String localHost = null;
		try 
			{
			localHost = InetAddress.getLocalHost().getHostAddress();
			}
		catch(java.net.UnknownHostException ex)
			{
			ex.printStackTrace();
			throw new ParseException(ex.getMessage(), 0);
			}
		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		String lastMacAddress = "";
 
		while(tokenizer.hasMoreTokens())
			{
			String line = tokenizer.nextToken().trim();
			boolean containsLocalHost = line.indexOf(localHost) >= 0;
			// see if line contains IP address
//			if(containsLocalHost && lastMacAddress != null)
//				{
//				return lastMacAddress;
//				}
 
			// see if line contains MAC address
			int macAddressPosition = line.indexOf("HWaddr");
			if(macAddressPosition <= 0) continue;
 
			String macAddressCandidate = line.substring(macAddressPosition + 6).trim();
			if(linuxIsMacAddress(macAddressCandidate))
				{
				lastMacAddress += macAddressCandidate;
				continue;
				}
			}
		if (!(lastMacAddress.equals("")))
	    {
	    	return lastMacAddress;
	    }
		else
		{
			ParseException ex = new ParseException("cannot read MAC address for " + localHost + " from [" + ipConfigResponse + "]", 0);
			ex.printStackTrace();
			throw ex;
		}
	}
 
 
	private final static boolean linuxIsMacAddress(String macAddressCandidate)
	{
		Pattern macPattern = Pattern.compile("[0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0 -9a-fA-F]{2}[-:][0-9a-fA-F]{2}");
		Matcher m = macPattern.matcher(macAddressCandidate);
		return m.matches();
	}
 
 
	private final static String linuxRunIfConfigCommand() throws IOException
	{
		Process p = Runtime.getRuntime().exec("ifconfig");
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());
 
		StringBuffer buffer= new StringBuffer();
		for (;;)
			{
			int c = stdoutStream.read();
			if (c == -1) break;
			buffer.append((char)c);
			}
		String outputText = buffer.toString();
		stdoutStream.close();
		return outputText;
	}
 
	/*
	 * Windows stuff
	 */
	private final static String windowsParseMacAddress(String ipConfigResponse) throws ParseException
	{
		String localHost = null;
	    try {
	      localHost = InetAddress.getLocalHost().getHostAddress();
	    }
	    catch(java.net.UnknownHostException ex) {
	      ex.printStackTrace();
	      throw new ParseException(ex.getMessage(), 0);
	    }
	    
	    StringTokenizer tokenizer = 
	        new StringTokenizer(ipConfigResponse, "\n");
	    String lastMacAddress = "";
	    
	    //System.out.println("Output ="+ ipConfigOutput);

	    while(tokenizer.hasMoreTokens()) {
	      String line = tokenizer.nextToken().trim();

	      // see if line contains MAC address
	      int macAddressPosition = line.indexOf(":");
	      if(macAddressPosition <= 0) continue;

	      String macAddressCandidate = line.substring(macAddressPosition + 1).trim();
	      if (windowsIsMacAddress(macAddressCandidate)) {
	         lastMacAddress += macAddressCandidate;
	         continue;
	      }
	    }

	    if (!(lastMacAddress.equals("")))
	    {
	    	return lastMacAddress;
	    }
	    else
	    {
		    ParseException ex = new ParseException
		       ("cannot read MAC address from [" + ipConfigResponse + "]", 0);
		    ex.printStackTrace();
		    throw ex;
	    }
	}
 
 
	private final static boolean windowsIsMacAddress(String macAddressCandidate)
		{
		Pattern macPattern = Pattern.compile("[0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0 -9a-fA-F]{2}[-:][0-9a-fA-F]{2}");
		Matcher m = macPattern.matcher(macAddressCandidate);
		return m.matches();
	}
 
	private final static String windowsRunIpConfigCommand() throws IOException
		{
		Process p = Runtime.getRuntime().exec("ipconfig /all");
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());
 
		StringBuffer buffer= new StringBuffer();
		for (;;)
			{
			int c = stdoutStream.read();
			if (c == -1) break;
			buffer.append((char)c);
			}
		String outputText = buffer.toString();
		stdoutStream.close();
		return outputText;
	}
 
	 /*
	 * Mac OS X Stuff
	 */
	 private final static String osxParseMacAddress(String ipConfigResponse) throws ParseException
	 {
		String localHost = null;
 
		try
			{
			localHost = InetAddress.getLocalHost().getHostAddress();
			}
		catch(java.net.UnknownHostException ex)
			{
			ex.printStackTrace();
			throw new ParseException(ex.getMessage(), 0);
			}
		StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
		while(tokenizer.hasMoreTokens())
			{
			String line = tokenizer.nextToken().trim();
			// see if line contains MAC address
			int macAddressPosition = line.indexOf("ether");
			if(macAddressPosition != 0) continue;
			String macAddressCandidate = line.substring(macAddressPosition + 6).trim();
			if(osxIsMacAddress(macAddressCandidate))
				{
				return macAddressCandidate;
				}
			}
 
		ParseException ex = new ParseException("cannot read MAC address for " + localHost + " from [" + ipConfigResponse + "]", 0);
		ex.printStackTrace();
		throw ex;
	 }
 
	private final static boolean osxIsMacAddress(String macAddressCandidate)
		{
		Pattern macPattern = Pattern.compile("[0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0 -9a-fA-F]{2}[-:][0-9a-fA-F]{2}");
		Matcher m = macPattern.matcher(macAddressCandidate);
		return m.matches();
	}
 
	private final static String osxRunIfConfigCommand() throws IOException
		{
		Process p = Runtime.getRuntime().exec("ifconfig");
		InputStream stdoutStream = new BufferedInputStream(p.getInputStream());
		StringBuffer buffer= new StringBuffer();
		for (;;)
			{
			int c = stdoutStream.read();
			if (c == -1) break;
			buffer.append((char)c);
			}
		String outputText = buffer.toString();
		stdoutStream.close();
		return outputText;
	}
}
