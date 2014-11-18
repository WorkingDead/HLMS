package module.ale.handheld.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import org.springframework.stereotype.Component;

import module.ale.handheld.HandheldHandler;
import module.ale.handheld.event.EventItemList;
import module.ale.handheld.event.EventParameter;
import module.ale.handheld.event.HandheldEvent;

import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.Staff;
import module.dao.master.Cloth.ClothStatus;

import module.service.ServiceFactory;
import module.service.all.MasterService;

@Component(ClothAssociationHandler.BEANNAME)
public class ClothAssociationHandler implements HandheldHandler {
	
	private static final Logger log4j = Logger.getLogger(ClothAssociationHandler.class);
	
//	public static final String BEANNAME = "ClothRFIDAssociationHandler";
	public static final String BEANNAME = "ClothRFIDAssociationHandler";
	
	public static final String EVENT_NAME = "association"; //must not change, hh use this name
	private static final String PARAMETER_STAFF_CODE = "staff_code"; //must not change, hh use this name
	private static final String PARAMETER_CLOTH_TYPE = "cloth_type"; //must not change, hh use this name
	private static final String PARAMETER_CLOTH_SIZE = "cloth_size"; //must not change, hh use this name
	private static final String PARAMETER_CLOTH_CODE = "cloth_code"; //must not change, hh use this name
	
	private Map<String, Cloth> mapRfidClothAll = new HashMap<String, Cloth>();	// major purpose is to filter duplicate RFID
	private Map<String, Cloth> mapRfidClothNew = new HashMap<String, Cloth>();	// newly captured clothes are realtime appended to table in JSP
	private Map<String, Cloth> mapRfidClothOld = new HashMap<String, Cloth>();	// after JSP using clothes in mapRfidClothNew, these clothes will be put in mapRfidClothOld for later use
	
	@Resource(name=ServiceFactory.MasterService)
	private MasterService masterService;
	
	@Override
	public void handle(HandheldEvent event)
	{
		Iterator<EventParameter> itParameter = event.getParameters().iterator();
		EventParameter parameter = null;
		
		String staffCode = null;
		String clothTypeIdStr = null;
		String clothSize = null;
		String clothCode = null;
		while ( itParameter.hasNext() )
		{
			parameter = itParameter.next();
			if (parameter.getName().equals(PARAMETER_STAFF_CODE))
			{
				staffCode = parameter.getValue();
			}
			else if ( parameter.getName().equals(PARAMETER_CLOTH_TYPE) )
			{
				clothTypeIdStr = parameter.getValue();
			}
			else if ( parameter.getName().equals(PARAMETER_CLOTH_SIZE) )
			{
				clothSize = parameter.getValue();
			}
			else if ( parameter.getName().equals(PARAMETER_CLOTH_CODE) )
			{
				clothCode = parameter.getValue();
			}
		}
		
		log4j.info("staffCode: " + staffCode);
		log4j.info("clothType: " + clothTypeIdStr);
		log4j.info("clothSize: " + clothSize);
		log4j.info("clothCode: " + clothCode);
		
		Staff staff = null;
		ClothType clothType = null;
		if (staffCode != null)
		{
			staff = this.getStaffByCode(staffCode);
		}
		
		if (clothTypeIdStr != null)
		{
			clothType = this.getClothTypeById(Long.parseLong(clothTypeIdStr));
		}
		
		
		if (clothCode != null && staff != null && this.isClothCodeUsing(clothCode, clothType.getId(), staff.getId()))
		{
			log4j.info("Cloth code '" + clothCode + "' duplicated!");
			clothCode = null;
		}
		
		if (staff != null && clothType != null && clothSize != null && clothCode != null)
		{
			List<EventItemList> rawItemList = new ArrayList<EventItemList>(event.getItemLists());
			EventItemList rawItem = rawItemList.get(0);	// should be only 1 RFID in the list
			
			// Check if newly-captured-RFID already using
			String rfid = rawItem.getHex();
			if ( this.isRfidAvailable(rfid) )
			{
				// if RFID not found in DB
				Cloth cloth = new Cloth();
				cloth.setStaff(staff);
				cloth.setClothType(clothType);
				cloth.setSize(clothSize);
				cloth.setCode(clothCode);
				cloth.setClothStatus(ClothStatus.Using);
				
				//Record the original "rfid" for detecting the change in "onBeforeSave()" and "OnBeforeUpdate()"
				cloth.setOldRfid( cloth.getRfid() );
				cloth.setRfid(rfid);
				//Record the original "rfid" for detecting the change in "onBeforeSave()" and "OnBeforeUpdate()"
				
				cloth.setEnable(true);
				
				mapRfidClothAll.put(rfid, cloth);
				mapRfidClothNew.put(rfid, cloth);
				log4j.info("Accept: " + rfid);
			}
			else
			{
				log4j.info("Ignore! RFID '" + rfid + "' already USING! ");
			}
		}
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
	
	private ClothType getClothTypeById(Long id)
	{
		return this.masterService.get(ClothType.class, id);
	}
	
	private Staff getStaffByCode(final String code)
	{
		List<Staff> staffList = this.masterService.findByExample(Staff.class, null, null, null, 
				new CustomCriteriaHandler<Staff>()
				{
					@Override
					public void makeCustomCriteria(Criteria criteria)
					{
						criteria.add(Restrictions.eq("code", code));
					}
				}, 
				
				new CustomLazyHandler<Staff>()
				{
					@Override
					public void LazyList(List<Staff> list)
					{
						for (int j = 0; j < list.size(); j++)
						{
							list.get(j).getDept().getId();
							list.get(j).getClothSet().size();
						}
					}
				}, 
				
				
				null);
		
		Staff staff = null;
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
	
	private boolean isClothCodeUsing(final String clothCode, final Long clothTypeId, final Long staffId)
	{
		// 1. Check clothCode in DB
		Integer numOfFound = this.masterService.totalByExample(Cloth.class, null, 
				
				new CustomCriteriaHandler<Cloth>()
				{
					@Override
					public void makeCustomCriteria(Criteria criteria)
					{
						criteria.add(Restrictions.eq("code", clothCode));
						criteria.add(Restrictions.eq("clothType.id", clothTypeId));
						criteria.add(Restrictions.eq("staff.id", staffId));
					}
				}
		);
		if (numOfFound != null && numOfFound > 0)
			return true;
		
		// 2. Check clothCode newly captured
		List<Cloth> tmpClothList = new ArrayList<Cloth>(this.mapRfidClothAll.values());
		Cloth cloth = null;
		for (int i = 0; i < tmpClothList.size(); i++)
		{
			cloth = tmpClothList.get(i);
			if (cloth.getCode().equals(clothCode) && cloth.getClothType().getId().equals(clothTypeId) && cloth.getStaff().getId().equals(staffId))
			{
				return true;
			}
				
		}
		
		return false;	// Cloth code is not using
	}
	
	private boolean isRfidAvailable(final String rfid)
	{
		// 1. check in DB
		Integer numOfFound = this.masterService.totalByExample(Cloth.class, null, 
				
				new CustomCriteriaHandler<Cloth>()
				{
					@Override
					public void makeCustomCriteria(Criteria criteria)
					{
						criteria.add(Restrictions.eq("rfid", rfid));
					}
				});
		if (numOfFound != null && numOfFound > 0)
		{
			return false;
		}
		
		// 2. check newly-captured RFID
		if (this.mapRfidClothAll.containsKey(rfid))
		{
			return false;
		}
		
		return true;
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
}
