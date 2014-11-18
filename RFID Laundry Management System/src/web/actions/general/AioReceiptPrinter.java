package web.actions.general;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import module.dao.general.Receipt;
import module.dao.general.ReceiptPatternIron;
import module.dao.master.Cloth;
import module.dao.master.Staff;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import utils.convertor.DateConverter;
import web.actions.BaseActionGeneral;
import web.actions.form.HandheldReceiptCollectMainObject;
import web.actions.form.HandheldReceiptCollectSub1Object;
import web.actions.form.HandheldReceiptCollectSub2Object;
import web.actions.form.ReceiptDistMainObject;
import web.actions.form.ReceiptDistSub1Object;
import web.actions.form.ReceiptDistSub2Object;
import web.actions.form.ReceiptIronMainObject;
import web.actions.form.ReceiptIronSub1Object;
import web.actions.form.ReceiptIronSub2Object;
import web.actions.form.ReceiptRackMainObject;
import web.actions.form.ReceiptRackSub1Object;
import web.actions.form.ReceiptRackSub2Object;

public class AioReceiptPrinter extends BaseActionGeneral
{
	private static final long serialVersionUID = 3157110574560513410L;
	private static final Logger log4j = Logger.getLogger(AioReceiptPrinter.class);
	
	// iReport Receipt Printing
	private String subreportPath;		// absolute path of subreport file
	private List mainReportList;
	private List subreport1List;
	private List subreport2List;
	
