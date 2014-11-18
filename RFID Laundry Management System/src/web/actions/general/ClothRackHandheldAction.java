package web.actions.general;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import module.ale.handheld.HandheldHandlerFactory;
import module.ale.handheld.handler.ClothRackingHandler;
import module.dao.general.HistoryCloth;
import module.dao.general.Receipt;
import module.dao.general.Receipt.ReceiptStatus;
import module.dao.general.Receipt.ReceiptType;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.Staff;
import module.dao.master.Cloth.ClothStatus;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
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
import utils.convertor.DateConverter;
import utils.spring.SpringUtils;
import web.actions.BaseActionGeneral;
import web.actions.form.ClothTypeCounter;
import web.actions.form.ReceiptRackMainObject;
import web.actions.form.ReceiptRackSub1Object;
import web.actions.form.ReceiptRackSub2Object;

@Results({
//	@Result(name="checkPageMaster", type="json", params = {
//			"includeProperties" , 	"masterIsMe"
//	}),

	@Result(name="getClothInfoJson", type="json", params = {
			"includeProperties" , 	"clothList\\[\\d+\\]\\.code, " +
					"clothList\\[\\d+\\]\\.rfid, " +
					"clothList\\[\\d+\\]\\.clothStatus, " +
					"clothList\\[\\d+\\]\\.lastReceiptCode, " +
					"clothList\\[\\d+\\]\\.clothType\\.name, " + 
					"clothList\\[\\d+\\]\\.zone\\.code, " + 
					"clothList\\[\\d+\\]\\.staff\\.code, " + 
					"clothList\\[\\d+\\]\\.staff\\.nameCht, " +
					"clothList\\[\\d+\\]\\.staff\\.nameEng, " + 
					"clothList\\[\\d+\\]\\.staff\\.dept\\.nameCht, " +
					"clothList\\[\\d+\\]\\.staff\\.dept\\.nameEng, " +
					"receiptClothTotal, " + 
					"clothTypeCountList\\[\\d+\\]\\.type, " + 
					"clothTypeCountList\\[\\d+\\]\\.num"
	}),

	@Result(name="ajaxRemoveRfid", type="json", params = {
			"includeProperties" , 	"receiptClothTotal, " +
									"clothTypeCountList\\[\\d+\\]\\.type, " + 
									"clothTypeCountList\\[\\d+\\]\\.num"
	}),
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", "create"}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class ClothRackHandheldAction extends BaseActionGeneral
{
	private static final long serialVersionUID = -7995531412514215979L;
	private static final Logger log4j = Logger.getLogger(ClothRackHandheldAction.class);
	
	// Session
	private static final String SESSION_KEY_CLOTHTYPE_COUNT_MAP = "SESSION_KEY_CLOTHTYPE_COUNT_MAP";
	
	private Integer receiptClothTotal;
	private List<String> rfidToBeRemovedList;
	private List<ClothTypeCounter> clothTypeCountList;
	private List<Cloth> clothList;
	private Receipt receipt;

	// Handheld Handler
	private ClothRackingHandler clothRackingHandler;
	
	// iReport Receipt Printing
	private static final String JASPER_RECEIPT_RACK = "jasper_report/receiptRack.jasper";
	private String subreportPath;		// absolute path of subreport file
	private List<ReceiptRackMainObject> mainReportList;
	private List<ReceiptRackSub1Object> subreport1List;
	private List<ReceiptRackSub2Object> subreport2List;
	
	public String getMainPage()
	{
//		if ( !isPageAvailable() )
//		{
//			this.setTilesKey("cloth.racking.page.in.use");
//			return TILES;
//		}
		
		//System.out.println( "Get Rack Page!" );
		this.resetSessionVariables();
		
		String receiptCode = this.getGeneralService().genClothRackingReceiptCode();
		receipt = new Receipt();
		receipt.setCode(receiptCode);
		
		
		// clothes read by Handheld but already "real-time-appended-to-table" before
		this.clothList = new ArrayList<Cloth>();
		this.clothList.addAll(this.getClothRackingHandler().getMapRfidClothOld().values());
		this.receiptClothTotal = this.clothList.size();
		////////////////////////////////////////////////
		// Update clothTypeCountMap
		////////////////////////////////////////////////
		try
		{
			this.clothTypeCountList = this.updateClothTypeCountMap(this.clothList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		this.setTilesKey("cloth.racking.main");
		return TILES;
	}
	
	public void validateCreate()
	{
//		if ( !isActionValid() )
//		{
//			addActionError(getText("errors.page.no.longer.valid"));
//		}
//		else
//		{
			// check cloth list
			Map<String, Cloth> mapRfidClothAll = this.getClothRackingHandler().getMapRfidClothAll();
			if (mapRfidClothAll == null || mapRfidClothAll.size() == 0)
			{
				addActionError(getText("errors.no.cloth.found"));
			}
//		}
	}
	
	
	public String create()
	{
		//////////////////////////////////////////////
		// 1. Save receipt and transaction
		//////////////////////////////////////////////
		try
		{
			// 1. Save receipt and transactions
			createImpl();
			
			// 2. Print the receipt
//			this.printReceipt(this.receipt);
			
			// 3. Update receipt status if need 
			this.getReceiptStatusSchedulerAsyncWorker().executeAsync();
			
			// 4. Update special event status
			this.getSpecialEventIronSchedulerAsyncWorker().executeAsync();
			
			addActionMessage( getText( SuccessMessage_SaveSuccess ) );
			log4j.info( getText( SuccessMessage_SaveSuccess ) );
		}
		catch (Exception e)
		{
			log4j.error( e );
			
			while ( true )
			{
				Exception cause = (Exception)e.getCause();
				if ( cause == null )
				{
					addActionError( getText (ErrorMessage_SaveFail, new String[]{e.getMessage()} ) );
					e.printStackTrace();
					log4j.error( getText( ErrorMessage_SaveFail, new String[]{e.getMessage()} ) );
					break;
				}
				else
				{
					e = cause;
				}
			}
		}
		
		return "jsonValidateResult";
	}
	
	public void createImpl() throws Exception
	{
		Map<String, Cloth> mapRfidClothAll = this.getClothRackingHandler().getMapRfidClothAll();
		HashSet<Cloth> clothSet = new HashSet<Cloth>(mapRfidClothAll.values());
		Iterator<Cloth> itCloth = clothSet.iterator();
		
		while (itCloth.hasNext())
		{
			Cloth cloth = itCloth.next();
			cloth.setClothStatus(ClothStatus.Ready);
			cloth.setLastReceiptCode(receipt.getCode());
//			cloth.setModifiedBy(systemUser);		// this step is auto-done
		}
		
		receipt.setReceiptClothTotal(mapRfidClothAll.size());
		receipt.setReceiptType(ReceiptType.Rack);
		receipt.setReceiptStatus(ReceiptStatus.Finished);
		receipt.setClothSet( clothSet );
		receipt.setStaffHandledBy(null);
		receipt.setStaffPickedBy(null);
		receipt.setFinishDate(Calendar.getInstance());
		
		
		//////////////////////////////////////////////
		// Save receipt and transaction
		//////////////////////////////////////////////
		this.getGeneralService().saveReceiptAndTransaction(receipt);
		this.deleteHistoryClothFromReceipt(mapRfidClothAll.keySet());	// keySet are RFIDs
		this.resetReceipt();
	}
	
	private void printReceipt(Receipt receipt) throws Exception
	{
		/////////////////////////////////////////////
		// 1. Preparing receipt data for printing
		/////////////////////////////////////////////
		
		// iReport setting
		String reportFilePath = this.getRealPath() + JASPER_RECEIPT_RACK;
		this.subreportPath = this.getRealPath() + JASPER_FOLDER;
		Map parameters = null;
		String printerName = this.getBeansFactoryApplication().getSystemPrinterA4();
		
		String handledByUser = this.getUser().getUserDisplayName();
		String receiptCode = receipt.getCode();
		Calendar printDateTime = Calendar.getInstance();
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
		
		try
		{
			printUnderWindowDriver(reportFilePath, parameters, this.mainReportList, 1, printerName);
		}
		catch (Exception e)
		{
			log4j.error( e );
			e.printStackTrace();
			System.out.println("[Error] Print failed!");
		}
	}
	
	public synchronized String getCapturedRfidJson()
	{
//		if ( isActionValid() )
//		{
			////////////////////////////////////////////////
			// For display
			////////////////////////////////////////////////
			this.clothList = new ArrayList<Cloth>();
			this.clothList.addAll(this.getClothRackingHandler().getMapRfidClothNew().values());
			this.receiptClothTotal = this.getClothRackingHandler().getMapRfidClothAll().size();
			
			////////////////////////////////////////////////
			// Update clothTypeCountMap
			////////////////////////////////////////////////
			try
			{
				this.clothTypeCountList = this.updateClothTypeCountMap(this.clothList);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			// clear the newly capture list is very important!
			this.getClothRackingHandler().moveMapRfidClothNewToOld();
			this.getClothRackingHandler().clearMapRfidClothNew1();
//		}
//		else
//		{
//			// Empty List return through json to prevent js/jquery error
//			this.clothList = new ArrayList<Cloth>();
//			this.receiptClothTotal = 0;
//			this.clothTypeCountList = new ArrayList<ClothTypeCounter>();
//		}
		
		return "getClothInfoJson";
	}
	
	public synchronized String ajaxRemoveRfid()
	{
//		if ( isActionValid() )
//		{
			Map<String, Cloth> mapRfidClothAll = this.getClothRackingHandler().getMapRfidClothAll();
			Map<String, Cloth> mapRfidClothOld = this.getClothRackingHandler().getMapRfidClothOld();
			TreeMap<String, Integer> clothTypeCountMap = this.getSessionClothTypeCountMap();
			
			
			for (int i = 0; i < rfidToBeRemovedList.size(); i++)
			{
				String curRfid = rfidToBeRemovedList.get(i);
				Cloth curCloth = mapRfidClothAll.get(curRfid);
				
				// 1. Delete from mapRfidClothAll so it can be re-captured
				mapRfidClothAll.remove(curRfid);
				mapRfidClothOld.remove(curRfid);
				
				// 2. update the cloth-type-counter
				String curType = curCloth.getClothType().getName();
				Integer curCounter = clothTypeCountMap.get(curType);
				curCounter--;
				if (curCounter == 0)
				{
					clothTypeCountMap.remove(curType);
				}
				else
				{
					clothTypeCountMap.put(curType, curCounter);
				}
				
				//System.out.println("RFID " + curRfid + " is removed!");
			}
			
			// 3. update the receipt-cloth-total
			this.receiptClothTotal = mapRfidClothAll.size();
			
			// 4. convert to list for displaying
			this.clothTypeCountList = this.convertClothTypeCountMapToList(clothTypeCountMap);
//		}
		
		return "ajaxRemoveRfid";
	}
	
	
	
	
	private List<ClothTypeCounter> updateClothTypeCountMap(List<Cloth> clothList) throws Exception
	{
		// 1. get the cloth-type-count-map from session
		TreeMap<String, Integer> clothTypeCountMap = this.getSessionClothTypeCountMap();
		
		// 2. examine the newly-captured-cloth-list and increment the counter of cloth type
		for (int i = 0; i < clothList.size(); i++)
		{
			String type = clothList.get(i).getClothType().getName();
			Integer counter = null;
			if (clothTypeCountMap.containsKey(type))
			{
				counter = clothTypeCountMap.get(type);
				counter++;
				clothTypeCountMap.put(type, counter);
			}
			else
			{
				clothTypeCountMap.put(type, 1);
			}
		}
		
		// 3. convert the cloth-type-count-map into a list so it can be sent back to HTML by json
		List<ClothTypeCounter> typeCountList = this.convertClothTypeCountMapToList(clothTypeCountMap);
		return typeCountList;
	}
	
	private TreeMap<String, Integer> getSessionClothTypeCountMap()
	{
		TreeMap<String, Integer> clothTypeCountMap = (TreeMap<String, Integer>) getSession().get(SESSION_KEY_CLOTHTYPE_COUNT_MAP);
		if (clothTypeCountMap == null)
		{
			clothTypeCountMap = new TreeMap<String, Integer>();
			getSession().put(SESSION_KEY_CLOTHTYPE_COUNT_MAP, clothTypeCountMap);
		}
		
		return clothTypeCountMap;
	}
	
	private void clearSessionClothTypeCountMap()
	{
		getSession().put(SESSION_KEY_CLOTHTYPE_COUNT_MAP, null);
	}

	public void resetReceipt()
	{
		//System.out.println("Reset Receipt!");
		this.resetSessionVariables();
		this.getClothRackingHandler().clearMapRfidClothAll();
		this.getClothRackingHandler().clearMapRfidClothNew1();
		this.getClothRackingHandler().clearMapRfidClothOld();
		// no staff to reset (HH provide zone field only)
	}


	///////////////////////////////////////////////////////////////
	// Start - Check page availability
	///////////////////////////////////////////////////////////////
	/*
	 * Background: 
	 * 		This page is disallowed to be accessed by multiple user/IP/Session simultaneously,
	 * 		this page need to be checked before entering. If there is already someone (IP/Session) 
	 * 		using this page, go to the confirm-page first. The confirm-page will ask whether 
	 * 		you want to continue, if yes, you will enter the page but normally the another user's 
	 * 		data input will all be removed; if no, stay at the confirm page
	 * 
	 *		See detail in /ProjectName/Doc Code for Copy/Page-In-Use.txt
	 */
//	private static String masterHostIp = null;	// decide which IP can use this page / action
//	private static String masterSession = null;	// decide which session can use this page / action
//	private Boolean takePageOwnership;			// client use this var to decide whether it will take the ownership of Ironing Page
//	private Boolean masterIsMe;				// client use this var to check if he the page master currently
//
//	protected void setPageToAvailable()
//	{
//		masterHostIp = null;
//		masterSession = null;
//	}
//
//	protected boolean isPageAvailable()
//	{
//		boolean isAvailable = true;
//		String clientIp = getServletRequest().getRemoteAddr();
//		String mySessionId = getServletRequest().getSession().getId();
//
//		if (masterHostIp == null)	// 沒有人正在使用這個Page
//		{
//			masterHostIp = clientIp;
//			masterSession = mySessionId;
//			//System.out.println("Master: " + clientIp);
//		}
//		else if (!masterHostIp.equals(clientIp) )	// 別人正在使用
//		{
//			if (this.takePageOwnership == null || this.takePageOwnership == false)
//			{
//				isAvailable = false;
//			}
//			else
//			{
//				//System.out.println( "Master Change: " + masterHostIp + " -> " + clientIp );
//				masterHostIp = clientIp;
//				masterSession = mySessionId;
//			}
//		}
//		else	// 自己正在使用
//		{
//			if (masterSession == null)
//			{
//				masterSession = mySessionId;
//			}
//			else if (!masterSession.equals(mySessionId))
//			{
//				if (this.takePageOwnership == null || this.takePageOwnership == false)
//				{
//					isAvailable = false;
//				}
//				else
//				{
//					//System.out.println( "MasterSession Change: " + masterSession + " -> " + mySessionId );
//					//					masterHostIp = clientIp;
//					masterSession = mySessionId;
//				}
//			}
//		}
//
//		return isAvailable;
//	}
//
//	protected boolean isActionValid()
//	{
//		boolean isValid = true;
//
//		String clientIp = getServletRequest().getRemoteAddr();
//		String mySessionId = getServletRequest().getSession().getId();
//
//		if (masterHostIp != null )
//		{
//			if ( !masterHostIp.equals(clientIp) )	// 已被別人搶去使用權
//			{
//				isValid = false;
//			}
//			else
//			{
//				// 自己正在使用中, 但被另一個自己的Session搶了使用權
//				if (masterSession != null && !masterSession.equals(mySessionId))
//				{
//					isValid = false;
//				}
//			}
//		}
//
//		return isValid;
//	}
//
//	// JSP will use this var
//	public Boolean getTakePageOwnership()
//	{
//		return takePageOwnership;
//	}
//	public void setTakePageOwnership(Boolean takePageOwnership)
//	{
//		this.takePageOwnership = takePageOwnership;
//	}
//	public String checkPageMaster()
//	{
//		this.masterIsMe = this.isActionValid();
//		return "checkPageMaster";
//	}
//	public Boolean getMasterIsMe()
//	{
//		return masterIsMe;
//	}
//	public void setMasterIsMe(Boolean masterIsMe)
//	{
//		this.masterIsMe = masterIsMe;
//	}
	///////////////////////////////////////////////////////////////
	// End - Check page availability
	///////////////////////////////////////////////////////////////




	////////////////////////////////////////////////
	// Utility Methods
	////////////////////////////////////////////////	

	// Manually clear Session Var
	private void resetSessionVariables()
	{
		getSession().put(SESSION_KEY_CLOTHTYPE_COUNT_MAP, null);
	}
	
	public ClothRackingHandler getClothRackingHandler()
	{
		if (clothRackingHandler == null)
		{
			HandheldHandlerFactory factory = SpringUtils.getBean(HandheldHandlerFactory.BEANNAME);
			clothRackingHandler = (ClothRackingHandler) factory.getHandler(ClothRackingHandler.EVENT_NAME);
		}
		
		return clothRackingHandler;
	}

	private List<ClothTypeCounter> convertClothTypeCountMapToList(TreeMap<String, Integer> clothTypeCountMap)
	{
		ArrayList<Entry<String, Integer>> ctcList = new ArrayList<Entry<String,Integer>>(clothTypeCountMap.entrySet());
		ArrayList<ClothTypeCounter> resultList = new ArrayList<ClothTypeCounter>();
		
		for (int i = 0; i < ctcList.size(); i++)
		{
			Entry<String, Integer> e = ctcList.get(i);
			
			String type = e.getKey();
			Integer num = e.getValue();
			
			ClothTypeCounter typeCounter = new ClothTypeCounter();
			typeCounter.setType(type);
			typeCounter.setNum(num);
			
			resultList.add(typeCounter);
		}
		
		return resultList;
	}
	
	private void deleteHistoryClothFromReceipt(Set<String> rfidSet)
	{
		// get all UNFINISHED receipt
		List<Receipt> urList = findUnfinishedReceipt();
		Receipt ur = null;
		Iterator<HistoryCloth> itHc = null;
		HistoryCloth hc = null;
		
		List<HistoryCloth> deleteList = new ArrayList<HistoryCloth>();
		for (int i = 0; i < urList.size(); i++)
		{
			ur = urList.get(i);
			itHc = ur.getHistoryClothSet().iterator();
			
			while (itHc.hasNext())
			{
				hc = itHc.next();
				if (rfidSet.contains(hc.getRfid()))
				{
					deleteList.add(hc);
				}
			}
		}
		
		this.getGeneralService().deleteList(HistoryCloth.class, deleteList);
	}
	
	private List<Receipt> findUnfinishedReceipt()
	{
		// find all UNFINISHED receipts (collect, iron)
		List<Receipt> receiptList = this.getGeneralService().findByExample(Receipt.class, null, null, null, 
				
				new CustomCriteriaHandler<Receipt>()
				{
					@Override
					public void makeCustomCriteria(Criteria criteria)
					{
						Disjunction cases1 = Restrictions.disjunction();
						cases1.add( Restrictions.eq("receiptStatus", ReceiptStatus.Processing));
						cases1.add( Restrictions.eq("receiptStatus", ReceiptStatus.Followup));
						criteria.add(cases1);
						
						Disjunction cases2 = Restrictions.disjunction();
						cases2.add( Restrictions.eq("receiptType", ReceiptType.Collect) );
						cases2.add( Restrictions.eq("receiptType", ReceiptType.Iron) );
						criteria.add(cases2);
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
							receipt = list.get(i);
							
							if (receipt.getHistoryClothSet() != null)
							{
								receipt.getHistoryClothSet().size();
							}
						}
					}
				},
				
				Order.asc("id"));
		
		return receiptList;
	}
	
	
	////////////////////////////////////////////////
	// Getter and Setter
	////////////////////////////////////////////////
	public Integer getReceiptClothTotal()
	{
		return receiptClothTotal;
	}
	public void setReceiptClothTotal(Integer receiptClothTotal)
	{
		this.receiptClothTotal = receiptClothTotal;
	}
	public Receipt getReceipt()
	{
		return receipt;
	}
	public void setReceipt(Receipt receipt)
	{
		this.receipt = receipt;
	}
	public List<ClothTypeCounter> getClothTypeCountList()
	{
		return clothTypeCountList;
	}
	public void setClothTypeCountList(List<ClothTypeCounter> clothTypeCountList)
	{
		this.clothTypeCountList = clothTypeCountList;
	}
	public List<Cloth> getClothList()
	{
		return clothList;
	}
	public void setClothList(List<Cloth> clothList)
	{
		this.clothList = clothList;
	}
	public List<String> getRfidToBeRemovedList()
	{
		return rfidToBeRemovedList;
	}
	public void setRfidToBeRemovedList(List<String> rfidToBeRemovedList)
	{
		this.rfidToBeRemovedList = rfidToBeRemovedList;
	}
	public String getSubreportPath()
	{
		return subreportPath;
	}
	public void setSubreportPath(String subreportPath)
	{
		this.subreportPath = subreportPath;
	}
	public List<ReceiptRackMainObject> getMainReportList()
	{
		return mainReportList;
	}
	public void setMainReportList(List<ReceiptRackMainObject> mainReportList)
	{
		this.mainReportList = mainReportList;
	}
	public List<ReceiptRackSub1Object> getSubreport1List()
	{
		return subreport1List;
	}
	public void setSubreport1List(List<ReceiptRackSub1Object> subreport1List)
	{
		this.subreport1List = subreport1List;
	}
	public List<ReceiptRackSub2Object> getSubreport2List()
	{
		return subreport2List;
	}
	public void setSubreport2List(List<ReceiptRackSub2Object> subreport2List)
	{
		this.subreport2List = subreport2List;
	}
}
