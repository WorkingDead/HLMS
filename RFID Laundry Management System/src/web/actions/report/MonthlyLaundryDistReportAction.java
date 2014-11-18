package web.actions.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import module.dao.general.Receipt;
import module.dao.general.Receipt.ReceiptType;
import module.dao.master.Cloth;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import utils.convertor.DateConverter;
import web.actions.BaseActionReport;

@Results({
	@Result(name="getReportPage", location="monthly.laundry.dist.report", type="tiles"), 
	@Result(name="input", location="monthly.laundry.dist.report", type="tiles"),
	
	@Result(name="makeReport",  type="jasper", params={
			"location", 	"/jasper_report/reportMonthlyLaundryDist.jasper", 
			"dataSource", 	"reportList",
			"format", 		"${format}",
			"documentName",	"Monthly_Laundry_Dist_Report"
	}),
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", "exportReport"}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class MonthlyLaundryDistReportAction extends BaseActionReport
{
	private static final long serialVersionUID = 5854852324575959370L;
	private static final Logger log4j = Logger.getLogger(MonthlyLaundryDistReportAction.class);
	
	private List<ReportObject1> reportList;
	// private String yearMonthStr;		// already defined in superclass
	
	public MonthlyLaundryDistReportAction()
	{
		yearMonthStr = this.getCurYearMonthStr();
	}
	
	public String getReportPage()
	{
		return "getReportPage";
	}
	
	public void validateExportReport()
	{
		if (yearMonthStr == null || yearMonthStr.isEmpty())
		{
			addActionError(String.format(getText("errors.custom.required"), getText("label.month")));
		}
		else
		{
			String parts[] = yearMonthStr.split("-");
			try
			{
				this.year = Integer.parseInt(parts[0]);
				this.month = Integer.parseInt(parts[1]);
			}
			catch (Exception e)
			{
				addActionError(String.format(getText("errors.custom.invalid"), getText("label.month")));
			}
		}
	}
	
	public String exportReport() throws Exception 
	{
		try
		{
			return exportImpl();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log4j.error( e );
			
			while ( true )
			{
				Exception cause = (Exception) e.getCause();
				if ( cause == null )
				{
					break;
				}
				else
				{
					e = cause;
				}
			}
		}
		
		return "makeReport";
	}
	
	public String exportImpl() throws Exception
	{
		reportClothTotal = 0;
		reportClothTypeTotalStr = "";
		clothTypeCountMap = new TreeMap<String, Integer>();
//		yearMonthStr
		
		Calendar calMonthStartEndArray[] = new Calendar[2];
		String strMonthStartEndArray[] = new String[2];
		this.findMonthStartAndMonthEnd(year, month, calMonthStartEndArray, strMonthStartEndArray);
		
		this.dateFrom = calMonthStartEndArray[0];
		this.dateTo = calMonthStartEndArray[1];
		List<Receipt> receiptList = findReceipt(this.dateFrom, this.dateTo, ReceiptType.Distribute);
		
		this.reportList = new ArrayList<ReportObject1>();
		ReportObject1 ro = null;
		Receipt receipt = null;
		List<Cloth> receiptClothList = null;
		Cloth tmpCloth = null;
		String clothTypeName = null;
		Integer clothTypeTotal = null;
		Integer recepitClothTypeTotal = null;
		Map<String, Integer> clothTypeCounter = null;
		String clothTypeNum = null;
		List<String> keyList = null;
		for (int i = 0; i < receiptList.size(); i++)
		{
			receipt = receiptList.get(i);
			ro = new ReportObject1();
			
			ro.setReceiptCode(receipt.getCode());
			ro.setReceiptDate(DateConverter.format(receipt.getCreationDate(), DateConverter.DATE_FORMAT));
			ro.setReceiptStatus(getText(receipt.getReceiptStatus().getValue()));
			ro.setReceiptClothTotal(receipt.getReceiptClothTotal());
			
			// Count each clothType and Summarizing clothes by staff
			receiptClothList = new ArrayList<Cloth>(receipt.getClothSet());
			clothTypeCounter = new HashMap<String, Integer>();
			for (int j = 0; j < receiptClothList.size(); j++)
			{
				// Summarizing clothes by clothType (count each clothType)
				tmpCloth = receiptClothList.get(j);
				clothTypeName = tmpCloth.getClothType().getName();
				if (clothTypeCounter.containsKey(clothTypeName))
				{
					clothTypeTotal = clothTypeCounter.get(clothTypeName);
					clothTypeTotal++;
					clothTypeCounter.put(clothTypeName, clothTypeTotal);
				}
				else
				{
					clothTypeCounter.put(clothTypeName, 1);
				}
			}
			
			keyList = new ArrayList<String>(clothTypeCounter.keySet());
			Collections.sort(keyList);
			clothTypeNum = "";
			for (int j = 0; j < keyList.size(); j++)
			{
				clothTypeName = keyList.get(j);
				clothTypeTotal = clothTypeCounter.get(clothTypeName);
				clothTypeNum += clothTypeName + " x " + clothTypeTotal;
				if (j < keyList.size()-1)
					clothTypeNum += "\n";
				
				
				reportClothTotal += clothTypeTotal;
				if (clothTypeCountMap.containsKey(clothTypeName))
				{
					recepitClothTypeTotal = clothTypeCountMap.get(clothTypeName);
					recepitClothTypeTotal += clothTypeTotal;
					clothTypeCountMap.put(clothTypeName, recepitClothTypeTotal);
				}
				else
				{
					clothTypeCountMap.put(clothTypeName, clothTypeTotal);
				}
			}
			ro.setClothTypeCounter(clothTypeNum);
			this.reportList.add(ro);
		}
		
		ArrayList<Entry<String, Integer>> ctcList = new ArrayList<Entry<String,Integer>>(clothTypeCountMap.entrySet());
		for (int i = 0; i < ctcList.size(); i++)
		{
			Entry<String, Integer> e = ctcList.get(i);
			String type = e.getKey();
			Integer num = e.getValue();
			
			reportClothTypeTotalStr += type + " x " + num;
			if (i < ctcList.size()-1)
				reportClothTypeTotalStr += "\n";
		}
		
		if (this.reportList.size() == 0)
		{
			// handle iReport bug
			this.reportList.add(new ReportObject1());
		}
		
		
		if ((this.getFormat()).equals(ExportFormat.PDF.getExportName()))
		{
			return "makeReport";
		}
		else
		{
			exportExcel();
			return null;
		}
	}
	
	private String exportExcel()
	{
		try
		{
			///////////////////////////////////////////////
			// 1. Set return an Excel to browser
			///////////////////////////////////////////////
			this.response.setContentType("application/vnd.ms-excel");
			this.response.setHeader("Content-Disposition", "attachment; filename=Monthly_Laundry_Dist_Report.xls");
			
			
			///////////////////////////////////////////////
			// 2. Make Excel
			///////////////////////////////////////////////
			final int NUM_OF_COL = 5;
			
			WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
			WritableSheet sheet = workbook.createSheet(DEFAULT_SHEET_NAME, 0);
			
			// Title
			WritableCellFormat titleCellFormat = this.getJxlCellFormatForReportTitle();
			Label label = new Label(0, 0, "\u6bcf\u6708\u6d17\u8863\u623f\u4e7e\u6de8\u8863\u7269\u6d3e\u767c\u5831\u544a", titleCellFormat);
			sheet.addCell(label);
			sheet.setColumnView(0, GENERAL_CELL_WIDTH);
			sheet.mergeCells(0, 0, (NUM_OF_COL-1), 0);
			
			// Page Header
			WritableCellFormat headerCellFormat = this.getJxlCellFormatForReportPageHeader();
			label = new Label(0, 1, yearMonthStr, headerCellFormat);
			sheet.addCell(label);
			sheet.setColumnView(0, GENERAL_CELL_WIDTH);
			sheet.mergeCells(0, 1, (NUM_OF_COL-1), 1);
			
			// Column Title
			List<String> titleList = Arrays.asList(
					"\u6e05\u55ae\u7de8\u865f",
					"\u6e05\u55ae\u65e5\u671f", 
					"\u72c0\u614b", 
					"\u8863\u670d\u7a2e\u985e\u6578\u91cf", 
					"\u7e3d\u6578"
					);
			
			WritableCellFormat colHeaderCellFormat = this.getJxlCellFormatForReportColumnHeader();
			for (int i = 0; i < titleList.size(); i++)
			{
				label = new Label(i, 2, titleList.get(i), colHeaderCellFormat);
				sheet.addCell(label);
				sheet.setColumnView(i, GENERAL_CELL_WIDTH);
			}
			
			// Data
			ReportObject1 ro = null;
			WritableCellFormat dataCellFormat = this.getJxlCellFormatForReportData();
			final int NUM_OF_DATA_ROWS = this.reportList.size();
			for (int i = 0; i < NUM_OF_DATA_ROWS; i++)
			{
				ro = this.reportList.get(i);
				
				label = new Label(0, (i+3), ro.getReceiptCode(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(1, (i+3), ro.getReceiptDate(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(2, (i+3), ro.getReceiptStatus(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(3, (i+3), ro.getClothTypeCounter(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(4, (i+3), ro.getReceiptClothTotal().toString(), dataCellFormat);
				sheet.addCell(label);
			}
			
			// Summary
			int summaryRowStartingIndex = (3+NUM_OF_DATA_ROWS+1);
			
			label = new Label(NUM_OF_COL-2, summaryRowStartingIndex, "\u7e3d\u7d50", colHeaderCellFormat);
			sheet.addCell(label);
			sheet.mergeCells(NUM_OF_COL-2, summaryRowStartingIndex, (NUM_OF_COL-1), summaryRowStartingIndex);
			
			label = new Label(NUM_OF_COL-2, summaryRowStartingIndex+1, this.reportClothTypeTotalStr, dataCellFormat);
			sheet.addCell(label);
			
			label = new Label(NUM_OF_COL-1, summaryRowStartingIndex+1, this.reportClothTotal.toString(), dataCellFormat);
			sheet.addCell(label);
			
			
			workbook.write();
			workbook.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	

	public List<ReportObject1> getReportList()
	{
		return reportList;
	}
	public void setReportList(List<ReportObject1> reportList)
	{
		this.reportList = reportList;
	}
}
