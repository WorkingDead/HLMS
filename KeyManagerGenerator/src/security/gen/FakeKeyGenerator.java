package security.gen;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class FakeKeyGenerator {

	private static final String realKeyName = "2DE98";
	private static final String targetKeyPath = "C:/Sedna";

	private static final Map<Integer, Integer> bytesMap = new ConcurrentHashMap<Integer, Integer>();

	private static final Map<Integer, Integer> fakeFileNameMap = new ConcurrentHashMap<Integer, Integer>();
	

	private static BufferedOutputStream bos;



	public static void main(String[] args) {
		
		File f = new File( targetKeyPath + "/" + realKeyName );
	    int size = (int) f.length();
	    byte[] bytes = new byte[size];
	    try {
	        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(f));
	        buf.read(bytes, 0, bytes.length);
	        buf.close();
	    }
	    catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    }
	    catch (IOException e) {
	    	e.printStackTrace();
	    }



	    final Random prng = new Random(); // randomly seeded
	    final long seed = prng.nextLong() + Calendar.getInstance().getTimeInMillis();
	    prng.setSeed(seed);

	    byte[] finalBytes = new byte[ bytes.length ];
	    int total = 0;
	    while ( total < bytes.length ) {

	    	int eachRandomByte = prng.nextInt( bytes.length );

		    if ( bytesMap.containsKey( eachRandomByte ) == false ) {
		    	bytesMap.put(eachRandomByte, eachRandomByte);
		    	finalBytes[total] = bytes[eachRandomByte];
		    	total = total + 1;
		    }
	    }
	    
	    System.out.println("total = " + total);



	    final Random pr = new Random(); // randomly seeded
	    final long se = prng.nextLong() + Calendar.getInstance().getTimeInMillis();
	    pr.setSeed(se);

	    char[] ch = realKeyName.toCharArray();
	    StringBuilder newFakeFileNmae = new StringBuilder();
	    int t = 0;
	    while ( t < ch.length ) {

	    	int eachC =  pr.nextInt( ch.length );

	    	if ( fakeFileNameMap.containsKey( eachC ) == false ) {
	    		fakeFileNameMap.put(eachC, eachC);
	    		newFakeFileNmae.append( ch[eachC] );
	    		t = t + 1;
	    	}
	    }

	    System.out.println("newFakeFileChar = " + newFakeFileNmae.toString());



		try {
			File file = new File( targetKeyPath + "/" + newFakeFileNmae.toString());
			if (file.exists()) {
				file.delete();
			}
			
			bos = new BufferedOutputStream( new FileOutputStream(file, false) );
			bos.write( finalBytes );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if ( bos != null ) {
				try {
					bos.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
