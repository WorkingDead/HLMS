package module.ale.handheld.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import web.actions.BaseActionKiosk.KioskName;
import module.ale.handheld.HandheldHandler;
import module.ale.handheld.event.EventItemList;
import module.ale.handheld.event.EventParameter;
import module.ale.handheld.event.HandheldEvent;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.Staff;
import module.dao.master.Cloth.ClothStatus;
import module.service.ServiceFactory;
import module.service.all.MasterService;

@Component(ClothDistributeHandler.BEANNAME)
public class ClothDistributeHandler implements HandheldHandler
{
	private static final Logger log4j = Logger.getLogger(ClothDistributeHandler.class);
	
	public static final String BEANNAME = "ClothDistributeHandler";
	
	public static final String EVENT_NAME = "take_out"; //must not change, hh use this name
	private static final String PARAMETER_TAKE_STAFF_CODE = "take_staff_code"; //must not change, hh use this name
	private static final String PARAMETER_PROCESS_STAFF_CODE = "process_staff_code"; //must not change, hh use this name
	
	// Kiosk 1
	private Staff k1HandleByStaff = null;
	private Staff k1PickedByStaff = null;
	private Map<String, Cloth> k1MapRfidClothAll = new HashMap<String, Cloth>();	// major purpose is to filter duplicate RFID
	private Map<String, Cloth> k1MapRfidClothNew = new HashMap<String, Cloth>();	// newly captured clothes are realtime appended to table in JSP
	private Map<String, Cloth> k1MapRfidClothOld = new HashMap<String, Cloth>();	// after JSP using clothes in mapRfidClothNew, these clothes will be put in mapRfidClothOld for later use
	
	// Kiosk 2
	private Staff k2HandleByStaff = null;
	private Staff k2PickedByStaff = null;
	private Map<String, Cloth> k2MapRfidClothAll = new HashMap<String, Cloth>();	// major purpose is to filter duplicate RFID
	private Map<String, Cloth> k2MapRfidClothNew = new HashMap<String, Cloth>();	// newly captured clothes are realtime appended to table in JSP
	private Map<String, Cloth> k2MapRfidClothOld = new HashMap<String, Cloth>();	// after JSP using clothes in mapRfidClothNew, these clothes will be put in mapRfidClothOld for later use
	
	@Resource(name=ServiceFactory.MasterService)
	private MasterService masterService;
	
	@Override
	public void handle(HandheldEvent event)
	{
		String kioskName = event.getKioskName();
		log4j.info("kioskName: " + kioskName);
		
		Iterator<EventParameter> itParameter = event.getParameters().iterator();
		EventParameter parameter = null;
		
		Staff tmpHandleByStaff = null;	// can be empty from Kiosk
		Staff tmpPickedByStaff = null;	// can be empty from Kiosk
		while (itParameter.hasNext() && (tmpHandleByStaff == null || tmpPickedByStaff == null))
		{
			parameter = itParameter.next();
			
			if (parameter.getName().equals(PARAMETER_PROCESS_STAFF_CODE))
			{
				final String staffCode = parameter.getValue();
				tmpHandleByStaff= this.getStaffByCode(staffCode);
			}
			else if (parameter.getName().equals(PARAMETER_TAKE_STAFF_CODE))
			{
				final String staffCode = parameter.getValue();
				tmpPickedByStaff = this.getStaffByCode(staffCode);
			}
		}
		
		////////////////////////////////////////////
		// Kiosk storage
		////////////////////////////////////////////
		List<EventItemList> rawItemList = new ArrayList<EventItemList>(event.getItemLists());
		if (kioskName.equals(KioskName.kiosk1.toString()))
		{
			this.k1HandleByStaff = tmpHandleByStaff;
			this.k1PickedByStaff = tmpPickedByStaff;
			this.sendDataToKioskCache(rawItemList, k1MapRfidClothAll, k1MapRfidClothNew);
		}
		else
		{
			this.k2HandleByStaff = tmpHandleByStaff;
			this.k2PickedByStaff = tmpPickedByStaff;
			this.sendDataToKioskCache(rawItemList, k2MapRfidClothAll, k2MapRfidClothNew);
		}
	}
	
