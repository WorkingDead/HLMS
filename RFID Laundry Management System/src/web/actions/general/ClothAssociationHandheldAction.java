package web.actions.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import module.ale.handheld.HandheldHandlerFactory;
import module.ale.handheld.handler.ClothAssociationHandler;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.master.Cloth;
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
import utils.spring.SpringUtils;
import web.actions.BaseActionGeneral;
import web.actions.form.ClothTypeCounter;

@Results({
	// ensure that the page can only be accessed by one user at same time
//	@Result(name="checkPageMaster", type="json", params = {
//			"includeProperties" , 	"masterIsMe"
//	}),

	@Result(name="getClothInfoJson", type="json", params = {
			"includeProperties" , 	"clothList\\[\\d+\\]\\.code, " +
									"clothList\\[\\d+\\]\\.size, " +
									"clothList\\[\\d+\\]\\.rfid, " +
									"clothList\\[\\d+\\]\\.clothType\\.name, " + 
									"clothList\\[\\d+\\]\\.staff\\.code, " + 
									"clothList\\[\\d+\\]\\.staff\\.nameCht, " +
									"clothList\\[\\d+\\]\\.staff\\.nameEng, " + 
									"clothList\\[\\d+\\]\\.staff\\.dept\\.nameCht, " +
									"clothList\\[\\d+\\]\\.staff\\.dept\\.nameEng, " +
									"clothTotal, " + 
									"clothTypeCountList\\[\\d+\\]\\.type, " + 
									"clothTypeCountList\\[\\d+\\]\\.num"
	}),

	@Result(name="ajaxRemoveRfid", type="json", params = {
			"includeProperties" , 	"clothTotal, " +
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
public class ClothAssociationHandheldAction extends BaseActionGeneral
{
	private static final long serialVersionUID = -2857645601833422842L;
	private static final Logger log4j = Logger.getLogger(ClothAssociationHandheldAction.class);
	
	// Session
	private static final String SESSION_KEY_CLOTHTYPE_COUNT_MAP = "SESSION_KEY_CLOTHTYPE_COUNT_MAP";
	
	private Integer clothTotal;
	private List<String> rfidToBeRemovedList;
	private List<ClothTypeCounter> clothTypeCountList;
	private List<Cloth> clothList;
	private ClothAssociationHandler clothAssoHandler;
	
	public String getMainPage()
	{
//		if ( !isPageAvailable() )
//		{
//			this.setTilesKey("handheld.cloth.association.page.in.use");
//			return TILES;
//		}
		
		//System.out.println( "Get Handheld Cloth Asso Page!" );
		this.resetSessionVariables();
		
		// clothes read by Handheld but already "real-time-appended-to-table" before
		this.clothList = new ArrayList<Cloth>();
		this.clothList.addAll(this.getClothAssoHandler().getMapRfidClothOld().values());
		this.clothTotal = this.clothList.size();
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
		
		this.setTilesKey("handheld.cloth.association.main");
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
			Map<String, Cloth> mapRfidClothAll = this.getClothAssoHandler().getMapRfidClothAll();
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
			createImpl();
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
		Map<String, Cloth> mapRfidClothAll = this.getClothAssoHandler().getMapRfidClothAll();
		List<Cloth> tmpClothList = new ArrayList<Cloth>(mapRfidClothAll.values());
		List<Staff> staffList = this.groupClothByStaff(tmpClothList);
		this.getMasterService().saveList(Staff.class, staffList);
		this.resetPage();
	}
	
	
	private List<Staff> groupClothByStaff(List<Cloth> clothList)
	{
		// group clothes by staff
		HashMap<Long, Staff> staffMap = new HashMap<Long, Staff>();
		Cloth cloth = null;
		Staff staff = null;
		Long staffId = null;
		for (int i = 0; i < clothList.size(); i++)
		{
			cloth = clothList.get(i);
			staff = cloth.getStaff();
			staffId = staff.getId();
			
			if (staffMap.containsKey(staffId))
			{
				staff = staffMap.get(staffId);
				staff.getClothSet().add(cloth);
			}
			else
			{
//				staff = this.getMasterService().get(Staff.class, staffId);
				staff.getClothSet().add(cloth);
				staffMap.put(staffId, staff);
			}
			
			cloth.setStaff(null);	// prevent hibernate error
		}
		
		List<Staff> staffList = new ArrayList<Staff>(staffMap.values());
		return staffList;
	}
	
	
	public synchronized String getCapturedRfidJson()
	{
//		if ( isActionValid() )
//		{
			////////////////////////////////////////////////
			// For display
			////////////////////////////////////////////////
			this.clothList = new ArrayList<Cloth>();
			this.clothList.addAll(this.getClothAssoHandler().getMapRfidClothNew().values());
			this.clothTotal = this.getClothAssoHandler().getMapRfidClothAll().size();
			
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
			
			// [Important] clear the newly capture list and move data to old list
			this.getClothAssoHandler().moveNewClothMapToOldClothMap();
			this.getClothAssoHandler().clearMapRfidClothNew1();
//		}
//		else
//		{
//			// Empty List return through json to prevent js/jquery error
//			this.clothList = new ArrayList<Cloth>();
//			this.clothTypeCountList = new ArrayList<ClothTypeCounter>();
//			this.clothTotal = 0;
//		}
		
		return "getClothInfoJson";
	}
	
	public synchronized String ajaxRemoveRfid()
	{
//		if ( isActionValid() )
//		{
			Map<String, Cloth> mapRfidClothAll = this.getClothAssoHandler().getMapRfidClothAll();
			Map<String, Cloth> mapRfidClothOld = this.getClothAssoHandler().getMapRfidClothOld();
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
			
			// 3. update the cloth-total
			this.clothTotal = mapRfidClothAll.size();
			
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

	public void resetPage()
	{
		//System.out.println("Reset Page!");
		this.resetSessionVariables();
		this.getClothAssoHandler().clearMapRfidClothAll();
		this.getClothAssoHandler().clearMapRfidClothNew1();
		this.getClothAssoHandler().clearMapRfidClothOld();
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

	public ClothAssociationHandler getClothAssoHandler()
	{
		if (clothAssoHandler == null)
		{
			HandheldHandlerFactory factory = SpringUtils.getBean(HandheldHandlerFactory.BEANNAME);
			clothAssoHandler = (ClothAssociationHandler) factory.getHandler(ClothAssociationHandler.EVENT_NAME);
		}
		
		return clothAssoHandler;
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



	
	////////////////////////////////////////////////
	// Getter and Setter
	////////////////////////////////////////////////
	public Integer getClothTotal()
	{
		return clothTotal;
	}
	public void setClothTotal(Integer clothTotal)
	{
		this.clothTotal = clothTotal;
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
	public List<Cloth> getClothList()
	{
		return clothList;
	}
	public void setClothList(List<Cloth> clothList)
	{
		this.clothList = clothList;
	}
}
