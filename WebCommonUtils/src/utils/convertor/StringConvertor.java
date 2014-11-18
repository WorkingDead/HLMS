package utils.convertor;

import java.lang.Character.UnicodeBlock;

public class StringConvertor {
	
	public static String addSpaceAfter(String str, int total_length)
	{
		return addAfter(str, ' ', total_length);
	}
	
	public static String addSpaceBefore(String str, int total_length)
	{
		return addBefore(str, ' ', total_length);
	}
	
	public static String addBefore(String str, char c, int total_length)
	{
		for(;str.length()<total_length;)
		{
			str = c + str;
		}
		return str;
	}
	
	public static String addAfter(String str, char c, int total_length)
	{
		for(;str.length()<total_length;)
		{
			str = str + c;
		}
		return str;
	}
	
	//Inserting Zero
	public static String insertZero(String s, int len) {

		int l = s.length();
		
		char c[] = s.toCharArray();		
		char cc[] = new char[len];				
									
	
		int count = 0;					
		for (int i=(l-1); i>=0; i--) {
			cc[count]= c[i];
			count = count + 1;
		}
	
		for (int i = count; i<len; i++)	
			cc[i] = '0';
	
		c = new char[len];					
		count = 0;						
		for (int i=len-1; i>=0; i--) {			
			c[count]= cc[i];
			count = count + 1;
		}
	
		s="";							
		for (int k=0; k<len; k++)		
			s=s+c[k];
		
		return s;
	}
	
	//utf8
	public static String utf8ToUnicode(String inStr) {
		char[] myBuffer = inStr.toCharArray();
		StringBuffer sb = new StringBuffer(); 
		for (int i = 0; i < inStr.length(); i++) {
			UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
			if(ub == UnicodeBlock.BASIC_LATIN){ 
				sb.append(myBuffer[i]); 
			}else if(ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS){
				int j = (int) myBuffer[i] - 65248;
				sb.append((char)j);
			}else{
				short s = (short) myBuffer[i];
				String hexS = Integer.toHexString(s);
				String unicode = "\\u"+hexS; 
				sb.append(unicode.toLowerCase());
			} 
		} 
		return sb.toString();
	}
	public static String unicodeToUtf8(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
	
		for (int x = 0; x < len;){
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the \\uxxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + aChar - '0';
								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + 10 + aChar - 'a';
								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + 10 + aChar - 'A';
								break;
							default:
								throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}
}