	private void sendDataToKioskCache(List<EventItemList> rawItemList, 
			Map<String, Cloth> mapRfidClothAll, Map<String, Cloth> mapRfidClothNew)
	{
		// 1. find all newly-captured-RFID
		List<String> rfidListNew = this.getNewlyCapturedRfid(rawItemList, mapRfidClothAll);
		
		// 2. if have, get newly-captured-RFID info from DB
		if (rfidListNew.size() > 0)
		{
			List<Cloth> clothList = this.getClothListByRfid(rfidListNew);
			
			// 3. put all newly-captured-washing-status-RFID to map
			Cloth curCloth = null;
			String curRfid = null;
			synchronized (this)
			{
				for (int i = 0; i < clothList.size(); i++)
				{
					curCloth = clothList.get(i);
					curRfid = curCloth.getRfid();
					// Only clothes at "Ironing" status are added
					if (curCloth.getClothStatus().equals(ClothStatus.Ready) )
					{
//						curCloth.setZone(null);
						mapRfidClothAll.put(curRfid, curCloth);
						mapRfidClothNew.put(curRfid, curCloth);
						log4j.info("Accept: " + curRfid + "\t at " + curCloth.getZone().getCode());
					}
					else
					{
						log4j.info("Ignore: " + curRfid);
					}
				}
			}
		}
	}
	
	
	private List<String> getNewlyCapturedRfid( List<EventItemList> rawItemList, Map<String, Cloth> rfidClothMap )
	{
		List<String> rfidListFiltered = new ArrayList<String>();
		EventItemList curItem = null;
		String curRfid = null;
		for (int i = 0; i < rawItemList.size(); i++)
		{
			curItem = rawItemList.get(i);
			curRfid = curItem.getHex();
			
			if (rfidClothMap.containsKey(curRfid))
			{
				// nothing to do
				log4j.info("Skip! " + curRfid);
			}
			else
			{
				rfidListFiltered.add(curRfid);
			}
		}
		return rfidListFiltered;
	}
	
	private List<Cloth> getClothListByRfid(List<String> rfidList)
	{
		List<Cloth> clothList = null;
		if ( rfidList != null && rfidList.size() > 0)
		{
			Disjunction cases = Restrictions.disjunction();
			for (int i = 0; i < rfidList.size(); i++)
			{
				String tmpRfid = rfidList.get(i);
				cases.add( Restrictions.eq("rfid", tmpRfid) );
			}
			final Disjunction disjunction = cases;
			
			clothList = new ArrayList<Cloth>();
			Cloth example = new Cloth();
			clothList = this.masterService.findByExample(Cloth.class, example, null, null, 

					new CustomCriteriaHandler<Cloth>()
					{
						@Override
						public void makeCustomCriteria(Criteria baseCriteria)
						{
							baseCriteria.add( disjunction );
						}
					},
					
					new CustomLazyHandler<Cloth>()
					{
						@Override
						public void LazyList(List<Cloth> list)
						{
							for (int i = 0; i < list.size(); i++)
							{
								Cloth tmpCloth = list.get(i);
								tmpCloth.getClothType().getId();
								tmpCloth.getStaff().getId();
								tmpCloth.getStaff().getDept().getId();
								
								if (tmpCloth.getZone() != null)
								{
									tmpCloth.getZone().getId();
								}
								
							}
						}
					}, 
					
					Order.asc("id"));
		}
		
		if (clothList == null || clothList.size() == 0)
		{
			log4j.info("No/Duplicate/Invalid cloth(s) found!");
		}
		
		return clothList;
	}
	
	
	////////////////////////////////////
	// Utility Method
	////////////////////////////////////
	
	// Kiosk 1
	public void clearK1MapRfidClothAll()
	{
		this.k1MapRfidClothAll.clear();
	}
	public void clearK1MapRfidClothNew1()
	{
		this.k1MapRfidClothNew.clear();
	}
	public void moveK1MapRfidClothNewToOld()
	{
		this.k1MapRfidClothOld.putAll(this.k1MapRfidClothNew);
	}
	public void clearK1MapRfidClothOld()
	{
		this.k1MapRfidClothOld.clear();
	}
	
