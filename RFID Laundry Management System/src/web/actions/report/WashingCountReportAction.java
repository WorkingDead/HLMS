package web.actions.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.Department;
import module.dao.master.Staff;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import web.actions.BaseActionReport;

@Results({
	@Result(name="getReportPage", location="washing.count.report", type="tiles"), 
	@Result(name="input", location="washing.count.report", type="tiles"),
	@Result(name="getClothCount", type="json", params={"root", "numOfFound"}),
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", "exportReport"}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class WashingCountReportAction extends BaseActionReport
{
	private static final long serialVersionUID = 5854852324575959370L;
	private static final Logger log4j = Logger.getLogger(WashingCountReportAction.class);
	private static final int FETCH_INTERVAL = 5000;
	
	public static enum WashCountRange
	{
		GE0("wash.count.ge0", 0),
		GE50("wash.count.ge50", 50),
		GE60("wash.count.ge60", 60),
		GE70("wash.count.ge70", 70),
		GE80("wash.count.ge80", 80),
		GE90("wash.count.ge90", 90),
		GE99("wash.count.ge99", 99);
		
		private String txt;
		private Integer value;

		WashCountRange(String txt, Integer value)
		{
			this.txt = txt;
			this.value = value;
		}
		public String getTxt()
		{
			return txt;
		}
		public void setTxt(String txt)
		{
			this.txt = txt;
		}
		public Integer getValue()
		{
			return value;
		}
		public void setValue(Integer value)
		{
			this.value = value;
		}
	}
	
	private List<Department> deptList;
	private Department dept;
	
	private List<WashCountRange> washCountRangeList;
	private Integer washCountRange;
	
	Integer numOfFound;
	List<ReportObject4> reportList;
	
	public String getReportPage()
	{
		deptList = this.getMasterService().findAll(Department.class, null, null, null, Order.asc("nameEng"));
		washCountRangeList = Arrays.asList(WashCountRange.values());
		
		return "getReportPage";
	}
	
	public String getClothCount()
	{
		CustomCriteriaHandler<Cloth> criteriaHandler = this.createCriteriaHandler();
		this.numOfFound = this.getMasterService().totalByExample(Cloth.class, null, criteriaHandler);
		
		return "getClothCount";
	}
	
	
	public void validateExportReport()
	{
		// Nothing
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
		
		return null;
	}
	
	private CustomCriteriaHandler<Cloth> createCriteriaHandler()
	{
		CustomCriteriaHandler<Cloth> criteriaHandler = new CustomCriteriaHandler<Cloth>() {
				@Override
				public void makeCustomCriteria(Criteria criteria)
				{
					if (washCountRange <= 0)
					{
						Disjunction orCases = Restrictions.disjunction();
						orCases.add(Restrictions.isNull("washingCount"));
						orCases.add(Restrictions.ge("washingCount", washCountRange));
						criteria.add(orCases);
					}
					else
					{
						criteria.add(Restrictions.ge("washingCount", washCountRange));
					}
					
					if (dept != null && dept.getId() != null)
					{
						Criteria staffCriteria = criteria.createCriteria("staff");
						Criteria deptCriteria = staffCriteria.createCriteria("dept");
						deptCriteria.add(Restrictions.eq("id", dept.getId()));
					}
				}
		};
		
		return criteriaHandler;
	}
	
	private CustomLazyHandler<Cloth> createLazyHandler()
	{
		CustomLazyHandler lazyHandler = new CustomLazyHandler<Cloth>() {
			
				@Override
				public void LazyList(List<Cloth> list)
				{
					Iterator<Cloth> itCloth = list.iterator();
					Cloth tmpCloth = null;
					ClothType tmpClothType = null;
					Staff tmpStaff = null;
					Department tmpDept = null;
					while (itCloth.hasNext())
					{
						tmpCloth = itCloth.next();
						if (tmpCloth.getClothType() != null)
						{
							tmpCloth.getClothType().getId();
						}
						
						if (tmpCloth.getStaff() != null)
						{
							tmpCloth.getStaff().getId();
							
							if (tmpCloth.getStaff().getDept() != null)
							{
								tmpCloth.getStaff().getDept().getId();
							}
						}
					}
				}
		};
		
		return lazyHandler;
	}
	
	public String exportImpl() throws Exception
	{
		CustomCriteriaHandler<Cloth> criteriaHandler = this.createCriteriaHandler();
		CustomLazyHandler<Cloth> lazyHandler = this.createLazyHandler();
		
		Integer numOfFound = this.getMasterService().totalByExample(Cloth.class, null, criteriaHandler);
//		System.out.println("numOfFound: " + numOfFound);
		
		reportList = new ArrayList<ReportObject4>();
		List<Cloth> clothList = null;
		Cloth cloth = null;
		ReportObject4 ro = null;
		
		final int LOOP = numOfFound / FETCH_INTERVAL;
//		final int REMAINDER = numOfFound % FETCH_INTERVAL;
		int t1 = -1;
//		int t2 = -1;
		for (int i = 0; i < (LOOP+1); i++)
		{
			t1 = i * FETCH_INTERVAL;
//			t2 = t1 + FETCH_INTERVAL - 1;
			
			clothList = this.getMasterService().findByExample(Cloth.class, null, t1, FETCH_INTERVAL, criteriaHandler, lazyHandler, Order.asc("id"));
//			System.out.println("Loop: " + i);
//			System.out.println("Start Postion: " + t1);
//			System.out.println("found: " + clothList.size());
			for (int j = 0; j < clothList.size(); j++)
			{
				cloth = clothList.get(j);
				ro = new ReportObject4();
				ro.setClothType(cloth.getClothType().getName());
				ro.setCode(cloth.getCode());
				ro.setRfid(cloth.getRfid());
				ro.setStaffCode(cloth.getStaff().getCode());
				
				ro.setStaffNameCht(cloth.getStaff().getNameCht());
				ro.setStaffNameEng(cloth.getStaff().getNameEng());
				ro.setStaffDept(cloth.getStaff().getDept().getNameCht());
				ro.setWashCount(cloth.getWashingCount());
				
				reportList.add(ro);
			}
		}
		
		exportExcel();
		return null;
	}
	
	private String exportExcel()
	{
		try
		{
			///////////////////////////////////////////////
			// 1. Set return an Excel to browser
			///////////////////////////////////////////////
			this.response.setContentType("application/vnd.ms-excel");
			this.response.setHeader("Content-Disposition", "attachment; filename=Washing_Count_Report.xls");

			///////////////////////////////////////////////
			// 2. Make Excel
			///////////////////////////////////////////////
			final int NUM_OF_COL = 8;
			WritableWorkbook wb = Workbook.createWorkbook(response.getOutputStream());
			WritableSheet sheet = wb.createSheet(DEFAULT_SHEET_NAME, 0);
			
			// Title
			WritableCellFormat titleCellFormat = this.getJxlCellFormatForReportTitle();
			Label label = new Label(0, 0, "\u6d17\u6ecc\u6b21\u6578\u5831\u8868", titleCellFormat);
			sheet.addCell(label);
			sheet.setColumnView(0, GENERAL_CELL_WIDTH);
			sheet.mergeCells(0, 0, (NUM_OF_COL-1), 0);
			
			// Column Title
			List<String> titleList = Arrays.asList(
					"\u54e1\u5de5\u90e8\u9580",				// dept
					"\u54e1\u5de5\u7de8\u865f", 			// staff code
					"\u54e1\u5de5\u59d3\u540d(\u4e2d)", 	// staff nameCht
					"\u54e1\u5de5\u59d3\u540d(\u82f1)", 	// staff nameEng
					"\u8863\u7269\u985e\u5225", 			// cloth type
					"\u8863\u7269\u7de8\u865f", 			// cloth code
					"RFID",
					"\u6d17\u6ecc\u6b21\u6578"				// washing count
			);
			List<Integer> columnWidth = Arrays.asList(25, 15, 15, 15, 30, 15, 30, 15);
			
			WritableCellFormat colHeaderCellFormat = this.getJxlCellFormatForReportColumnHeader();
			for (int i = 0; i < titleList.size(); i++)
			{
				label = new Label(i, 2, titleList.get(i), colHeaderCellFormat);
				sheet.addCell(label);
				sheet.setColumnView(i, columnWidth.get(i));
			}
			
			// Data
			ReportObject4 ro = null;
			WritableCellFormat dataCellFormat = this.getJxlCellFormatForReportData();
			final int NUM_OF_DATA_ROWS = this.reportList.size();
			WritableCellFormat ncf = new WritableCellFormat(new NumberFormat("0"));
			jxl.write.Number num = null;
			for (int i = 0; i < NUM_OF_DATA_ROWS; i++)
			{
				ro = this.reportList.get(i);
				
				label = new Label(0, (i+3), ro.getStaffDept(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(1, (i+3), ro.getStaffCode(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(2, (i+3), ro.getStaffNameCht(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(3, (i+3), ro.getStaffNameEng(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(4, (i+3), ro.getClothType(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(5, (i+3), ro.getCode(), dataCellFormat);
				sheet.addCell(label);
				
				label = new Label(6, (i+3), ro.getRfid(), dataCellFormat);
				sheet.addCell(label);
				
				if (ro.getWashCount() != null)
				{
					num = new jxl.write.Number(7, (i+3), ro.getWashCount().intValue(), ncf);
					sheet.addCell(num);
				}
			}
			
			wb.write();
			wb.close();
		}
		catch (Exception e)
		{
			log4j.error( e );
		}
		
		return null;
	}

	
	public List<Department> getDeptList()
	{
		return deptList;
	}
	public void setDeptList(List<Department> deptList)
	{
		this.deptList = deptList;
	}
	public Department getDept()
	{
		return dept;
	}
	public void setDept(Department dept)
	{
		this.dept = dept;
	}
	public List<WashCountRange> getWashCountRangeList()
	{
		return washCountRangeList;
	}
	public void setWashCountRangeList(List<WashCountRange> washCountRangeList)
	{
		this.washCountRangeList = washCountRangeList;
	}
	public Integer getWashCountRange()
	{
		return washCountRange;
	}
	public void setWashCountRange(Integer washCountRange)
	{
		this.washCountRange = washCountRange;
	}
	public Integer getNumOfFound()
	{
		return numOfFound;
	}
	public void setNumOfFound(Integer numOfFound)
	{
		this.numOfFound = numOfFound;
	}
	public List<ReportObject4> getReportList()
	{
		return reportList;
	}
	public void setReportList(List<ReportObject4> reportList)
	{
		this.reportList = reportList;
	}
}