	public void printCollectReceipt(Receipt receipt, String reportFilePath, String subreportPath) throws Exception
	{
		/////////////////////////////////////////////
		// 1. Preparing receipt data for printing
		/////////////////////////////////////////////
		// iReport setting
		this.subreportPath = subreportPath;
		Map parameters = null;
		String printerName = this.getBeansFactoryApplication().getSystemPrinterA4();
		
		String handledByStaffCode = receipt.getStaffHandledBy().getCode();
		String handledByStaffName = receipt.getStaffHandledBy().getNameEng();
		String receiptCode = receipt.getCode();
		Calendar printDateTime = receipt.getCreationDate();
		String receiptDate = DateConverter.format(printDateTime, DateConverter.DATE_FORMAT);
		String receiptTime = DateConverter.format(printDateTime, DateConverter.HOUR_MINUTE_FORMAT);
		Integer receiptClothTotal = receipt.getReceiptClothTotal();
		String receiptRemark = receipt.getRemark();
		
		// Get all cloths
		ArrayList<Cloth> receiptClothList = new ArrayList<Cloth>(receipt.getClothSet());
		
		// Count each clothType and Summarizing clothes by staff
		Cloth tmpCloth = null;
		String clothTypeName = null;
		Integer clothTypeTotal = null;
		Map<String, Integer> clothTypeCounter = new HashMap<String, Integer>();
		Long staffId = null;
		Map<Long, ArrayList<Cloth>> staffClothListMap = new HashMap<Long, ArrayList<Cloth>>();
		ArrayList<Cloth> staffClothList = null;
		for (int i = 0; i < receiptClothList.size(); i++)
		{
			tmpCloth = receiptClothList.get(i);
			
			// Summarizing clothes by clothType (count each clothType)
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
			
			// Summarizing clothes by staff
			staffId = tmpCloth.getStaff().getId();
			if (staffClothListMap.containsKey(staffId))
			{
				staffClothList = staffClothListMap.get(staffId);
				staffClothList.add(tmpCloth);
			}
			else
			{
				staffClothList = new ArrayList<Cloth>();
				staffClothList.add(tmpCloth);
				staffClothListMap.put(staffId, staffClothList);
			}
		}
		
		// 1.3. Fill sub-report-1 (clothType summary)
		this.subreport1List = new ArrayList<HandheldReceiptCollectSub1Object>();
		ArrayList<String> clothTypeNameList = new ArrayList<String>(clothTypeCounter.keySet());
		Collections.sort(clothTypeNameList);
		HandheldReceiptCollectSub1Object rs1o = null;
		
		for (int j = 0; j < clothTypeNameList.size(); j++)
		{
			clothTypeName = clothTypeNameList.get(j);
			clothTypeTotal = clothTypeCounter.get(clothTypeName);
			
			rs1o = new HandheldReceiptCollectSub1Object();
			rs1o.setClothType(clothTypeName);
			rs1o.setQty(clothTypeTotal);
			this.subreport1List.add(rs1o);
		}
		
		
		// 1.4. Fill sub-report-2 (staff's clothes)
		this.subreport2List = new ArrayList<HandheldReceiptCollectSub2Object>();
		HandheldReceiptCollectSub2Object rs2o = null;
		ArrayList<Long> staffIdList = new ArrayList<Long>(staffClothListMap.keySet());
		Map<String, Integer> staffClothTypeCounter = null;
		Staff staff = null;
		String staffCode = null;
		String staffNameCht = null;
		String staffNameEng = null;
		String staffDept = null;
		Integer staffClothTotal = null;
		String staffClothDetail = null;
		for (int j = 0; j < staffIdList.size(); j++)
		{
			staffId = staffIdList.get(j);
			staffClothList = staffClothListMap.get(staffId);
			
			tmpCloth = staffClothList.get(0);
			staff = tmpCloth.getStaff();
			staffCode = staff.getCode();
			staffNameCht = staff.getNameCht();
			staffNameEng = staff.getNameEng();
			staffDept = staff.getDept().getNameCht();
			staffClothTotal = staffClothList.size();
			
			// count clothType for this staff
			staffClothTypeCounter = new HashMap<String, Integer>();
			for (int k = 0; k < staffClothList.size(); k++)
			{
				tmpCloth = staffClothList.get(k);
				clothTypeName = tmpCloth.getClothType().getName();
				
				if (staffClothTypeCounter.containsKey(clothTypeName))
				{
					clothTypeTotal = staffClothTypeCounter.get(clothTypeName);
					clothTypeTotal++;
					staffClothTypeCounter.put(clothTypeName, clothTypeTotal);
				}
				else
				{
					staffClothTypeCounter.put(clothTypeName, 1);
				}
			}
			
			// Construct the cloth-detail-string for this staff
			clothTypeNameList = new ArrayList<String>(staffClothTypeCounter.keySet());
			Collections.sort(clothTypeNameList);
			staffClothDetail = "";
			for (int k = 0; k < clothTypeNameList.size(); k++)
			{
				clothTypeName = clothTypeNameList.get(k);
				clothTypeTotal = staffClothTypeCounter.get(clothTypeName);
				staffClothDetail += clothTypeName + " x " + clothTypeTotal + "\n";
			}
			
			rs2o = new HandheldReceiptCollectSub2Object();
			rs2o.setStaffDept(staffDept);
			rs2o.setStaffCode(staffCode);
			rs2o.setStaffNameCht(staffNameCht);
			rs2o.setStaffNameEng(staffNameEng);
			rs2o.setClothDetail(staffClothDetail);
			rs2o.setStaffTotal(staffClothTotal);
			this.subreport2List.add(rs2o);
		}
		
		/////////////////////////////////////////////
		// 2. Send to Printer
		/////////////////////////////////////////////
		parameters = new HashMap();
		parameters.put("subreportPath", this.subreportPath);
		parameters.put("receiptCode", receiptCode);
		parameters.put("receiptDate", receiptDate);
		parameters.put("receiptTime", receiptTime);
		parameters.put("receiptClothTotal", receiptClothTotal);
		parameters.put("handleByStaffCode", handledByStaffCode);
		parameters.put("handleByStaffName", handledByStaffName);
		parameters.put("receiptRemark", receiptRemark);
		
		JRBeanCollectionDataSource dataSrc1 = new JRBeanCollectionDataSource(this.subreport1List);
		parameters.put("subreport1List", dataSrc1);
		JRBeanCollectionDataSource dataSrc2 = new JRBeanCollectionDataSource(this.subreport2List);
		parameters.put("subreport2List", dataSrc2);
		
		// add a dummy report-main-obj to defeat JasperReport bug
		this.mainReportList = new ArrayList<HandheldReceiptCollectMainObject>();
		this.mainReportList.add(new HandheldReceiptCollectMainObject());
		
		
		
		
		printUnderWindowDriver(reportFilePath, parameters, this.mainReportList, 1, printerName);
	}
	
	
	public void printIroningReceipt(Receipt receipt, String reportFilePath, String subreportPath) throws Exception
	{
		/////////////////////////////////////////////
		// 1. Preparing receipt data for printing
		/////////////////////////////////////////////
		
		// iReport setting
		this.subreportPath = subreportPath;
		
		Map parameters = null;
		String printerName = this.getBeansFactoryApplication().getSystemPrinterA4();
		
		String handledByUser = this.getUser().getUserDisplayName();
		String receiptCode = receipt.getCode();
		Calendar printDateTime = receipt.getCreationDate();
		String receiptDate = DateConverter.format(printDateTime, DateConverter.DATE_FORMAT);
		String receiptTime = DateConverter.format(printDateTime, DateConverter.HOUR_MINUTE_FORMAT);
		Integer receiptClothTotal = receipt.getReceiptClothTotal();
//		String staffCodeHandledBy = receipt.getStaffHandledBy().getCode();
//		String staffNameHandledBy = receipt.getStaffHandledBy().getNameCht();
		String receiptRemark = receipt.getRemark();
		
		// Get all cloths
		Iterator<ReceiptPatternIron> itPattern = receipt.getReceiptPatternIronSet().iterator();
		ReceiptPatternIron tmpPattern = null;
		ArrayList<Cloth> receiptClothList = new ArrayList<Cloth>();
		while (itPattern.hasNext())
		{
			tmpPattern = itPattern.next();
			receiptClothList.addAll(tmpPattern.getClothSet());
		}
		
		
		// Count each clothType and Summarizing clothes by staff
		Cloth tmpCloth = null;
		String clothTypeName = null;
		Integer clothTypeTotal = null;
		Map<String, Integer> clothTypeCounter = new HashMap<String, Integer>();
		Long staffId = null;
		Map<Long, ArrayList<Cloth>> staffClothListMap = new HashMap<Long, ArrayList<Cloth>>();
		ArrayList<Cloth> staffClothList = null;
		for (int i = 0; i < receiptClothList.size(); i++)
		{
			tmpCloth = receiptClothList.get(i);
			
			// Summarizing clothes by clothType (count each clothType)
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
			
			// Summarizing clothes by staff
			staffId = tmpCloth.getStaff().getId();
			if (staffClothListMap.containsKey(staffId))
			{
				staffClothList = staffClothListMap.get(staffId);
				staffClothList.add(tmpCloth);
			}
			else
			{
				staffClothList = new ArrayList<Cloth>();
				staffClothList.add(tmpCloth);
				staffClothListMap.put(staffId, staffClothList);
			}
		}
		
		// 1.3. Fill sub-report-1 (clothType summary)
		this.subreport1List = new ArrayList<ReceiptIronSub1Object>();
		ArrayList<String> clothTypeNameList = new ArrayList<String>(clothTypeCounter.keySet());
		Collections.sort(clothTypeNameList);
		ReceiptIronSub1Object ris1o = null;
		
		for (int j = 0; j < clothTypeNameList.size(); j++)
		{
			clothTypeName = clothTypeNameList.get(j);
			clothTypeTotal = clothTypeCounter.get(clothTypeName);
			
			ris1o = new ReceiptIronSub1Object();
			ris1o.setClothType(clothTypeName);
			ris1o.setQty(clothTypeTotal);
			this.subreport1List.add(ris1o);
		}
		
		
		// 1.4. Fill sub-report-2 (staff's clothes)
		this.subreport2List = new ArrayList<ReceiptIronSub2Object>();
		ReceiptIronSub2Object ris2o = null;
		ArrayList<Long> staffIdList = new ArrayList<Long>(staffClothListMap.keySet());
		Map<String, Integer> staffClothTypeCounter = null;
		Staff staff = null;
		String staffCode = null;
		String staffNameCht = null;
		String staffNameEng = null;
		String staffDept = null;
		Integer staffClothTotal = null;
		String staffClothDetail = null;
		for (int j = 0; j < staffIdList.size(); j++)
		{
			staffId = staffIdList.get(j);
			staffClothList = staffClothListMap.get(staffId);
			
			tmpCloth = staffClothList.get(0);
			staff = tmpCloth.getStaff();
			staffCode = staff.getCode();
			staffNameCht = staff.getNameCht();
			staffNameEng = staff.getNameEng();
			staffDept = staff.getDept().getNameCht();
			staffClothTotal = staffClothList.size();
			
			// count clothType for this staff
			staffClothTypeCounter = new HashMap<String, Integer>();
			for (int k = 0; k < staffClothList.size(); k++)
			{
				tmpCloth = staffClothList.get(k);
				clothTypeName = tmpCloth.getClothType().getName();
				
				if (staffClothTypeCounter.containsKey(clothTypeName))
				{
					clothTypeTotal = staffClothTypeCounter.get(clothTypeName);
					clothTypeTotal++;
					staffClothTypeCounter.put(clothTypeName, clothTypeTotal);
				}
				else
				{
					staffClothTypeCounter.put(clothTypeName, 1);
				}
			}
			
			// Construct the cloth-detail-string for this staff
			clothTypeNameList = new ArrayList<String>(staffClothTypeCounter.keySet());
			Collections.sort(clothTypeNameList);
			staffClothDetail = "";
			for (int k = 0; k < clothTypeNameList.size(); k++)
			{
				clothTypeName = clothTypeNameList.get(k);
				clothTypeTotal = staffClothTypeCounter.get(clothTypeName);
				staffClothDetail += clothTypeName + " x " + clothTypeTotal + "\n";
			}
			
			ris2o = new ReceiptIronSub2Object();
			ris2o.setStaffDept(staffDept);
			ris2o.setStaffCode(staffCode);
			ris2o.setStaffNameCht(staffNameCht);
			ris2o.setStaffNameEng(staffNameEng);
			ris2o.setClothDetail(staffClothDetail);
			ris2o.setStaffTotal(staffClothTotal);
			this.subreport2List.add(ris2o);
		}
		
		
		/////////////////////////////////////////////
		// 2. Send to Printer
		/////////////////////////////////////////////
		parameters = new HashMap();
		parameters.put("subreportPath", this.subreportPath);
		parameters.put("receiptCode", receiptCode);
		parameters.put("receiptDate", receiptDate);
		parameters.put("receiptTime", receiptTime);
		parameters.put("receiptClothTotal", receiptClothTotal);
		parameters.put("handledByUser", handledByUser);
		parameters.put("receiptRemark", receiptRemark);
		
		JRBeanCollectionDataSource dataSrc1 = new JRBeanCollectionDataSource(this.subreport1List);
		parameters.put("subreport1List", dataSrc1);
		JRBeanCollectionDataSource dataSrc2 = new JRBeanCollectionDataSource(this.subreport2List);
		parameters.put("subreport2List", dataSrc2);
		
		// add a dummy report-main-obj to defeat JasperReport bug
		this.mainReportList = new ArrayList<ReceiptIronMainObject>();
		this.mainReportList.add(new ReceiptIronMainObject());
		
		
		
		printUnderWindowDriver(reportFilePath, parameters, this.mainReportList, 1, printerName);
	}
	
	
	public void printRackingReceipt(Receipt receipt, String reportFilePath, String subreportPath) throws Exception
	{
		/////////////////////////////////////////////
		// 1. Preparing receipt data for printing
		/////////////////////////////////////////////
		
		// iReport setting
		this.subreportPath = subreportPath;
		Map parameters = null;
		String printerName = this.getBeansFactoryApplication().getSystemPrinterA4();
		
		String handledByUser = this.getUser().getUserDisplayName();
		String receiptCode = receipt.getCode();
		Calendar printDateTime = receipt.getCreationDate();
		String receiptDate = DateConverter.format(printDateTime, DateConverter.DATE_FORMAT);
		String receiptTime = DateConverter.format(printDateTime, DateConverter.HOUR_MINUTE_FORMAT);
		Integer receiptClothTotal = receipt.getReceiptClothTotal();
		String receiptRemark = receipt.getRemark();
		
		// Get all cloths
		ArrayList<Cloth> receiptClothList = new ArrayList<Cloth>(receipt.getClothSet());
		
		// Count each clothType and Summarizing clothes by staff
		Cloth tmpCloth = null;
		String clothTypeName = null;
		Integer clothTypeTotal = null;
		Map<String, Integer> clothTypeCounter = new HashMap<String, Integer>();
		Long staffId = null;
		Map<Long, ArrayList<Cloth>> staffClothListMap = new HashMap<Long, ArrayList<Cloth>>();
		ArrayList<Cloth> staffClothList = null;
		for (int i = 0; i < receiptClothList.size(); i++)
		{
			tmpCloth = receiptClothList.get(i);
			
			// Summarizing clothes by clothType (count each clothType)
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
			
			// Summarizing clothes by staff
			staffId = tmpCloth.getStaff().getId();
			if (staffClothListMap.containsKey(staffId))
			{
				staffClothList = staffClothListMap.get(staffId);
				staffClothList.add(tmpCloth);
			}
			else
			{
				staffClothList = new ArrayList<Cloth>();
				staffClothList.add(tmpCloth);
				staffClothListMap.put(staffId, staffClothList);
			}
		}
		
		// 1.3. Fill sub-report-1 (clothType summary)
		this.subreport1List = new ArrayList<ReceiptRackSub1Object>();
		ArrayList<String> clothTypeNameList = new ArrayList<String>(clothTypeCounter.keySet());
		Collections.sort(clothTypeNameList);
		ReceiptRackSub1Object rs1o = null;
		
		for (int j = 0; j < clothTypeNameList.size(); j++)
		{
			clothTypeName = clothTypeNameList.get(j);
			clothTypeTotal = clothTypeCounter.get(clothTypeName);
			
			rs1o = new ReceiptRackSub1Object();
			rs1o.setClothType(clothTypeName);
			rs1o.setQty(clothTypeTotal);
			this.subreport1List.add(rs1o);
		}
		
		
		// 1.4. Fill sub-report-2 (staff's clothes)
		this.subreport2List = new ArrayList<ReceiptRackSub2Object>();
		ReceiptRackSub2Object rs2o = null;
		ArrayList<Long> staffIdList = new ArrayList<Long>(staffClothListMap.keySet());
		Map<String, Integer> staffClothTypeCounter = null;
		Staff staff = null;
		String staffCode = null;
		String staffNameCht = null;
		String staffNameEng = null;
		String staffDept = null;
		Integer staffClothTotal = null;
		String staffClothDetail = null;
		for (int j = 0; j < staffIdList.size(); j++)
		{
			staffId = staffIdList.get(j);
			staffClothList = staffClothListMap.get(staffId);
			
			tmpCloth = staffClothList.get(0);
			staff = tmpCloth.getStaff();
			staffCode = staff.getCode();
			staffNameCht = staff.getNameCht();
			staffNameEng = staff.getNameEng();
			staffDept = staff.getDept().getNameCht();
			staffClothTotal = staffClothList.size();
			
			// count clothType for this staff
			staffClothTypeCounter = new HashMap<String, Integer>();
			for (int k = 0; k < staffClothList.size(); k++)
			{
				tmpCloth = staffClothList.get(k);
				clothTypeName = tmpCloth.getClothType().getName();
				
				if (staffClothTypeCounter.containsKey(clothTypeName))
				{
					clothTypeTotal = staffClothTypeCounter.get(clothTypeName);
					clothTypeTotal++;
					staffClothTypeCounter.put(clothTypeName, clothTypeTotal);
				}
				else
				{
					staffClothTypeCounter.put(clothTypeName, 1);
				}
			}
			
			// Construct the cloth-detail-string for this staff
			clothTypeNameList = new ArrayList<String>(staffClothTypeCounter.keySet());
			Collections.sort(clothTypeNameList);
			staffClothDetail = "";
			for (int k = 0; k < clothTypeNameList.size(); k++)
			{
				clothTypeName = clothTypeNameList.get(k);
				clothTypeTotal = staffClothTypeCounter.get(clothTypeName);
				staffClothDetail += clothTypeName + " x " + clothTypeTotal + "\n";
			}
			
			rs2o = new ReceiptRackSub2Object();
			rs2o.setStaffDept(staffDept);
			rs2o.setStaffCode(staffCode);
			rs2o.setStaffNameCht(staffNameCht);
			rs2o.setStaffNameEng(staffNameEng);
			rs2o.setClothDetail(staffClothDetail);
			rs2o.setStaffTotal(staffClothTotal);
			this.subreport2List.add(rs2o);
		}
		
		
		/////////////////////////////////////////////
		// 2. Send to Printer
		/////////////////////////////////////////////
		parameters = new HashMap();
		parameters.put("subreportPath", this.subreportPath);
		parameters.put("receiptCode", receiptCode);
		parameters.put("receiptDate", receiptDate);
		parameters.put("receiptTime", receiptTime);
		parameters.put("receiptClothTotal", receiptClothTotal);
		parameters.put("handledByUser", handledByUser);
		parameters.put("receiptRemark", receiptRemark);
		
		JRBeanCollectionDataSource dataSrc1 = new JRBeanCollectionDataSource(this.subreport1List);
		parameters.put("subreport1List", dataSrc1);
		JRBeanCollectionDataSource dataSrc2 = new JRBeanCollectionDataSource(this.subreport2List);
		parameters.put("subreport2List", dataSrc2);
		
		// add a dummy report-main-obj to defeat JasperReport bug
		this.mainReportList = new ArrayList<ReceiptRackMainObject>();
		this.mainReportList.add(new ReceiptRackMainObject());
		
		
		
		
		
		printUnderWindowDriver(reportFilePath, parameters, this.mainReportList, 1, printerName);
	}
	