	public void resetK1AllStaff()
	{
		this.k1HandleByStaff = null;
		this.k1PickedByStaff = null;
	}
	
	// Kiosk 2
	public void clearK2MapRfidClothAll()
	{
		this.k2MapRfidClothAll.clear();
	}
	public void clearK2MapRfidClothNew1()
	{
		this.k2MapRfidClothNew.clear();
	}
	public void moveK2MapRfidClothNewToOld()
	{
		this.k2MapRfidClothOld.putAll(this.k2MapRfidClothNew);
	}
	public void clearK2MapRfidClothOld()
	{
		this.k2MapRfidClothOld.clear();
	}
	public void resetK2AllStaff()
	{
		this.k2HandleByStaff = null;
		this.k2PickedByStaff = null;
	}
	
	
	private Staff getStaffByCode(final String code)
	{
		Staff staff = null;
		List<Staff> staffList = this.masterService.findByExample(Staff.class, null, null, null, 
				new CustomCriteriaHandler<Staff>()
				{
					@Override
					public void makeCustomCriteria(Criteria criteria)
					{
						criteria.add(Restrictions.eq("code", code));
					}
				}, 
				null, null);
		
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

	public Map<String, Cloth> getK1MapRfidClothAll()
	{
		return k1MapRfidClothAll;
	}
	public void setK1MapRfidClothAll(Map<String, Cloth> k1MapRfidClothAll)
	{
		this.k1MapRfidClothAll = k1MapRfidClothAll;
	}
	public Map<String, Cloth> getK1MapRfidClothNew()
	{
		return k1MapRfidClothNew;
	}
	public void setK1MapRfidClothNew(Map<String, Cloth> k1MapRfidClothNew)
	{
		this.k1MapRfidClothNew = k1MapRfidClothNew;
	}
	public Map<String, Cloth> getK1MapRfidClothOld()
	{
		return k1MapRfidClothOld;
	}
	public void setK1MapRfidClothOld(Map<String, Cloth> k1MapRfidClothOld)
	{
		this.k1MapRfidClothOld = k1MapRfidClothOld;
	}
	public Map<String, Cloth> getK2MapRfidClothAll()
	{
		return k2MapRfidClothAll;
	}
	public void setK2MapRfidClothAll(Map<String, Cloth> k2MapRfidClothAll)
	{
		this.k2MapRfidClothAll = k2MapRfidClothAll;
	}
	public Map<String, Cloth> getK2MapRfidClothNew()
	{
		return k2MapRfidClothNew;
	}
	public void setK2MapRfidClothNew(Map<String, Cloth> k2MapRfidClothNew)
	{
		this.k2MapRfidClothNew = k2MapRfidClothNew;
	}
	public Map<String, Cloth> getK2MapRfidClothOld()
	{
		return k2MapRfidClothOld;
	}
	public void setK2MapRfidClothOld(Map<String, Cloth> k2MapRfidClothOld)
	{
		this.k2MapRfidClothOld = k2MapRfidClothOld;
	}
	public Staff getK1HandleByStaff()
	{
		return k1HandleByStaff;
	}
	public void setK1HandleByStaff(Staff k1HandleByStaff)
	{
		this.k1HandleByStaff = k1HandleByStaff;
	}
	public Staff getK1PickedByStaff()
	{
		return k1PickedByStaff;
	}
	public void setK1PickedByStaff(Staff k1PickedByStaff)
	{
		this.k1PickedByStaff = k1PickedByStaff;
	}
	public Staff getK2HandleByStaff()
	{
		return k2HandleByStaff;
	}
	public void setK2HandleByStaff(Staff k2HandleByStaff)
	{
		this.k2HandleByStaff = k2HandleByStaff;
	}
	public Staff getK2PickedByStaff()
	{
		return k2PickedByStaff;
	}
	public void setK2PickedByStaff(Staff k2PickedByStaff)
	{
		this.k2PickedByStaff = k2PickedByStaff;
	}
	public MasterService getMasterService()
	{
		return masterService;
	}
	public void setMasterService(MasterService masterService)
	{
		this.masterService = masterService;
	}
}
