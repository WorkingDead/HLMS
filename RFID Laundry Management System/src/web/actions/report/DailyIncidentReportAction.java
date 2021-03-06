package web.actions.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import module.dao.general.SpecialEvent;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.Staff;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import utils.convertor.DateConverter;
import web.actions.BaseActionReport;

@Results({
	@Result(name="getReportPage", location="daily.incident.report", type="tiles"), 
	@Result(name="input", location="daily.incident.report", type="tiles"),
	
	@Result(name="makeReport",  type="jasper", params={
			"location", 	"/jasper_report/reportDailyIncident.jasper", 
			"dataSource", 	"reportList",
			"format", 		"${format}",
			"documentName",	"Daily_Incident_Report"
	}),
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", "exportReport"}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class DailyIncidentReportAction extends BaseActionReport
{
	private static final long serialVersionUID = 5854852324575959370L;
	private static final Logger log4j = Logger.getLogger(DailyIncidentReportAction.class);
	
	private List<ReportObject3> reportList;
	
	public DailyIncidentReportAction()
	{
		dateFrom = Calendar.getInstance();
	}
	
	public String getReportPage()
	{
		return "getReportPage";
	}
	
	public void validateExportReport()
	{
		if (dateFrom == null)
		{
			addActionError(String.format(getText("errors.custom.required"), getText("label.date")));
		}
		else
		{
//			try
//			{
//				
//			}
//			catch (Exception e)
//			{
//				addActionError(String.format(getText("errors.custom.invalid"), getText("label.date")));
//			}
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
		
		dateFromStr = DateConverter.format(dateFrom, DateConverter.DATE_FORMAT);
		List<SpecialEvent> seList = findSpecialEvent(this.dateFrom);
		this.reportList = new ArrayList<ReportObject3>();
		SpecialEvent se = null;
		ReportObject3 ro = null;
		Staff staff = null;
		Cloth cloth = null;
		ClothType clothType = null;
		String clothTypeName = null;
		Integer reportClothTypeTotal = null;
		for (int i = 0; i < seList.size(); i++)
		{
			se = seList.get(i);
			cloth = se.getCloth();
			clothType = se.getClothType();
			staff = se.getStaff();
			
			ro = new ReportObject3();
			ro.setEventName(getText(se.getSpecialEventName().getValue()));
			ro.setDept(staff.getDept().getNameCht());
			ro.setStaffCode(staff.getCode());
			ro.setStaffName(staff.getNameEng());
			
			if (clothType != null)
			{
				clothTypeName = clothType.getName();
				ro.setClothType(clothTypeName);
				
				if (clothTypeCountMap.containsKey(clothTypeName))
				{
					reportClothTypeTotal = clothTypeCountMap.get(clothTypeName);
					reportClothTypeTotal++;
					clothTypeCountMap.put(clothTypeName, reportClothTypeTotal);
				}
				else
				{
					clothTypeCountMap.put(clothTypeName, 1);
				}
			}
			
			if (cloth != null)
			{
				ro.setClothSize(se.getCloth().getSize());
				ro.setClothCode(se.getCloth().getCode());
			}
			
			this.reportList.add(ro);
		}
		
		reportClothTotal = this.reportList.size();
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
			this.reportList.add(new ReportObject3());
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
			this.response.setHeader("Content-Disposition", "attachment; filename=Daily_Incident_Report.xls");
			
			
			///////////////////////////////////////////////
			// 2. Make Excel
			///////////////////////////////////////////////
			final int NUM_OF_COL = 7;
			
			WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
			WritableSheet sheet = workbook.createSheet(DEFAULT_SHEET_NAME, 0);
			
			// Title
			WritableCellFormat titleCellFormat = this.getJxlCellFormatForReportTitle();
			Label label = new Label(0, 0, "\u6bcf\u65e5\u4e8b\u4ef6\u8ddf\u9032\u5831\u544a", titleCellFormat);
			sheet.addCell(label);
			sheet.setColumnView(0, GENERAL_CELL_WIDTH);
			sheet.mergeCells(0, 0, (NUM_OF_COL-1), 0);
			
			// Page Header
			WritableCellFormat headerCellFormat = this.getJxlCellFormatForReportPageHeader();
			label = new Label(0, 1, dateFromStr, headerCellFormat);
			sheet.addCell(label);
			sheet.setColumnView(0, GENERAL_CELL_WIDTH);
			sheet.mergeCells(0, 1, (NUM_OF_COL-1), 1);
			
			// Column Title
			List<String> titleList = Arrays.asList(
					"\u4e8b\u4ef6",
					"\u90e8\u9580", 
					"\u54e1\u5de5\u7de8\u865f", 
					"\u54e1\u5de5\u59d3\u540d", 
					"\u8863\u7269\u7a2e\u985e", 
					"\u5c3a\u5bf8", 
					"\u8863\u7269\u7de8\u865f"
			);
			
			WritableCellFormat colHeaderCellFormat = this.getJxlCellFormatForReportColumnHeader();
			for (int i = 0; i < titleList.size(); i++)
			{
				label = new Label(i, 2, titleList.get(i), colHeaderCellFormat);
				sheet.addCell(label);
				sheet.setColumnView(i, GENERAL_CELL_WIDTH);
			}
			
			// Data
			ReportObject3 ro3 = null;
			WritableCellFormat dataCellFormat = this.getJxlCellFormatForReportData();
			final int NUM_OF_DATA_ROWS = this.reportList.size();
			for (int i = 0; i < NUM_OF_DATA_ROWS; i++)
			{
				ro3 = this.reportList.get(i);
				
				label = new Label(0, (i+3), ro3.getEventName(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(1, (i+3), ro3.getDept(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(2, (i+3), ro3.getStaffCode(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(3, (i+3), ro3.getStaffName(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(4, (i+3), ro3.getClothType(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(5, (i+3), ro3.getClothSize(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(6, (i+3), ro3.getClothCode(), dataCellFormat);
				sheet.addCell(label);
			}
			
			// Summary
			int summaryRowStartingIndex = (3+NUM_OF_DATA_ROWS+1);
			
			label = new Label(NUM_OF_COL-2, summaryRowStartingIndex, "\u4e8b\u4ef6\u7e3d\u6578", dataCellFormat);
			sheet.addCell(label);
			
			label = new Label(NUM_OF_COL-1, summaryRowStartingIndex, this.reportClothTotal.toString(), dataCellFormat);
			sheet.addCell(label);
//			sheet.mergeCells(NUM_OF_COL-2, summaryRowStartingIndex, (NUM_OF_COL-1), summaryRowStartingIndex);
			
			
			label = new Label(NUM_OF_COL-2, summaryRowStartingIndex+1, "\u4e8b\u4ef6\u8863\u7269\u7e3d\u7d50", dataCellFormat);
			sheet.addCell(label);
			
			label = new Label(NUM_OF_COL-1, summaryRowStartingIndex+1, this.reportClothTypeTotalStr, dataCellFormat);
			sheet.addCell(label);
//			sheet.mergeCells(NUM_OF_COL-2, summaryRowStartingIndex+1, (NUM_OF_COL-1), summaryRowStartingIndex+1);
			
			
			workbook.write();
			workbook.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	

	private List<SpecialEvent> findSpecialEvent(Calendar date)
	{
		Calendar date1 = (Calendar) date.clone();
		date1.set(Calendar.HOUR_OF_DAY, date1.getActualMinimum(Calendar.HOUR_OF_DAY));
		date1.set(Calendar.MINUTE, date1.getActualMinimum(Calendar.MINUTE));
		date1.set(Calendar.SECOND, date1.getActualMinimum(Calendar.SECOND));
		date1.getTime();		//getTime() is necessary
		final Calendar dateStart = date1;
		
		Calendar date2 = (Calendar) date.clone();
		date2.set(Calendar.HOUR_OF_DAY, date2.getActualMaximum(Calendar.HOUR_OF_DAY));
		date2.set(Calendar.MINUTE, date2.getActualMaximum(Calendar.MINUTE));
		date2.set(Calendar.SECOND, date2.getActualMaximum(Calendar.SECOND));
		date2.getTime();		//getTime() is necessary
		final Calendar dateEnd = date2;
		
		List<SpecialEvent> seList = this.getGeneralService().findByExample( SpecialEvent.class, null, null, null,
				new CustomCriteriaHandler<SpecialEvent>()
				{
					@Override
					public void makeCustomCriteria(Criteria criteria)
					{
						criteria.add(Restrictions.ge("creationDate", dateStart) );
						criteria.add(Restrictions.le("creationDate", dateEnd) );
					}
				}, 
				
				new CustomLazyHandler<SpecialEvent>()
				{
					@Override
					public void LazyList(List<SpecialEvent> list)
					{
						SpecialEvent se = null;
						for (int i = 0; i < list.size(); i++)
						{
							se = list.get(i);
							se.getStaff().getDept().getId();
							if (se.getCloth() != null)
								se.getCloth().getClothType().getId();
						}
					}
				}, 
				
				Order.asc("creationDate")
		);
				
		return seList;
	}

	public List<ReportObject3> getReportList()
	{
		return reportList;
	}
	public void setReportList(List<ReportObject3> reportList)
	{
		this.reportList = reportList;
	}
}
