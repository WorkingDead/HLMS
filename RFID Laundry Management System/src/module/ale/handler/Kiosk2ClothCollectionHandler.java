package module.ale.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import module.ale.ECSpecHandler;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.Cloth.ClothStatus;
import module.service.ServiceFactory;
import module.service.all.MasterService;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import epcglobal.ale.xsd._1.ECReports;

@Component(Kiosk2ClothCollectionHandler.BEANNAME)
public class Kiosk2ClothCollectionHandler extends CaptureButtonBaseHandler implements ECSpecHandler
{
	private static final Logger log4j = Logger.getLogger(Kiosk2ClothCollectionHandler.class);
	
	public static final String BEANNAME = "Kiosk2ClothCollectionHandler";
	public static final String SPECNAME = "Kiosk2ClothCollectionHandler";
	
	private Map<String, Cloth> mapRfidClothAll = new HashMap<String, Cloth>();
	private Map<String, Cloth> mapRfidClothNew = new HashMap<String, Cloth>();
	
	@Resource(name=ServiceFactory.MasterService)
	private MasterService masterService;
	
	@Override
	public void handle(List<String> rawHexs, ECReports ecReports)
	{
		log4j.info("rawHexs: " + rawHexs.size());
		
		if (enable)
		{
			// 1. find all newly-captured-RFID
			List<String> rfidListNew = this.getNewlyCapturedRfid(rawHexs, this.mapRfidClothAll);
			
			// 2. if have, get newly-captured-using-status-RFID info from DB
			if (rfidListNew.size() > 0)
			{
				List<Cloth> clothList = this.getClothListByRfid(rfidListNew);
				
				// 3. put all newly-captured-RFID to map
				Cloth curCloth = null;
				String curRfid = null;
				synchronized (this)
				{
					for (int i = 0; i < clothList.size(); i++)
					{
						curCloth = clothList.get(i);
						curRfid = curCloth.getRfid();
						
						// Only clothes at "Using" status are added
						if (curCloth.getClothStatus().equals(ClothStatus.Using))
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
		else
		{
			log4j.info("Not Capturing! Captured data discarded!");
		}
	}
		
	
	private List<String> getNewlyCapturedRfid( List<String> rawRfidList, Map<String, Cloth> rfidClothMap )
	{
		List<String> rfidListFiltered = new ArrayList<String>();
		String curRfid = null;
		for (int i = 0; i < rawRfidList.size(); i++)
		{
			curRfid = rawRfidList.get(i);
			
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
			clothList = masterService.findByExample(Cloth.class, example, null, null, 

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
			log4j.info("Invalid cloth(s) found!");
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
	public void clearMapRfidClothNew()
	{
		this.mapRfidClothNew.clear();
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
}
