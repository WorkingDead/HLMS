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

@Component(ClothCollectionHandler.BEANNAME)
public class ClothCollectionHandler implements HandheldHandler
{
	private static final Logger log4j = Logger.getLogger(ClothCollectionHandler.class);
	
	public static final String BEANNAME = "ClothCollectionHandler";
	public static final String EVENT_NAME = "collect"; 					//must not change, Handheld use this name
	private static final String PARAMETER_STAFF_CODE = "staff_code"; 	//must not change, Handheld use this name

	private Map<String, Cloth> mapRfidClothAll = new HashMap<String, Cloth>();	// major purpose is to filter duplicate RFID
	private Map<String, Cloth> mapRfidClothNew = new HashMap<String, Cloth>();	// newly captured clothes are realtime appended to table in JSP
	private Map<String, Cloth> mapRfidClothOld = new HashMap<String, Cloth>();	// after JSP using clothes in mapRfidClothNew, these clothes will be put in mapRfidClothOld for later use
	private Staff handleByStaff = null;
	
	@Resource(name=ServiceFactory.MasterService)
	private MasterService masterService;
	
	@Override
	public void handle(HandheldEvent event)
	{
		Iterator<EventParameter> itParameter = event.getParameters().iterator();
		EventParameter parameter = null;
		while (itParameter.hasNext())
		{
			parameter = itParameter.next();
			if (parameter.getName().equals(PARAMETER_STAFF_CODE))
			{
				final String staffCode = parameter.getValue();
				
				List<Staff> staffList = this.masterService.findByExample(Staff.class, null, null, null, 
						new CustomCriteriaHandler<Staff>()
						{
							@Override
							public void makeCustomCriteria(Criteria criteria)
							{
								criteria.add(Restrictions.eq("code", staffCode));
							}
						}, 
						null, null);
				
				
				if (staffList != null && staffList.size() == 1)
				{
					this.handleByStaff = staffList.get(0);
				}
				else
				{
					this.handleByStaff = null;
				}
				
				break;
			}
		}
		
		List<EventItemList> rawItemList = new ArrayList<EventItemList>(event.getItemLists());
		
		// 1. find all newly-captured-RFID
		List<String> rfidListNew = this.getNewlyCapturedRfid(rawItemList, this.mapRfidClothAll);
		
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
					// Only clothes at "Using" status are added
					if (curCloth.getClothStatus().equals(ClothStatus.Using) )
					{
						mapRfidClothAll.put(curRfid, curCloth);
						mapRfidClothNew.put(curRfid, curCloth);
						log4j.info("Accept: " + curRfid);
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
	public void clearMapRfidClothAll()
	{
		this.mapRfidClothAll.clear();
	}
	public void clearMapRfidClothNew1()
	{
		this.mapRfidClothNew.clear();
	}
	public void moveNewClothMapToOldClothMap()
	{
		this.mapRfidClothOld.putAll(this.mapRfidClothNew);
	}
	public void clearMapRfidClothOld()
	{
		this.mapRfidClothOld.clear();
	}
	public void resetAllStaff()
	{
		this.handleByStaff = null;
	}
	
	
	////////////////////////////////////
	// Getter and Setter
	////////////////////////////////////
	public Map<String, Cloth> getMapRfidClothAll()
	{
		return mapRfidClothAll;
	}
	public void setMapRfidClothAll(Map<String, Cloth> mapRfidClothAll)
	{
		this.mapRfidClothAll = mapRfidClothAll;
	}
	public Map<String, Cloth> getMapRfidClothNew()
	{
		return mapRfidClothNew;
	}
	public void setMapRfidClothNew(Map<String, Cloth> mapRfidClothNew)
	{
		this.mapRfidClothNew = mapRfidClothNew;
	}
	public Map<String, Cloth> getMapRfidClothOld()
	{
		return mapRfidClothOld;
	}
	public void setMapRfidClothOld(Map<String, Cloth> mapRfidClothOld)
	{
		this.mapRfidClothOld = mapRfidClothOld;
	}
	public Staff getHandleByStaff()
	{
		return handleByStaff;
	}
	public void setHandleByStaff(Staff handleByStaff)
	{
		this.handleByStaff = handleByStaff;
	}
	
}
