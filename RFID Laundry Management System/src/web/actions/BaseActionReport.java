package web.actions;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import javax.servlet.http.HttpServletResponse;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;
import module.dao.general.Receipt;
import module.dao.general.Receipt.ReceiptType;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import utils.convertor.DateConverter;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class BaseActionReport extends BaseActionSecurity implements ServletResponseAware
{
	private static final long serialVersionUID = 1560171868883675259L;
	private static final Logger log4j = Logger.getLogger(BaseActionReport.class);
	
	public final String DEFAULT_SHEET_NAME = "S1";
	public final int GENERAL_CELL_WIDTH = 25;
	
	protected static enum ExportFormat
	{
		PDF("PDF", "PDF"),
		EXCEL("EXCEL", "XLS");
//		CSV("CSV", "CSV");
		
		private String displayName;
		private String exportName;
		
		ExportFormat(String displayName, String exportName)
		{
			this.displayName = displayName;
			this.exportName = exportName;
		}

		public String getDisplayName()
		{
			return displayName;
		}
		public void setDisplayName(String displayName)
		{
			this.displayName = displayName;
		}
		public String getExportName()
		{
			return exportName;
		}
		public void setExportName(String exportName)
		{
			this.exportName = exportName;
		}
	}
	
	///////////////////////////////////////////////
	// Member Constants
	///////////////////////////////////////////////
//	public final int NUM_OF_DIGIT_OF_YEAR = 4;
//	public final int NUM_OF_MONTHS_PER_YEAR = 12;
//	public final int MAX_NUM_OF_DIGIT_OF_MONTH = 2;
//	
//	public final String YEAR_START_POSTFIX= "-01-01 00:00:00";
//	public final String YEAR_END_POSTFIX = "-12-31 23:59:59";
	
	public final String MONTH_START_POSTFIX= "-01 00:00:00";
	public final String MONTH_31_END_POSTFIX = "-31 23:59:59";
	public final String MONTH_30_END_POSTFIX = "-30 23:59:59";
	public final String COMM_FEB_END_POSTFIX= "-02-28 23:59:59";	// for handling Common Year of February
	public final String LEAP_FEB_END_POSTFIX= "-02-29 23:59:59";	// for handling Leap Year of February
	
	
	///////////////////////////////////////////////
	// Member Variables
	///////////////////////////////////////////////
	protected HttpServletResponse response;
	protected Calendar dateFrom;
	protected Calendar dateTo;
	protected String dateFromStr;
	protected String dateToStr;
	protected Integer year;
	protected Integer month;
	protected String yearMonthStr;
	protected Integer reportClothTotal;
	protected String reportClothTypeTotalStr;
	protected TreeMap<String, Integer> clothTypeCountMap;
	
//	private List<ExportFormat> formatList = Arrays.asList(ExportFormat.PDF, ExportFormat.CSV);
	private List<ExportFormat> formatList = Arrays.asList(ExportFormat.values());
	private String format;	// the selected format
	
	
	/////////////////////////////////////////////////
	// Utility Method
	/////////////////////////////////////////////////
	
	public WritableCellFormat getJxlCellFormatForReportTitle()
	{
		try
		{
			WritableFont font16 = new WritableFont(WritableFont.ARIAL, 16);
			WritableCellFormat cellType16 = new WritableCellFormat(font16);
			cellType16.setAlignment(Alignment.CENTRE);
//			cellType16.setWrap(true);
			return cellType16;
		}
		catch (WriteException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	public WritableCellFormat getJxlCellFormatForReportPageHeader()
	{
		try
		{
			WritableFont font11 = new WritableFont(WritableFont.ARIAL, 11);
			WritableCellFormat cellType11 = new WritableCellFormat(font11);
			cellType11.setAlignment(Alignment.CENTRE);
//			cellType11.setWrap(true);
			return cellType11;
		}
		catch (WriteException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	public WritableCellFormat getJxlCellFormatForReportColumnHeader()
	{
		try
		{
			WritableFont colTitleFont = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
			WritableCellFormat cellBgGray = new WritableCellFormat();
			cellBgGray.setFont(colTitleFont);
			cellBgGray.setBackground(Colour.GRAY_80);
//			cellBgGray.setWrap(true);
			return cellBgGray;
		}
		catch (WriteException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	public WritableCellFormat getJxlCellFormatForReportData()
	{
		try
		{
			WritableCellFormat dataCell = new WritableCellFormat();
			dataCell.setWrap(true);
			dataCell.setVerticalAlignment(VerticalAlignment.TOP);
			return dataCell;
		}
		catch (WriteException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public String getCurYearMonthStr()
	{
		String result = null;
		Calendar curTime = Calendar.getInstance();
		int year = curTime.get(Calendar.YEAR);
		int month = curTime.get(Calendar.MONTH) + 1;	// month start from 0 (0 is JAN)
		
		if (month < 10)
			result = year + "-0" + month;
		else
			result = year + "-" + month;
		
		return result;
	}
	
	
	// find month-start and month-end of a given year and month (handle leap year situation)
	// example: given year 2012 and month 2
	// 			calMonthStartEndArray[0]: 	2012-02-01 00:00:00
	//			calMonthStartEndArray[1]: 	2012-02-01
	//			strMonthStartEndArray[0]: 	2012-02-29 23:59:59
	//			strMonthStartEndArray[1]: 	2012-02-29
	public void findMonthStartAndMonthEnd(int curYear, int curMonth, Calendar calMonthStartEndArray[], String strMonthStartEndArray[])
	{
		if (calMonthStartEndArray == null || strMonthStartEndArray == null )
		{
			System.out.println("Error: Month Arrays is/are NULL!");
			return;
		}
		else if (calMonthStartEndArray.length != 2 ||
				 calMonthStartEndArray.length != strMonthStartEndArray.length)
		{
			System.out.println("Error: Size of Month Arrays is/are not 2!");
			return;
		}
		
		String monthNum = null;
		// zero-padding of single-digit months
		if (curMonth < 10)
			monthNum = "0" + curMonth;
		else
			monthNum = "" + curMonth;
		
		// Construct the String of month-start (result eg: 2010-01-01 00:00:00)
		String timeStart = curYear + "-" + monthNum + MONTH_START_POSTFIX;
		
		// Construct the String of month-end (result eg: 2010-12-31 23:59:59)
		String timeEnd = null;
		if ( curMonth == 2 )
		{
			// handling February (common year or leap year)
			if (curYear % 400 == 0)
				timeEnd = curYear + LEAP_FEB_END_POSTFIX;
			else if ((curYear % 4 == 0) && (curYear % 100 != 0))
				timeEnd = curYear + LEAP_FEB_END_POSTFIX;
			else
				timeEnd = curYear + COMM_FEB_END_POSTFIX;
		}
		else if ( curMonth == 1 || curMonth == 3 || curMonth == 5 || curMonth == 7 ||
				  curMonth == 8 || curMonth == 10 || curMonth == 12 )
		{
			// handling 31-days months
			timeEnd = curYear + "-" + monthNum + MONTH_31_END_POSTFIX;
		}
		else
		{
			// handling 30-days months
			timeEnd = curYear + "-" + monthNum + MONTH_30_END_POSTFIX;
		}
		
		// Convert Calendar and String
		try
		{
			calMonthStartEndArray[0] = DateConverter.toCalendar(timeStart, DateConverter.DB_DATETIME_FORMAT);
			strMonthStartEndArray[0] = DateConverter.format(calMonthStartEndArray[0], DateConverter.DATE_FORMAT);
			
			calMonthStartEndArray[1] = DateConverter.toCalendar(timeEnd, DateConverter.DB_DATETIME_FORMAT);
			strMonthStartEndArray[1] = DateConverter.format(calMonthStartEndArray[1], DateConverter.DATE_FORMAT);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
	}
	
	
	/////////////////////////////////////////////////
	// Shared Method
	/////////////////////////////////////////////////
	public HttpServletResponse getResponse()
	{
		return response;
	}

	@Override
	public void setServletResponse(HttpServletResponse response)
	{
		this.response = response;
	}
	
	
	public List<Receipt> findReceipt(Calendar dateFrom, Calendar dateTo, final ReceiptType receiptType)
	{
		dateFrom.set(Calendar.HOUR_OF_DAY, dateFrom.getActualMinimum(Calendar.HOUR_OF_DAY));
		dateFrom.set(Calendar.MINUTE, dateFrom.getActualMinimum(Calendar.MINUTE));
		dateFrom.set(Calendar.SECOND, dateFrom.getActualMinimum(Calendar.SECOND));
		dateFrom.getTime();		// getTime() is necessary
		final Calendar dateStart = dateFrom;
		
		dateTo.set(Calendar.HOUR_OF_DAY, dateTo.getActualMaximum(Calendar.HOUR_OF_DAY));
		dateTo.set(Calendar.MINUTE, dateTo.getActualMaximum(Calendar.MINUTE));
		dateTo.set(Calendar.SECOND, dateTo.getActualMaximum(Calendar.SECOND));
		dateTo.getTime();		// getTime() is necessary
		final Calendar dateEnd = dateTo;
		
		List<Receipt> receiptList = this.getGeneralService().findByExample( Receipt.class, null, null, null,
				new CustomCriteriaHandler<Receipt>()
				{
					@Override
					public void makeCustomCriteria(Criteria criteria)
					{
						criteria.add(  Restrictions.eq("receiptType", receiptType) );
						criteria.add(  Restrictions.ge("creationDate", dateStart) );
						criteria.add(  Restrictions.le("creationDate", dateEnd) );
					}
				}, 
				
				new CustomLazyHandler<Receipt>()
				{
					@Override
					public void LazyList(List<Receipt> list)
					{
						Receipt receipt = null;
						for (int i = 0; i < list.size(); i++)
						{
							Iterator<Cloth> itCloth = list.get(i).getClothSet().iterator();
							while (itCloth.hasNext())
							{
								itCloth.next().getClothType().getId();
							}
						}
					}
				}, 
				
				Order.asc("creationDate")
		);
		
		return receiptList;
	}
	
	
	
	/////////////////////////////////////////////////
	// Getter and Setter
	/////////////////////////////////////////////////
	public Calendar getDateFrom()
	{
		return dateFrom;
	}
	@TypeConversion(converter = "utils.convertor.struts2.SimpleDateTimeToCalendarTypeConvertor")
	public void setDateFrom(Calendar dateFrom)
	{
		this.dateFrom = dateFrom;
	}
	public Calendar getDateTo()
	{
		return dateTo;
	}
	@TypeConversion(converter = "utils.convertor.struts2.SimpleDateTimeToCalendarTypeConvertor")
	public void setDateTo(Calendar dateTo)
	{
		this.dateTo = dateTo;
	}
	public String getDateFromStr()
	{
		return dateFromStr;
	}
	public void setDateFromStr(String dateFromStr)
	{
		this.dateFromStr = dateFromStr;
	}
	public String getDateToStr()
	{
		return dateToStr;
	}
	public void setDateToStr(String dateToStr)
	{
		this.dateToStr = dateToStr;
	}
	public Integer getReportClothTotal()
	{
		return reportClothTotal;
	}
	public void setReportClothTotal(Integer reportClothTotal)
	{
		this.reportClothTotal = reportClothTotal;
	}
	public List<ExportFormat> getFormatList()
	{
		return formatList;
	}
	public void setFormatList(List<ExportFormat> formatList)
	{
		this.formatList = formatList;
	}
	public String getFormat()
	{
		return format;
	}
	public void setFormat(String format)
	{
		this.format = format;
	}
	public Integer getYear()
	{
		return year;
	}
	public void setYear(Integer year)
	{
		this.year = year;
	}
	public Integer getMonth()
	{
		return month;
	}
	public void setMonth(Integer month)
	{
		this.month = month;
	}
	public String getYearMonthStr()
	{
		return yearMonthStr;
	}
	public void setYearMonthStr(String yearMonthStr)
	{
		this.yearMonthStr = yearMonthStr;
	}
	public TreeMap<String, Integer> getClothTypeCountMap()
	{
		return clothTypeCountMap;
	}
	public void setClothTypeCountMap(TreeMap<String, Integer> clothTypeCountMap)
	{
		this.clothTypeCountMap = clothTypeCountMap;
	}
	public String getReportClothTypeTotalStr()
	{
		return reportClothTypeTotalStr;
	}
	public void setReportClothTypeTotalStr(String reportClothTypeTotalStr)
	{
		this.reportClothTypeTotalStr = reportClothTypeTotalStr;
	}
}