	public void printDistReceipt(Receipt receipt, String reportFilePath, String subreportPath) throws Exception
	{
		/////////////////////////////////////////////
		// 1. Preparing receipt data for printing
		/////////////////////////////////////////////
		
		// iReport setting
		this.subreportPath = subreportPath;
		Map parameters = null;
		
		String printerName = this.getBeansFactoryApplication().getSystemPrinterA4();
		String receiptCode = receipt.getCode();
		Integer receiptClothTotal = receipt.getReceiptClothTotal();
		Calendar printDateTime = receipt.getCreationDate();
		String receiptDate = DateConverter.format(printDateTime, DateConverter.DATE_FORMAT);
		String receiptTime = DateConverter.format(printDateTime, DateConverter.HOUR_MINUTE_FORMAT);
		String handleByStaffCode = receipt.getStaffHandledBy().getCode();
		String handleByStaffName = receipt.getStaffHandledBy().getNameEng();
		String pickedByStaffCode = receipt.getStaffPickedBy().getCode();
		String pickedByStaffName = receipt.getStaffPickedBy().getNameEng();
		String receiptRemark = receipt.getRemark();
		
		
		// Get all cloths
		ArrayList<Cloth> receiptClothList = new ArrayList<Cloth>(receipt.getClothSet());
		
		// Count each clothType and Summarizing clothes by staff
		Cloth tmpCloth = null;
		String clothTypeName = null;
		Integer clothTypeTotal = null;
		Map<String, Integer> clothTypeCounter = new HashMap<String, Integer>();
		Long staffId = null;
		Map<Long, ArrayList<Cloth>> staffClothListMap = new HashMap<Long, ArrayList<Cloth>>();
		ArrayList<Cloth> staffClothList = null;
		for (int i = 0; i < receiptClothList.size(); i++)
		{
			tmpCloth = receiptClothList.get(i);
			
			// Summarizing clothes by clothType (count each clothType)
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
			
			// Summarizing clothes by staff
			staffId = tmpCloth.getStaff().getId();
			if (staffClothListMap.containsKey(staffId))
			{
				staffClothList = staffClothListMap.get(staffId);
				staffClothList.add(tmpCloth);
			}
			else
			{
				staffClothList = new ArrayList<Cloth>();
				staffClothList.add(tmpCloth);
				staffClothListMap.put(staffId, staffClothList);
			}
		}
		
		
		// 1.3. Fill sub-report-1 (clothType summary)
		this.subreport1List = new ArrayList<ReceiptDistSub1Object>();
		ArrayList<String> clothTypeNameList = new ArrayList<String>(clothTypeCounter.keySet());
		Collections.sort(clothTypeNameList);
		ReceiptDistSub1Object rs1o = null;
		
		for (int j = 0; j < clothTypeNameList.size(); j++)
		{
			clothTypeName = clothTypeNameList.get(j);
			clothTypeTotal = clothTypeCounter.get(clothTypeName);
			
			rs1o = new ReceiptDistSub1Object();
			rs1o.setClothType(clothTypeName);
			rs1o.setQty(clothTypeTotal);
			this.subreport1List.add(rs1o);
		}
		
		
		// 1.4. Fill sub-report-2 (staff's clothes)
		this.subreport2List = new ArrayList<ReceiptDistSub2Object>();
		ReceiptDistSub2Object rs2o = null;
		ArrayList<Long> staffIdList = new ArrayList<Long>(staffClothListMap.keySet());
		Map<String, Integer> staffClothTypeCounter = null;
		Staff staff = null;
		String staffCode = null;
		String staffNameCht = null;
		String staffNameEng = null;
		String staffDept = null;
		Integer staffClothTotal = null;
		String staffClothDetail = null;
		for (int j = 0; j < staffIdList.size(); j++)
		{
			staffId = staffIdList.get(j);
			staffClothList = staffClothListMap.get(staffId);
			
			tmpCloth = staffClothList.get(0);
			staff = tmpCloth.getStaff();
			staffCode = staff.getCode();
			staffNameCht = staff.getNameCht();
			staffNameEng = staff.getNameEng();
			staffDept = staff.getDept().getNameCht();
			staffClothTotal = staffClothList.size();
			
			// count clothType for this staff
			staffClothTypeCounter = new HashMap<String, Integer>();
			for (int k = 0; k < staffClothList.size(); k++)
			{
				tmpCloth = staffClothList.get(k);
				clothTypeName = tmpCloth.getClothType().getName();
				
				if (staffClothTypeCounter.containsKey(clothTypeName))
				{
					clothTypeTotal = staffClothTypeCounter.get(clothTypeName);
					clothTypeTotal++;
					staffClothTypeCounter.put(clothTypeName, clothTypeTotal);
				}
				else
				{
					staffClothTypeCounter.put(clothTypeName, 1);
				}
			}
			
			// Construct the cloth-detail-string for this staff
			clothTypeNameList = new ArrayList<String>(staffClothTypeCounter.keySet());
			Collections.sort(clothTypeNameList);
			staffClothDetail = "";
			for (int k = 0; k < clothTypeNameList.size(); k++)
			{
				clothTypeName = clothTypeNameList.get(k);
				clothTypeTotal = staffClothTypeCounter.get(clothTypeName);
				staffClothDetail += clothTypeName + " x " + clothTypeTotal + "\n";
			}
			
			rs2o = new ReceiptDistSub2Object();
			rs2o.setStaffDept(staffDept);
			rs2o.setStaffCode(staffCode);
			rs2o.setStaffNameCht(staffNameCht);
			rs2o.setStaffNameEng(staffNameEng);
			rs2o.setClothDetail(staffClothDetail);
			rs2o.setStaffTotal(staffClothTotal);
			this.subreport2List.add(rs2o);
		}
		
		
		
		

			
			
			
			
		// ///////////////////////////////////////////
		// 2. Send to Printer
		// ///////////////////////////////////////////
		parameters = new HashMap();
		parameters.put("subreportPath", this.subreportPath);
		parameters.put("receiptCode", receiptCode);
		parameters.put("receiptClothTotal", receiptClothTotal);
		parameters.put("receiptDate", receiptDate);
		parameters.put("receiptTime", receiptTime);
		parameters.put("handleByStaffCode", handleByStaffCode);
		parameters.put("handleByStaffName", handleByStaffName);
		parameters.put("pickedByStaffCode", pickedByStaffCode);
		parameters.put("pickedByStaffName", pickedByStaffName);
		parameters.put("receiptRemark", receiptRemark);


		JRBeanCollectionDataSource dataSrc1 = new JRBeanCollectionDataSource(this.subreport1List);
		parameters.put("subreport1List", dataSrc1);
		JRBeanCollectionDataSource dataSrc2 = new JRBeanCollectionDataSource(this.subreport2List);
		parameters.put("subreport2List", dataSrc2);

		// add a dummy report-main-obj to defeat JasperReport bug
		this.mainReportList = new ArrayList<ReceiptDistMainObject>();
		this.mainReportList.add(new ReceiptDistMainObject());

		
		
		printUnderWindowDriver(reportFilePath, parameters, this.mainReportList, 1, printerName);

	}
	
	
	public String getSubreportPath()
	{
		return subreportPath;
	}
	public void setSubreportPath(String subreportPath)
	{
		this.subreportPath = subreportPath;
	}
	public List getMainReportList()
	{
		return mainReportList;
	}
	public void setMainReportList(List mainReportList)
	{
		this.mainReportList = mainReportList;
	}
	public List getSubreport1List()
	{
		return subreport1List;
	}
	public void setSubreport1List(List subreport1List)
	{
		this.subreport1List = subreport1List;
	}
	public List getSubreport2List()
	{
		return subreport2List;
	}
	public void setSubreport2List(List subreport2List)
	{
		this.subreport2List = subreport2List;
	}
}
