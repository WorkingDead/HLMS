package security.bo;

import security.utils.StringHandler;

public class Str {

	public static String getString() {

		return  new StringHandler(new long[] {0xE962512D830658FBL, 0xB12CAA15E6275074L}).toString() +
				new StringHandler(new long[] {0xFAC8AD6C1F5D2C4DL, 0x89DB49CA8AFC43D2L}).toString() +
				new StringHandler(new long[] {0x358748D3AA7330C8L, 0x67AC5760F6384CB4L, 0x61415D1934746800L}).toString() +
				new StringHandler(new long[] {0xE77B842360B33DADL, 0xFF38433F3A09D039L}).toString() +
				new StringHandler(new long[] {0xEBF01D1B3264D0FFL, 0x6C026EF76B926DBBL}).toString() +
				new StringHandler(new long[] {0xC531B4D03080F693L, 0xA9D896E95888E47L}).toString() +
				new StringHandler(new long[] {0xF64923E0B8874FDBL, 0x7BAB9E17C1FA5705L}).toString() +
				new StringHandler(new long[] {0x2DB2315956492C94L, 0xDCE32E7EEF8E4B56L}).toString() +
				new StringHandler(new long[] {0xED0F6A6CDC432E7FL, 0x79061F2F82453AE6L}).toString();
	}
}
