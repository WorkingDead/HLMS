package web.actions.kiosk;

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

import javax.annotation.Resource;

import module.ale.handheld.handler.ClothDistributeHandler;
import module.dao.general.Receipt;
import module.dao.general.Receipt.ReceiptStatus;
import module.dao.general.Receipt.ReceiptType;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.Department;
import module.dao.master.Staff;
import module.dao.master.Cloth.ClothStatus;
import module.dao.system.Users;
import module.service.ServiceFactory;
import module.service.all.MasterService;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

import utils.convertor.DateConverter;
import utils.spring.SpringUtils;
import web.actions.BaseActionKiosk;
import web.actions.BaseActionSecurity;
import web.actions.BaseActionKiosk.KioskName;
import web.actions.form.ClothTypeCounter;
import web.actions.form.ReceiptDistMainObject;
import web.actions.form.ReceiptDistSub1Object;
import web.actions.form.ReceiptDistSub2Object;

@Results({
	
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
public class ClothDistributeAction extends BaseActionKiosk
{
	private static final long serialVersionUID = -3720345000809590217L;
	private static final Logger log4j = Logger.getLogger(ClothDistributeAction.class);
	
	// Session
	private static final String SESSION_KEY_K1_CLOTHTYPE_COUNT_MAP = "SESSION_KEY_K1_CLOTHTYPE_COUNT_MAP";
	private static final String SESSION_KEY_K2_CLOTHTYPE_COUNT_MAP = "SESSION_KEY_K2_CLOTHTYPE_COUNT_MAP";	
	
	private List<Cloth> clothList;
	private Cloth cloth;
	private List<String> rfidToBeRemovedList;
	private List<ClothTypeCounter> clothTypeCountList;
	
	private Receipt receipt;
	private Staff staffHandledBy;
	private Staff staffPickedBy;
	private Staff staff;
	
	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}
	private Integer receiptClothTotal;
	
	// Handheld Handler
	private ClothDistributeHandler handler;
	private String kioskName;
	
	private Calendar distributeDate; 
	
	public Calendar getDistributeDate() {
		return distributeDate;
	}

	@TypeConversion(converter="utils.convertor.struts2.SimpleDateTimeToCalendarTypeConvertor")
	public void setDistributeDate(Calendar distributeDate) {
		this.distributeDate = distributeDate;
	}
	// iReport Receipt Printing
	private static final String JASPER_RECEIPT_DIST = "jasper_report/receiptDist.jasper";
	private String subreportPath;		// absolute path of subreport file
	private List<ReceiptDistMainObject> mainReportList;
	private List<ReceiptDistSub1Object> subreport1List;
	private List<ReceiptDistSub2Object> subreport2List;
	
	public String getMainPage()
	{
		this.kioskName = this.getServletRequest().getParameter(KIOSK_NAME);	// get Kiosk Name from URL
		//System.out.println( "get " + kioskName + "-Cloth-Dist Page!" );

		this.setupKioskMainPage(this.kioskName);
		
		this.setTilesKey("cloth-distribute.main");
		return TILES;
	}
	
	private void setupKioskMainPage(String kioskName)
	{
		String receiptCode = this.getGeneralService().genKioskClothDistributeReceiptCode();
		this.receipt = new Receipt();
		this.receipt.setCode(receiptCode);
		
//		System.out.println("kioskName: " + kioskName);
		
		if (this.kioskName.equals(KioskName.kiosk1.toString()))
		{
			this.resetSessionVariables(SESSION_KEY_K1_CLOTHTYPE_COUNT_MAP);
			this.staffHandledBy = this.getClothDistributeHandler().getK1HandleByStaff();
			this.staffPickedBy = this.getClothDistributeHandler().getK1PickedByStaff();
			
			// clothes read by Handheld but already "real-time-appended-to-table" before
			this.clothList = new ArrayList<Cloth>();
			this.clothList.addAll(this.getClothDistributeHandler().getK1MapRfidClothOld().values());
			this.receiptClothTotal = this.clothList.size();
			
			try
			{
				this.clothTypeCountList = this.updateKioskClothTypeCountMap(SESSION_KEY_K1_CLOTHTYPE_COUNT_MAP, this.clothList);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else if (kioskName.equals(KioskName.kiosk2.toString()))
		{
			this.resetSessionVariables(SESSION_KEY_K2_CLOTHTYPE_COUNT_MAP);
			this.staffHandledBy = this.getClothDistributeHandler().getK2HandleByStaff();
			this.staffPickedBy = this.getClothDistributeHandler().getK2PickedByStaff();
			
			// clothes read by Handheld but already "real-time-appended-to-table" before
			this.clothList = new ArrayList<Cloth>();
			this.clothList.addAll(this.getClothDistributeHandler().getK2MapRfidClothOld().values());
			this.receiptClothTotal = this.clothList.size();
			
			try
			{
				this.clothTypeCountList = this.updateKioskClothTypeCountMap(SESSION_KEY_K2_CLOTHTYPE_COUNT_MAP, this.clothList);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	

	
	public void validateCreate()
	{
		Integer numOfFound = null;
		
		// check handled-by-staff code
		if (this.staffHandledBy.getCode() != null && !this.staffHandledBy.getCode().isEmpty())
		{
			this.staffHandledBy = this.getStaffByCode(this.staffHandledBy.getCode());
			if (this.staffHandledBy == null)
			{
				addActionError(String.format(getText("errors.custom.invalid"), getText("staff.code.handled.by")));
			}
		}
		else if (this.staffHandledBy.getCardNumber() != null && !this.staffHandledBy.getCardNumber().isEmpty())
		{
			final String cardId = this.computeCardId(staffHandledBy.getCardNumber());
			this.staffHandledBy = this.getStaffByCard(cardId);
			if (this.staffHandledBy == null)
			{
				addActionError(String.format(getText("errors.custom.invalid"), getText("staff.card.handled.by")));
			}
		}
		else
		{
			addActionError(String.format(getText("errors.custom.required.or"), getText("staff.code.handled.by"), getText("staff.card.handled.by")));
		}
		
		// check picked-by-staff code
		if (this.staffPickedBy.getCode() != null && !this.staffPickedBy.getCode().isEmpty())
		{
			this.staffPickedBy = this.getStaffByCode(this.staffPickedBy.getCode());
			if (this.staffPickedBy == null)
			{
				addActionError(String.format(getText("errors.custom.invalid"), getText("staff.code.picked.by")));
			}
		}
		else if (this.staffPickedBy.getCardNumber() != null && !this.staffPickedBy.getCardNumber().isEmpty())
		{
			final String cardId = this.computeCardId(staffPickedBy.getCardNumber());
			this.staffPickedBy = this.getStaffByCard(cardId);
			if (this.staffPickedBy == null)
			{
				addActionError(String.format(getText("errors.custom.invalid"), getText("staff.card.picked.by")));
			}
		}
		else
		{
			addActionError(String.format(getText("errors.custom.required.or"), getText("staff.code.picked.by"), getText("staff.card.picked.by")));
		}
		
		// check cloth list
		Map<String, Cloth> mapRfidClothAll = null;
		if (this.kioskName.equals(KioskName.kiosk1.toString()))
		{
			mapRfidClothAll = this.getClothDistributeHandler().getK1MapRfidClothAll();
		}
		else if (this.kioskName.equals(KioskName.kiosk2.toString()))
		{
			mapRfidClothAll = this.getClothDistributeHandler().getK2MapRfidClothAll();
		}
		
		if (mapRfidClothAll == null || mapRfidClothAll.size() == 0)
		{
			addActionError(getText("errors.no.cloth.found"));
		}
	}
	
	public CustomLazyHandler<Cloth> getClothDefaultLazyHandler() {
		return 
			new CustomLazyHandler<Cloth>() {
	
				@Override
				public void LazyObject(Cloth obj) {
					
					obj.getStaff().getCode();
					obj.getStaff().getDept().getNameEng();
					obj.getClothType().getName();
					
					if ( obj.getZone() != null )
						obj.getZone().getCode();

					ClothType clothType = obj.getClothType();
	
					//for attachment use
					if(clothType.getAttachmentList()!=null)
					{
						clothType.getAttachmentList().getCreationDate();
						if(clothType.getAttachmentList().getAttachments()!=null)
						{
							clothType.getAttachmentList().getAttachments().size();
						}
					}

					clothType.setSoleImageAttachment( getOthersService().getSoleImageAttachment(clothType) );
					//clothType.setImageAttachment( getOthersService().getImageAttachment(clothType) );
					//clothType.setSelectionImageAttachment( getOthersService().getSelectedAttachment(clothType) );
				}
			};
	}
	
	
	public String distribute(){
		
		try
		{
			
			if ("".equals(staff.getCode())){
				addActionError( getText ("errors.staff.code.required") );
			}
			
			staff = getStaffByCode(staff.getCode());
			
			String receiptCode = this.getGeneralService().genKioskClothDistributeReceiptCode();
			this.receipt = new Receipt();
			this.receipt.setCode(receiptCode);
			
			Staff handledBy = getStaffByCard("99999");
			
//			UserDetails userDetails = getSystemService().loadUserByUsername(BaseActionSecurity.AdminUserName);
			
			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Users user = (Users) userDetails;
			
			CustomLazyHandler<Cloth> customLazyHandler = getClothDefaultLazyHandler();
			
			cloth = this.getMasterService().get(Cloth.class, cloth.getId(), customLazyHandler);
			
			
			HashSet<Cloth> clothSet = new HashSet<Cloth>();
			clothSet.add(cloth);
			Iterator<Cloth> itCloth = clothSet.iterator();
			while (itCloth.hasNext())
			{
				Cloth cloth = itCloth.next();
				cloth.setClothStatus(ClothStatus.Using);
				cloth.setLastReceiptCode(receipt.getCode());
				cloth.setModifiedBy(user);
				cloth.setZone(null);
			}
			
			receipt.setReceiptClothTotal(1);
			receipt.setReceiptType(ReceiptType.Distribute);
			receipt.setReceiptStatus(ReceiptStatus.Finished);	// no processing status (by Kitz)
			receipt.setClothSet( clothSet );
			receipt.setStaffHandledBy(handledBy);
			receipt.setStaffPickedBy(staff);
			receipt.setCreatedBy(user);
			
			Calendar now = Calendar.getInstance();
			distributeDate.set(Calendar.HOUR, now.get(Calendar.HOUR));
			distributeDate.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
			distributeDate.set(Calendar.SECOND, now.get(Calendar.SECOND));
			receipt.setFinishDate(distributeDate);
			
			///////////////////////////////////////////////////////
			// Save the Receipt
			///////////////////////////////////////////////////////
			this.getGeneralService().saveReceiptAndTransaction(receipt);
			
			this.kioskName = KioskName.kiosk1.toString();
			this.resetReceipt();
			
			// 2. Print the receipt
			this.printReceipt(this.receipt);
			
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
	
	public String create()
	{
		try
		{
			// 1. Save receipt and transaction
			createImpl();
			
			// 2. Print the receipt
			this.printReceipt(this.receipt);
			
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
//		this.staffHandledBy = getStaffByCode();		// already done in validation
//		this.staffPickedBy = getStaffByCode();		// already done in validation
		
//		Users kioskUser = getSystemService().findByExample(Users.class, null, null, null, 
//				new CustomCriteriaHandler<Users>()
//				{
//					@Override
//					public void makeCustomCriteria(Criteria baseCriteria)
//					{
//						baseCriteria.add(Restrictions.eq("username", BaseActionKiosk.KioskUserName));
//					}
//				}, 
//				null, null).get(0);
		
		UserDetails userDetails = getSystemService().loadUserByUsername(BaseActionKiosk.KioskUserName);
		Users kioskUser = (Users) userDetails;
		
		Map<String, Cloth> mapRfidClothAll = null;
		if (this.kioskName.equals(KioskName.kiosk1.toString()))
		{
			mapRfidClothAll = this.getClothDistributeHandler().getK1MapRfidClothAll();
		}
		else if (this.kioskName.equals(KioskName.kiosk2.toString()))
		{
			mapRfidClothAll = this.getClothDistributeHandler().getK2MapRfidClothAll();
		}
		
		
		HashSet<Cloth> clothSet = new HashSet<Cloth>(mapRfidClothAll.values());
		Iterator<Cloth> itCloth = clothSet.iterator();
		while (itCloth.hasNext())
		{
			Cloth cloth = itCloth.next();
			cloth.setClothStatus(ClothStatus.Using);
			cloth.setLastReceiptCode(receipt.getCode());
			cloth.setModifiedBy(kioskUser);
			cloth.setZone(null);
		}
		
		receipt.setReceiptClothTotal(mapRfidClothAll.size());
		receipt.setReceiptType(ReceiptType.Distribute);
		receipt.setReceiptStatus(ReceiptStatus.Finished);	// no processing status (by Kitz)
		receipt.setClothSet( clothSet );
		receipt.setStaffHandledBy(staffHandledBy);
		receipt.setStaffPickedBy(staffPickedBy);
		receipt.setCreatedBy(kioskUser);
		receipt.setFinishDate(Calendar.getInstance());
		
		///////////////////////////////////////////////////////
		// Save the Receipt
		///////////////////////////////////////////////////////
		this.getGeneralService().saveReceiptAndTransaction(receipt);
		this.resetReceipt();
	}
	
	
	private void printReceipt(Receipt receipt) throws Exception
	{
		/////////////////////////////////////////////
		// 1. Preparing receipt data for printing
		/////////////////////////////////////////////
		
		// iReport setting
		String reportFilePath = this.getRealPath() + JASPER_RECEIPT_DIST;
		this.subreportPath = this.getRealPath() + JASPER_FOLDER;
		Map parameters = null;
		
		String printerName = null;
		if (kioskName.equals(KioskName.kiosk1.toString()))
		{
			printerName = this.getBeansFactoryApplication().getKiosk1Printer();
		}
		else if (kioskName.equals(KioskName.kiosk2.toString()))
		{
			printerName = this.getBeansFactoryApplication().getKiosk2Printer();
		}
		else
		{
			printerName = "Invalid Kiosk Name so no printer selected!";
			System.out.println("printerName: " + printerName);
		}
		
		String receiptCode = receipt.getCode();
		Calendar printDateTime = Calendar.getInstance();
		String receiptDate = DateConverter.format(printDateTime, DateConverter.DATE_FORMAT);
		String receiptTime = DateConverter.format(printDateTime, DateConverter.HOUR_MINUTE_FORMAT);
		String handleByStaffCode = receipt.getStaffHandledBy().getCode();
		String handleByStaffName = receipt.getStaffHandledBy().getNameEng();
		String pickedByStaffCode = receipt.getStaffPickedBy().getCode();
		String pickedByStaffName = receipt.getStaffPickedBy().getNameEng();
		String receiptRemark = receipt.getRemark();
		
		// 1.1. Grouping clothes by cloth's staff's dept
		Iterator<Cloth> itCloth = receipt.getClothSet().iterator();
		Map<Long, List<Cloth>> deptClothMap = new HashMap<Long, List<Cloth>>();
		Long deptId = null;
		List<Cloth> clothList = null;
		Cloth tmpCloth = null;
		while (itCloth.hasNext())
		{
			tmpCloth = itCloth.next();
			deptId = tmpCloth.getStaff().getDept().getId();
			
			if (deptClothMap.containsKey(deptId))
			{
				clothList = deptClothMap.get(deptId);
				clothList.add(tmpCloth);
			}
			else
			{
				clothList = new ArrayList<Cloth>();
				clothList.add(tmpCloth);
				deptClothMap.put(deptId, clothList);
			}
		}
		
		// 1.2. For each dept, Summarizing clothes by clothType and summarizing clothes by staff
		Iterator<Long> itDeptId = deptClothMap.keySet().iterator();
		ArrayList<Cloth> staffClothList = null;
		ArrayList<Long> staffIdList = null;
		ArrayList<String> clothTypeNameList = null;
		Department dept = null;
		String deptName = null;
		String clothTypeName = null;
		Integer deptClothTotal = null;
		Integer clothTypeTotal = null;
		Integer staffClothTotal = null;
		Staff staff = null;
		Integer qty = null;
		Map<String, Integer> clothTypeCounter = null;
		Map<Long, ArrayList<Cloth>> staffClothListMap = null;
		Map<String, Integer> staffClothTypeCounter = null;
		Long staffId = null;
		String staffCode = null;
		String staffNameCht = null;
		String staffNameEng = null;
		String staffClothDetail = null;
		
		
		ReceiptDistSub1Object rs1o = null;
		ReceiptDistSub2Object rs2o = null;
		
		while (itDeptId.hasNext())
		{
			deptId = itDeptId.next();
			dept = getMasterService().get(Department.class, deptId);
			deptName = dept.getNameCht() + "\n" + dept.getNameEng();
			
			clothList = deptClothMap.get(deptId);
			deptClothTotal = clothList.size();
			
			clothTypeCounter = new HashMap<String, Integer>();
			staffClothListMap = new HashMap<Long, ArrayList<Cloth>>();
			
			for (int i = 0; i < clothList.size(); i++)
			{
				tmpCloth = clothList.get(i);
				
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
			clothTypeNameList = new ArrayList<String>(clothTypeCounter.keySet());
			Collections.sort(clothTypeNameList);
			
			
			for (int j = 0; j < clothTypeNameList.size(); j++)
			{
				clothTypeName = clothTypeNameList.get(j);
				qty = clothTypeCounter.get(clothTypeName);
				
				rs1o = new ReceiptDistSub1Object();
				rs1o.setClothType(clothTypeName);
				rs1o.setQty(qty);
				this.subreport1List.add(rs1o);
			}
			
			// 1.4. Fill sub-report-2 (staff's clothes)
			this.subreport2List = new ArrayList<ReceiptDistSub2Object>();
			
			staffIdList = new ArrayList<Long>(staffClothListMap.keySet());
			for (int j = 0; j < staffIdList.size(); j++)
			{
				staffId = staffIdList.get(j);
				staffClothList = staffClothListMap.get(staffId);
				
				tmpCloth = staffClothList.get(0);
				staff = tmpCloth.getStaff();
				staffCode = staff.getCode();
				staffNameCht = staff.getNameCht();
				staffNameEng = staff.getNameEng();
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
			parameters.put("handleByStaffCode", handleByStaffCode);
			parameters.put("handleByStaffName", handleByStaffName);
			parameters.put("pickedByStaffCode", pickedByStaffCode);
			parameters.put("pickedByStaffName", pickedByStaffName);
			parameters.put("receiptRemark", receiptRemark);
			
			parameters.put("deptClothTotal", deptClothTotal);
			parameters.put("dept", deptName);
			
			JRBeanCollectionDataSource dataSrc1 = new JRBeanCollectionDataSource(this.subreport1List);
			parameters.put("subreport1List", dataSrc1);
			JRBeanCollectionDataSource dataSrc2 = new JRBeanCollectionDataSource(this.subreport2List);
			parameters.put("subreport2List", dataSrc2);
			
			
			// add a dummy report-main-obj to defeat JasperReport bug
			this.mainReportList = new ArrayList<ReceiptDistMainObject>();
			this.mainReportList.add(new ReceiptDistMainObject());
			
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

	}
	
	
	// Used by JSP reset button (then this method cannot accept parameters)
	public void resetReceipt()
	{
		//System.out.println("Reset " + this.kioskName + "'s Receipt!");
		
		if (this.kioskName.equals(KioskName.kiosk1.toString()))
		{
			this.resetSessionVariables(SESSION_KEY_K1_CLOTHTYPE_COUNT_MAP);
			this.getClothDistributeHandler().clearK1MapRfidClothAll();
			this.getClothDistributeHandler().clearK1MapRfidClothNew1();
			this.getClothDistributeHandler().clearK1MapRfidClothOld();
			this.getClothDistributeHandler().resetK1AllStaff();
		}
		else if (this.kioskName.equals(KioskName.kiosk2.toString()))
		{
			this.resetSessionVariables(SESSION_KEY_K2_CLOTHTYPE_COUNT_MAP);
			this.getClothDistributeHandler().clearK2MapRfidClothAll();
			this.getClothDistributeHandler().clearK2MapRfidClothNew1();
			this.getClothDistributeHandler().clearK2MapRfidClothOld();
			this.getClothDistributeHandler().resetK2AllStaff();
		}
	}
	
	
	public synchronized String getCapturedRfidJson()
	{
		this.clothList = new ArrayList<Cloth>();
		
		
		if (this.kioskName.equals(KioskName.kiosk1.toString()))
		{
			this.clothList.addAll(this.getClothDistributeHandler().getK1MapRfidClothNew().values());
			this.receiptClothTotal = this.getClothDistributeHandler().getK1MapRfidClothAll().size();
			
			try
			{
				this.clothTypeCountList = this.updateKioskClothTypeCountMap(SESSION_KEY_K1_CLOTHTYPE_COUNT_MAP, this.clothList);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			// clear the newly capture list is very important!
			this.getClothDistributeHandler().moveK1MapRfidClothNewToOld();
			this.getClothDistributeHandler().clearK1MapRfidClothNew1();
		}
		else if (this.kioskName.equals(KioskName.kiosk2.toString()))
		{
			this.clothList.addAll(this.getClothDistributeHandler().getK2MapRfidClothNew().values());
			this.receiptClothTotal = this.getClothDistributeHandler().getK2MapRfidClothAll().size();
			
			try
			{
				this.clothTypeCountList = this.updateKioskClothTypeCountMap(SESSION_KEY_K2_CLOTHTYPE_COUNT_MAP, this.clothList);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			// clear the newly capture list is very important!
			this.getClothDistributeHandler().moveK2MapRfidClothNewToOld();
			this.getClothDistributeHandler().clearK2MapRfidClothNew1();
		}
		
		
		return "getClothInfoJson";
	}
	
	public synchronized String ajaxRemoveRfid()
	{
		Map<String, Cloth> mapRfidClothAll = null;
		TreeMap<String, Integer> clothTypeCountMap = null;
		if (this.kioskName.equals(KioskName.kiosk1.toString()))
		{
			mapRfidClothAll = this.getClothDistributeHandler().getK1MapRfidClothAll();
			clothTypeCountMap = this.getSessionClothTypeCountMap(SESSION_KEY_K1_CLOTHTYPE_COUNT_MAP);
		}
		else if (this.kioskName.equals(KioskName.kiosk2.toString()))
		{
			mapRfidClothAll = this.getClothDistributeHandler().getK2MapRfidClothAll();
			clothTypeCountMap = this.getSessionClothTypeCountMap(SESSION_KEY_K2_CLOTHTYPE_COUNT_MAP);
		}
		
		
		for (int i = 0; i < rfidToBeRemovedList.size(); i++)
		{
			String curRfid = rfidToBeRemovedList.get(i);
			Cloth curCloth = mapRfidClothAll.get(curRfid);
			
			mapRfidClothAll.remove(curRfid);
			this.receiptClothTotal = mapRfidClothAll.size();
			
			//System.out.println("RFID " + curRfid + " is removed form + " + this.kioskName + "!");
			
			
			// Very important to update the clothTypeCounter
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
			
		}
		
		this.clothTypeCountList = this.convertClothTypeCountMapToList(clothTypeCountMap);
	
		return "ajaxRemoveRfid";
	}
	
	private List<ClothTypeCounter> updateKioskClothTypeCountMap(String kioskSession, List<Cloth> clothList) throws Exception
	{
		// 1. get the cloth-type-count-map from session
		TreeMap<String, Integer> clothTypeCountMap = this.getSessionClothTypeCountMap(kioskSession);
		
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
		
		// 3. save the cloth-type-count-map to the session (MUST do to prevent error, said by Horace) 
		this.setSessionClothTypeCountMap(kioskSession, clothTypeCountMap);
		
		// 4. convert the cloth-type-count-map into a list so it can be sent back to HTML by json
		List<ClothTypeCounter> typeCountList = this.convertClothTypeCountMapToList(clothTypeCountMap);
		
		return typeCountList;
	}
	
	////////////////////////////////////////////////
	// Utility Method
	////////////////////////////////////////////////
	
	// Manually clear Session Var
	private void resetSessionVariables(String kioskSession)
	{
		getSession().put(kioskSession, null);
	}
	
	private ClothDistributeHandler getClothDistributeHandler()
	{
		if (handler == null)
			handler = SpringUtils.getBean(ClothDistributeHandler.BEANNAME);
		
		return handler;
	}
	
	private TreeMap<String, Integer> getSessionClothTypeCountMap(String kioskSession)
	{
		TreeMap<String, Integer> clothTypeCountMap = (TreeMap<String, Integer>) getSession().get(kioskSession);
		if (clothTypeCountMap == null)
		{
			clothTypeCountMap = new TreeMap<String, Integer>();
		}
		
		return clothTypeCountMap;
	}
	
	private void setSessionClothTypeCountMap(String kioskSession, TreeMap<String, Integer> clothTypeCountMap)
	{
		getSession().put(kioskSession, clothTypeCountMap);
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


	
	private Staff getStaffByCode(final String code)
	{
		Staff staff = null;
		List<Staff> staffList = getMasterService().findByExample(Staff.class, null, null, null, 
				new CustomCriteriaHandler<Staff>()
				{
					@Override
					public void makeCustomCriteria(Criteria baseCriteria)
					{
						baseCriteria.add(Restrictions.eq("code", code));
					}
				}, null, Order.asc("id"));
		
		if (staffList != null && staffList.size() == 1)
		{
			staff = staffList.get(0);
		}
		else
		{
			staff = null;
		}
		
		return staff;
	}
	
	private Staff getStaffByCard(final String cardNum)
	{
		Staff staff = null;
		List<Staff> staffList = getMasterService().findByExample(Staff.class, null, null, null, 
				new CustomCriteriaHandler<Staff>()
				{
					@Override
					public void makeCustomCriteria(Criteria baseCriteria)
					{
						baseCriteria.add(Restrictions.eq("cardNumber", cardNum));
					}
				}, null, Order.asc("id"));
		
		if (staffList != null && staffList.size() == 1)
		{
			staff = staffList.get(0);
		}
		else
		{
			staff = null;
		}
		
		return staff;
	}
	
	///////////////////////////////////////////
	// Getter and Setter
	///////////////////////////////////////////
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
	public Receipt getReceipt()
	{
		return receipt;
	}
	public void setReceipt(Receipt receipt)
	{
		this.receipt = receipt;
	}
	public Staff getStaffHandledBy()
	{
		return staffHandledBy;
	}
	public void setStaffHandledBy(Staff staffHandledBy)
	{
		this.staffHandledBy = staffHandledBy;
	}
	public Staff getStaffPickedBy()
	{
		return staffPickedBy;
	}
	public void setStaffPickedBy(Staff staffPickedBy)
	{
		this.staffPickedBy = staffPickedBy;
	}
	public String getKioskName()
	{
		return kioskName;
	}
	public void setKioskName(String kioskName)
	{
		this.kioskName = kioskName;
	}
	public Integer getReceiptClothTotal()
	{
		return receiptClothTotal;
	}
	public void setReceiptClothTotal(Integer receiptClothTotal)
	{
		this.receiptClothTotal = receiptClothTotal;
	}
	public String getSubreportPath()
	{
		return subreportPath;
	}
	public void setSubreportPath(String subreportPath)
	{
		this.subreportPath = subreportPath;
	}
	public List<ReceiptDistMainObject> getMainReportList()
	{
		return mainReportList;
	}
	public void setMainReportList(List<ReceiptDistMainObject> mainReportList)
	{
		this.mainReportList = mainReportList;
	}
	public List<ReceiptDistSub1Object> getSubreport1List()
	{
		return subreport1List;
	}
	public void setSubreport1List(List<ReceiptDistSub1Object> subreport1List)
	{
		this.subreport1List = subreport1List;
	}
	public List<ReceiptDistSub2Object> getSubreport2List()
	{
		return subreport2List;
	}
	public void setSubreport2List(List<ReceiptDistSub2Object> subreport2List)
	{
		this.subreport2List = subreport2List;
	}
}
