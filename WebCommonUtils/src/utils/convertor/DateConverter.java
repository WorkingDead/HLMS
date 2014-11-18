package utils.convertor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateConverter
{
	public static final int GREATERTHAN = 0;
	public static final int SMALLERTHAN = 1;
	public static final int EQUAL = 2;
	public static final int GREATERTHANOREQUAL = 3;
	public static final int SMALLERTHANOREQUAL = 4;
	
	
	public static final String APPOINTMENT_DATETIME_FORMAT = "yyyy-MM-ddHH:mm";
	public static final String LONG_DATE_FORMAT = "MMMM dd, yyyy";
	public static final String TIME_FORMAT = "HH:mm:ss";
	public static final String HOUR_MINUTE_FORMAT = "HH:mm";
	public static final String DB_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String JS_DATETIME_FORMAT = "yyyyMMdd";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATE_FORMAT_REVERSE = "dd-MM-yyyy";
	public static final String ORDER_DATE_FORMAT = "yyyyMMdd";
	public static final String KINPO_REPORT_DATE_FORMAT = "yyyy/MM/dd";
	public static final String EXPORT_DATETIME_FORMAT = "yyyyMMddHHmmss";
	public static final String HANDHELD_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'+08:00'";

	private static SimpleDateFormat df;
	
	public static Calendar getCalendar() {
		Calendar datetime = new GregorianCalendar();
		datetime.set(Calendar.HOUR_OF_DAY, 0);
		datetime.set(Calendar.MINUTE, 0);
		datetime.set(Calendar.SECOND, 0);
		return datetime;
	}
	
	public static Calendar toCalendar(String datetime) {
		Calendar calendar = new GregorianCalendar();
		if (datetime.length() == 8) {
			int year = Integer.parseInt(datetime.substring(0, 4));
			int month = Integer.parseInt(datetime.substring(4, 6)) - 1;
			int date = Integer.parseInt(datetime.substring(6, 8));
			
			calendar.set(year, month, date, 0, 0, 0);
			
		}
		return calendar;
	}
	
	public static Calendar toCalendar(String datetime, String pattern) throws ParseException
	{
		df = new SimpleDateFormat(pattern);
		
		Date date = df.parse(datetime);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}
	
	// DateTime to Calendar (by Horace)
	// Must use following format
	// Date Format: 08-05-2013 (DATE_FORMAT_REVERSE)
	// Time Format: 17:25 (HOUR_MINUTE_FORMAT)
	public static Calendar dateTimeToCalendar(String dateStr, String timeStr)
	{
		Calendar calendar = new GregorianCalendar();
		if (dateStr.length() == 10 && timeStr.length() == 5)
		{
			int day = Integer.parseInt( dateStr.substring(0, 2) );
			int month = Integer.parseInt(dateStr.substring(3, 5)) - 1;
			int year = Integer.parseInt(dateStr.substring(6));
			
			int hour = Integer.parseInt(dateStr.substring(0, 2));
			int minute = Integer.parseInt(dateStr.substring(3));
			
			calendar.set(year, month, day, hour, minute, 0);
		}
		return calendar;
	}
	
	public static Calendar toCalendar(Date date) throws ParseException
	{
		String dateStr = format(date, DATE_FORMAT) + " 00:00:00";
		Calendar calendar = toCalendar(dateStr, DB_DATETIME_FORMAT);
		return calendar;
	}
	
	public static Calendar toCalendar(int year, int month, int date) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(year, month, date, 0, 0, 0);
		return calendar;
	}
	
	public static String format(Calendar calendar, String DATE_FORMAT) {
		df = new SimpleDateFormat(DATE_FORMAT);
		return df.format(calendar.getTime());
	}
	
	public static Calendar getCurrentMonthFirstDay() {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.DATE, 1);
		return calendar; 
	}
	
	public static Calendar getCurrentMonthLastDay() {
		int maxDate = (new GregorianCalendar()).getActualMaximum((Calendar.DAY_OF_MONTH));
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.DATE, maxDate);
		return calendar; 
	}
	
	public static int getMonthLastDay(int month) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.MONTH, month);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	public static Calendar getNextWeek(Calendar current) {
		Calendar nextCalendar = new GregorianCalendar();
		
		nextCalendar.set(Calendar.YEAR, current.get(Calendar.YEAR));
		nextCalendar.set(Calendar.MONTH, current.get(Calendar.MONTH));
		nextCalendar.set(Calendar.DATE, current.get(Calendar.DATE));
		
		nextCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		
		return nextCalendar;
	}
	
	public static Calendar getNextMonth(Calendar current) {
		Calendar nextCalendar = new GregorianCalendar();
		
		nextCalendar.set(Calendar.YEAR, current.get(Calendar.YEAR));
		nextCalendar.set(Calendar.MONTH, current.get(Calendar.MONTH));
		nextCalendar.set(Calendar.DATE, current.get(Calendar.DATE));
		
		nextCalendar.add(Calendar.MONTH, 1);
		
		return nextCalendar;
	}
	
	public static Calendar getNextDay(Calendar current) {
		Calendar nextCalendar = new GregorianCalendar();
		
		nextCalendar.set(Calendar.YEAR, current.get(Calendar.YEAR));
		nextCalendar.set(Calendar.MONTH, current.get(Calendar.MONTH));
		nextCalendar.set(Calendar.DATE, current.get(Calendar.DATE));
		
		nextCalendar.add(Calendar.DATE, 1);
		
		return nextCalendar;
	}
	
	public static String getDateIntervalByWeek(int week_of_year, int year, String format) {
		String weekInterval = "";
		Calendar startCalendar = toCalendar(year +"0101");
		
		int day_of_week = startCalendar.get(Calendar.DAY_OF_WEEK);
		if (day_of_week > 1) {
			startCalendar.add(Calendar.DATE, -(day_of_week - 1));
		}
		
		startCalendar.add(Calendar.WEEK_OF_YEAR, week_of_year - 1);
		
		weekInterval += DateConverter.format(startCalendar, format);
		startCalendar.add(Calendar.DATE, 6);
		weekInterval +=" - "+ DateConverter.format(startCalendar, format);
		
		return weekInterval;
	}
	
	//hour + minutes
	public static Calendar toCalendarWithHoursAndMinutes(String hour, String minutes) {
		Calendar calendar = new GregorianCalendar();
		if (hour.length() == 2 && minutes.length()==2) {
			int year = 0;
			int month = 0;
			int date = 0;
			int hour_ = Integer.parseInt(hour);
			int minutes_ = Integer.parseInt(minutes);
			int sec = 0;
			
			calendar.set(year, month, date, hour_, minutes_, sec);
			
		}
		return calendar;
	}
	
	public static void setCalendarToBeginOfDate(Calendar cld){
		cld.set(Calendar.HOUR, 0);
		cld.set(Calendar.MINUTE, 0);
		cld.set(Calendar.SECOND, 0);
		cld.set(Calendar.MILLISECOND, 0);
	}
	
	public static String format(Calendar calendar, String DATE_FORMAT, Locale locale) {
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT, locale);
		return df.format(calendar.getTime());
	}
	
	public static String format(Date date, String DATE_FORMAT) {
		df = new SimpleDateFormat(DATE_FORMAT);
		return df.format(date);
	}
	
	public static String format(Date date, String DATE_FORMAT, Locale locale) {
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT, locale);
		return df.format(date);
	}
	
	
	public static boolean compare2CalendarWithDuration(Calendar c1, Calendar c2, int field, int field_value, int indicator)
	{
		Calendar c1temp = (Calendar) c1.clone();
		Calendar c2temp = (Calendar) c2.clone();
		c2temp.add(field, field_value);
		switch (indicator)
		{
			case GREATERTHAN:
				return c1temp.compareTo(c2temp)>0;
			case GREATERTHANOREQUAL:
				return c1temp.compareTo(c2temp)>=0;
			case EQUAL:
				return c1temp.compareTo(c2temp)==0;
			case SMALLERTHAN:
				return c1temp.compareTo(c2temp)<0;
			case SMALLERTHANOREQUAL:
				return c1temp.compareTo(c2temp)<=0;
			default: System.out.println("Invalid Indicator!!");
				return false;
		}
	}
	
	public static String nextDate(String date)
	{
		Calendar temp = toCalendar(date);
		temp.add(Calendar.DATE, 1);
		return format(temp, DateConverter.DATE_FORMAT);
		
	}
	
	public static String getToday()
	{
		Calendar temp = Calendar.getInstance();
		return format(temp, DateConverter.DATE_FORMAT);
	}
	
	public static String getLongToday(Locale locale)
	{
		Calendar temp = Calendar.getInstance();
		return format(temp, DateConverter.LONG_DATE_FORMAT, locale);
	}
	
	public static boolean isSameDate(Calendar c1, Calendar c2){
		if(c1.get(Calendar.YEAR)==c2.get(Calendar.YEAR) && 
				c1.get(Calendar.MONTH)==c2.get(Calendar.MONTH) &&
				c1.get(Calendar.DAY_OF_MONTH)==c2.get(Calendar.DAY_OF_MONTH)){
			return true;
		}else{
			return false;
		}
	}
	
	public static long findDifferenceInMinutes(Calendar start, Calendar end){
		return ((end.getTimeInMillis()-start.getTimeInMillis())/60000);
	}
	
	public static Date toDate(String date, String pattern)
	{
		try{
			DateFormat df = new SimpleDateFormat(pattern);
			Date result = df.parse(date);
			return result;
		}catch (Exception e)
		{
			return null;
		}
	}
	
	//Count the days including the start and end calendar
	public static int calculateAllDaysByStartEnd(Calendar start, Calendar end) {
		
		Calendar startCal = (Calendar)start.clone();
		Calendar endCal = (Calendar)end.clone();
		
		startCal.set(Calendar.HOUR_OF_DAY, startCal.getActualMinimum(Calendar.HOUR_OF_DAY));
		startCal.set(Calendar.MINUTE, startCal.getActualMinimum(Calendar.MINUTE));
		startCal.set(Calendar.SECOND, startCal.getActualMinimum(Calendar.SECOND));
		
		endCal.set(Calendar.HOUR_OF_DAY, endCal.getActualMaximum(Calendar.HOUR_OF_DAY));
		endCal.set(Calendar.MINUTE, endCal.getActualMaximum(Calendar.MINUTE));
		endCal.set(Calendar.SECOND, endCal.getActualMaximum(Calendar.SECOND));

		int days = 0;

		if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
			return days;
		}

		while (startCal.getTimeInMillis() < endCal.getTimeInMillis()) {
			days++;
			startCal.add(Calendar.DAY_OF_MONTH, 1);
		}

		return days;
	}

	//Count the working days including the start and end calendar
	public static int calculateWorkingDaysByStartEnd(Calendar start, Calendar end) {	//New Version
//	public static int calculateWorkingDaysByStartEnd(Calendar startCal, Calendar endCal) throws Exception {

//		Calendar startCal = Calendar.getInstance();
//		startCal.setTime(startDate);
//
//		Calendar endCal = Calendar.getInstance();
//		endCal.setTime(endDate);
		
		Calendar startCal = (Calendar)start.clone();
		Calendar endCal = (Calendar)end.clone();
		
		startCal.set(Calendar.HOUR_OF_DAY, startCal.getActualMinimum(Calendar.HOUR_OF_DAY));
		startCal.set(Calendar.MINUTE, startCal.getActualMinimum(Calendar.MINUTE));
		startCal.set(Calendar.SECOND, startCal.getActualMinimum(Calendar.SECOND));
		
		endCal.set(Calendar.HOUR_OF_DAY, endCal.getActualMaximum(Calendar.HOUR_OF_DAY));
		endCal.set(Calendar.MINUTE, endCal.getActualMaximum(Calendar.MINUTE));
		endCal.set(Calendar.SECOND, endCal.getActualMaximum(Calendar.SECOND));
		
		int workDays = 0;

		if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
//			startCal.setTime(endDate);
//			endCal.setTime(startDate);
			//throw new Exception("Start Cal > End Cal");
			return workDays;
		}

		while ( startCal.getTimeInMillis() < endCal.getTimeInMillis() ) {
			if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				workDays++;
			}
			startCal.add(Calendar.DAY_OF_MONTH, 1);
		}

		return workDays;
	}

	//TODO Please test it whether it is totally correct or not
	public static Date calculateWorkingDaysByStartDuration(Date startDate, int duration) {

		Calendar startCal = Calendar.getInstance();

		startCal.setTime(startDate);

		for (int i = 1; i < duration; i++) {
			startCal.add(Calendar.DAY_OF_MONTH, 1);
			while (startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || startCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				startCal.add(Calendar.DAY_OF_MONTH, 1);
			}
		}

		return startCal.getTime();
	}
}