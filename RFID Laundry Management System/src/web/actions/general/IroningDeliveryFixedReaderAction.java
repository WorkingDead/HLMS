package web.actions.general;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import module.ale.handler.IroningDeliveryHandler;
import module.dao.general.HistoryCloth;
import module.dao.general.Receipt;
import module.dao.general.Receipt.ReceiptStatus;
import module.dao.general.Receipt.ReceiptType;
import module.dao.general.ReceiptPatternIron;
import module.dao.master.Cloth;
import module.dao.master.RollContainer;
import module.dao.master.Cloth.ClothStatus;
import module.dao.master.Staff;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.Order;
import utils.convertor.DateConverter;
import utils.spring.SpringUtils;
import web.actions.BaseActionGeneral;
import web.actions.form.ClothTypeCounter;
import web.actions.form.ReceiptIronMainObject;
import web.actions.form.ReceiptIronSub1Object;
import web.actions.form.ReceiptIronSub2Object;

@Results({
	
//	@Result(name="checkPageMaster", type="json", params = {
//			"includeProperties" , 	"masterIsMe"
//	}),
	
	@Result(name="getClothInfoJson", type="json", params = {
			"includeProperties" , 	"clothList\\[\\d+\\]\\.code, " +
									"clothList\\[\\d+\\]\\.rfid, " +
									"clothList\\[\\d+\\]\\.clothStatus, " +
									"clothList\\[\\d+\\]\\.clothType\\.name, " + 
									"clothList\\[\\d+\\]\\.staff\\.code, " + 
									"clothList\\[\\d+\\]\\.staff\\.nameCht, " +
									"clothList\\[\\d+\\]\\.staff\\.nameEng, " + 
									"clothList\\[\\d+\\]\\.staff\\.dept\\.nameCht, " +
									"clothList\\[\\d+\\]\\.staff\\.dept\\.nameEng, " +
									"receiptClothTotal, " + 
									"rollContainerClothTotal, " +  
									"clothTypeCountList\\[\\d+\\]\\.type, " + 
									"clothTypeCountList\\[\\d+\\]\\.num"
	}),
	
	@Result(name="ajaxRemoveRfid", type="json", params = {
			"includeProperties" , 	"receiptClothTotal, " +
									"rollContainerClothTotal, " + 
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
public class IroningDeliveryFixedReaderAction extends BaseActionGeneral
{
	private static final long serialVersionUID = 3157110574560513410L;
	private static final Logger log4j = Logger.getLogger(IroningDeliveryFixedReaderAction.class);
	
	// Session
	private static final String SESSION_KEY_CLOTHTYPE_COUNT_MAP = "SESSION_KEY_CLOTHTYPE_COUNT_MAP";
	private static final String SESSION_KEY_ROLL_CONTAINER_PATTERN_MAP = "SESSION_KEY_ROLL_CONTAINER_PATTERN_MAP";
	
	private List<Cloth> clothList;
	private Cloth cloth;
	private Integer receiptClothTotal;
	private Integer rollContainerClothTotal;
	private List<String> rfidToBeRemovedList;
	private List<ClothTypeCounter> clothTypeCountList;
	
	private Receipt receipt;
	private List<RollContainer> rollContainerList;
	private RollContainer rollContainer;
	
	// ECSpec Handler
	private IroningDeliveryHandler ironingDeliveryHandler;
	
	// iReport Receipt Printing
	private static final String JASPER_RECEIPT_IRON = "jasper_report/receiptIron.jasper";
	private String subreportPath;		// absolute path of subreport file
	
	private List<ReceiptIronMainObject> mainReportList;
	private List<ReceiptIronSub1Object> subreport1List;
	private List<ReceiptIronSub2Object> subreport2List;
	
	
	public String getMainPage()
	{
//		if ( !isPageAvailable() )
//		{
//			this.setTilesKey("ironing.delivery.page.in.use");
//			return TILES;
//		}
		
		//System.out.println( "Get Ironing-Delivery Page!" );
		this.resetReceipt();
		
		String receiptCode = this.getGeneralService().genIroningDeliveryReceiptCode();
		receipt = new Receipt();
		receipt.setCode(receiptCode);
		receiptClothTotal = 0;
		
		rollContainerList = getMasterService().findAll(RollContainer.class, null, null, null, Order.asc("code"));
		rollContainerClothTotal = 0;
		
		this.setTilesKey("ironing.delivery.main");
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
			Map<String, Cloth> mapRfidClothAll = this.getIroningDeliveryHandler().getMapRfidClothAll();
			if (mapRfidClothAll == null || mapRfidClothAll.size() == 0)
			{
				addActionError(getText("errors.no.cloth.found"));
			}
//		}
	}
	
	public String create()
	{
		
		try
		{
			// 1. Save receipt and transaction
			createImpl();
			
			// 2. Print the receipt
			this.printReceipt(this.receipt);
			
			this.resetReceipt();
			
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
		// Receipt Info
		receipt.setReceiptClothTotal( receiptClothTotal );
		receipt.setReceiptType(ReceiptType.Iron);
		receipt.setReceiptStatus(ReceiptStatus.Processing);
		
		// 1. get containerId-to-pattern map
		HashMap<Long, ReceiptPatternIron> patternMap = (HashMap<Long, ReceiptPatternIron>) getSession().get(SESSION_KEY_ROLL_CONTAINER_PATTERN_MAP);
		// 2. get all pattern
		HashSet<ReceiptPatternIron> patternSet = new HashSet<ReceiptPatternIron>( patternMap.values() );
		Iterator<ReceiptPatternIron> itPattern = patternSet.iterator();
		// 3. create a set for deletion of empty container
		HashSet<ReceiptPatternIron> deletedPatternSet = new HashSet<ReceiptPatternIron>();
		Calendar expDeliveryTime = Calendar.getInstance();
		// 4. HistoryCloth is used for ReceiptStatus change
		HashSet<HistoryCloth> historyClothSet = new HashSet<HistoryCloth>();
		HistoryCloth historyCloth = null;
		while (itPattern.hasNext())
		{
			ReceiptPatternIron pat = itPattern.next();
			if (pat.getPatternClothTotal() > 0)
			{
				pat.setIroningDeliveryTime(expDeliveryTime);
				Iterator<Cloth> itCloth = pat.getClothSet().iterator();
				while (itCloth.hasNext())
				{
					Cloth cloth = itCloth.next();
					cloth.setClothStatus(ClothStatus.Ironing);
					cloth.setLastReceiptCode(receipt.getCode());
					
					
					// add HistoryCloth
					historyCloth = new HistoryCloth();
					historyCloth.setRfid(cloth.getRfid());
					historyClothSet.add(historyCloth);
				}
			}
			else
			{
				// no-cloth-patterns are collected and remove later
				deletedPatternSet.add(pat);
			}
		}
		
		patternSet.removeAll(deletedPatternSet);
		receipt.setReceiptPatternIronSet(patternSet);
		receipt.setHistoryClothSet(historyClothSet);
		
		//////////////////////////////////////////////
		// Save the receipt
		//////////////////////////////////////////////
		this.getGeneralService().saveReceiptAndTransaction(receipt);
	}
	
	
	private void printReceipt(Receipt receipt) throws Exception
	{
		/////////////////////////////////////////////
		// 1. Preparing receipt data for printing
		/////////////////////////////////////////////
		
		// iReport setting
		String reportFilePath = this.getRealPath() + JASPER_RECEIPT_IRON;
		this.subreportPath = this.getRealPath() + JASPER_FOLDER;
		Map parameters = null;
		String printerName = this.getBeansFactoryApplication().getSystemPrinterA4();
		
		String handledByUser = this.getUser().getUserDisplayName();
		String receiptCode = receipt.getCode();
		Calendar printDateTime = Calendar.getInstance();
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
	
	
	public void resetReceipt()
	{
//		if ( isActionValid() )
//		{
			//System.out.println("Reset Receipt!");
			this.resetSessionVariables();
			this.stopCapture();
			this.getIroningDeliveryHandler().clearMapRfidClothAll();
			this.getIroningDeliveryHandler().clearMapRfidClothNew();
			// no staff to reset (use fixed-reader)
//		}
	}
	
	public String startCapture()
	{
//		if ( isActionValid() )
//		{
			System.out.println("Start capturing RFID...");
			getIroningDeliveryHandler().startCapture();
//		}
		
		return null;
	}
	
	public String stopCapture()
	{
//		if ( isActionValid() )
//		{
			System.out.println("Stop capturing!");
			getIroningDeliveryHandler().stopCapture();
//		}
		
		return null;
	}
	
	public synchronized String getCapturedRfidJson()
	{
//		if ( isActionValid() )
//		{
			Long cartId = rollContainer.getId();
			
			////////////////////////////////////////////////
			// For display
			////////////////////////////////////////////////
			this.clothList = new ArrayList<Cloth>();
			this.clothList.addAll(this.getIroningDeliveryHandler().getMapRfidClothNew().values());
			this.receiptClothTotal = this.getIroningDeliveryHandler().getMapRfidClothAll().size();
			
			
			////////////////////////////////////////////////
			// Add new clothes to container's session var
			////////////////////////////////////////////////
			// create or get pattern-from-session
			ReceiptPatternIron pattern = this.getSessionPattern(cartId);
			if (this.clothList.size() > 0)
			{
				pattern.getClothSet().addAll(this.clothList);
				pattern.setPatternClothTotal(pattern.getClothSet().size());
//				addActionMessage(String.format(getText("msg.custom.new.item.saved"), pattern.getClothSet().size()));
			}
			this.rollContainerClothTotal = pattern.getPatternClothTotal();
			
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
			this.getIroningDeliveryHandler().clearMapRfidClothNew();
//		}
//		else
//		{
//			this.resetDisplayData();
//		}
		
		return "getClothInfoJson";
	}
	
	
	public synchronized String getSavedRfidJson()
	{
//		if ( isActionValid() )
//		{
			// Super important to clear ClothTypeCountMap from session!!!
			this.clearSessionClothTypeCountMap();
			
			Long containerId = this.rollContainer.getId();
			//System.out.println("containerId: " + containerId);
			ReceiptPatternIron pattern = this.getSessionPattern(containerId);
			this.clothList = new ArrayList<Cloth>(pattern.getClothSet());
			this.rollContainerClothTotal = this.clothList.size();
			
			try
			{
				this.clothTypeCountList = this.updateClothTypeCountMap(this.clothList);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
//		}
//		else
//		{
//			this.resetDisplayData();
//		}
		
		return "getClothInfoJson";
	}
	
	public synchronized String ajaxRemoveRfid()
	{
//		if ( isActionValid() )
//		{
			Map<String, Cloth> mapRfidClothAll = this.getIroningDeliveryHandler().getMapRfidClothAll();
			TreeMap<String, Integer> clothTypeCountMap = this.getSessionClothTypeCountMap();
			
			Long containerId = this.rollContainer.getId();
			ReceiptPatternIron pattern = this.getSessionPattern(containerId);
			
			for (int i = 0; i < rfidToBeRemovedList.size(); i++)
			{
				String curRfid = rfidToBeRemovedList.get(i);
				Cloth curCloth = mapRfidClothAll.get(curRfid);
				
				// 1. Delete from mapRfidClothAll so it can be re-captured
				mapRfidClothAll.remove(curRfid);
				
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
				//System.out.println("RFID " + curRfid + " is removed from Container " + containerId + "!");
				
				// 3. delete the cloth from container's clothset
				ArrayList<Cloth> containerClothList = new ArrayList<Cloth>(pattern.getClothSet());
				Cloth tmpCloth = null;
				for (int j = 0; j < containerClothList.size(); j++)
				{
					tmpCloth = containerClothList.get(j);
					if (tmpCloth.getRfid().equals(curRfid))
					{
						containerClothList.remove(tmpCloth);
						pattern.getClothSet().remove(tmpCloth);
						break;
					}
				}
			}
			
			// 4. update the container-cloth-total
			int numOfClothes = pattern.getClothSet().size();
			pattern.setPatternClothTotal(numOfClothes);
			this.rollContainerClothTotal = numOfClothes;
			
			// 5. update the receipt-cloth-total
			this.receiptClothTotal = mapRfidClothAll.size();
			
			// 5. convert to list for displaying
			this.clothTypeCountList = this.convertClothTypeCountMapToList(clothTypeCountMap);
//		}
//		else
//		{
//			this.resetDisplayData();
//		}
		
		return "ajaxRemoveRfid";
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
////					masterHostIp = clientIp;
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
		getSession().put(SESSION_KEY_ROLL_CONTAINER_PATTERN_MAP, null);
		getSession().put(SESSION_KEY_CLOTHTYPE_COUNT_MAP, null);
	}
	
	
	public IroningDeliveryHandler getIroningDeliveryHandler()
	{
		if (ironingDeliveryHandler == null)
			ironingDeliveryHandler = SpringUtils.getBean(IroningDeliveryHandler.BEANNAME);
		
		return ironingDeliveryHandler;
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
	
	
	// each container has a pattern if it contains clothes
	private ReceiptPatternIron getSessionPattern(Long id)
	{
		HashMap<Long, ReceiptPatternIron> patternMap = (HashMap<Long, ReceiptPatternIron>) getSession().get(SESSION_KEY_ROLL_CONTAINER_PATTERN_MAP);
		if (patternMap == null)
		{
			patternMap = new HashMap<Long, ReceiptPatternIron>();
			getSession().put(SESSION_KEY_ROLL_CONTAINER_PATTERN_MAP, patternMap);
		}
		
		ReceiptPatternIron pattern = patternMap.get(id);
		if (pattern == null)
		{
			pattern = new ReceiptPatternIron();
			pattern.setRollContainer(getMasterService().get(RollContainer.class, id));
			pattern.setClothSet(new HashSet<Cloth>());
			pattern.setPatternClothTotal(0);
			patternMap.put(id, pattern);
		}
		
		return pattern;
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
	
	private void resetDisplayData()
	{
		this.clothList = new ArrayList<Cloth>();
		this.receiptClothTotal = 0;
		this.rollContainerClothTotal = 0;
		this.clothTypeCountList = new ArrayList<ClothTypeCounter>();
	}
	
	///////////////////////////////////////////
	// Getter and Setter
	///////////////////////////////////////////
	public Receipt getReceipt()
	{
		return receipt;
	}
	public void setReceipt(Receipt receipt)
	{
		this.receipt = receipt;
	}
	public List<Cloth> getClothList()
	{
		return clothList;
	}
	public void setClothList(List<Cloth> clothList)
	{
		this.clothList = clothList;
	}
	public Cloth getCloth()
	{
		return cloth;
	}
	public void setCloth(Cloth cloth)
	{
		this.cloth = cloth;
	}
	public Integer getReceiptClothTotal()
	{
		return receiptClothTotal;
	}
	public void setReceiptClothTotal(Integer receiptClothTotal)
	{
		this.receiptClothTotal = receiptClothTotal;
	}

	public Integer getRollContainerClothTotal()
	{
		return rollContainerClothTotal;
	}
	public void setRollContainerClothTotal(Integer rollContainerClothTotal)
	{
		this.rollContainerClothTotal = rollContainerClothTotal;
	}
	public List<String> getRfidToBeRemovedList()
	{
		return rfidToBeRemovedList;
	}
	public void setRfidToBeRemovedList(List<String> rfidToBeRemovedList)
	{
		this.rfidToBeRemovedList = rfidToBeRemovedList;
	}
	public List<ClothTypeCounter> getClothTypeCountList()
	{
		return clothTypeCountList;
	}
	public void setClothTypeCountList(List<ClothTypeCounter> clothTypeCountList)
	{
		this.clothTypeCountList = clothTypeCountList;
	}
	public List<RollContainer> getRollContainerList()
	{
		return rollContainerList;
	}
	public void setRollContainerList(List<RollContainer> rollContainerList)
	{
		this.rollContainerList = rollContainerList;
	}
	public RollContainer getRollContainer()
	{
		return rollContainer;
	}
	public void setRollContainer(RollContainer rollContainer)
	{
		this.rollContainer = rollContainer;
	}
	public void setIroningDeliveryHandler(IroningDeliveryHandler ironingDeliveryHandler)
	{
		this.ironingDeliveryHandler = ironingDeliveryHandler;
	}
	public String getSubreportPath()
	{
		return subreportPath;
	}
	public void setSubreportPath(String subreportPath)
	{
		this.subreportPath = subreportPath;
	}
	public List<ReceiptIronMainObject> getMainReportList()
	{
		return mainReportList;
	}
	public void setMainReportList(List<ReceiptIronMainObject> mainReportList)
	{
		this.mainReportList = mainReportList;
	}
	public List<ReceiptIronSub1Object> getSubreport1List()
	{
		return subreport1List;
	}
	public void setSubreport1List(List<ReceiptIronSub1Object> subreport1List)
	{
		this.subreport1List = subreport1List;
	}
	public List<ReceiptIronSub2Object> getSubreport2List()
	{
		return subreport2List;
	}
	public void setSubreport2List(List<ReceiptIronSub2Object> subreport2List)
	{
		this.subreport2List = subreport2List;
	}
}
