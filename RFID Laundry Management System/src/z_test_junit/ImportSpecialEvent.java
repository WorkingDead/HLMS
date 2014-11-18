package z_test_junit;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import module.dao.BaseBo;
import module.dao.general.SpecialEvent;
import module.dao.general.SpecialEvent.SpecialEventName;
import module.dao.general.SpecialEvent.SpecialEventStatus;
import module.dao.general.Transaction;
import module.dao.general.Transaction.TransactionName;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.Cloth.ClothStatus;
import module.dao.master.Staff;
import module.dao.system.Users;
import module.service.ServiceFactory;
import module.service.all.GeneralService;
import module.service.all.MasterService;
import module.service.all.SystemService;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import web.actions.BaseActionKiosk;
import web.actions.BaseActionSecurity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:databaseContext.xml", "classpath:applicationContext.xml"})
public class ImportSpecialEvent
{
	@Resource(name=ServiceFactory.MasterService)
	public MasterService masterService;
	
	@Resource(name=ServiceFactory.GeneralService)
	public GeneralService generalService;
	
	@Resource(name=ServiceFactory.SystemService)
	private SystemService systemService;
	
	@Test
	public void test()
	{
		final int NUM_OF_CLOTH = 10;		// this number must <= actual number of clothes
		final int NUM_OF_EVENT = 2;
		
		System.out.println("###############################################");
		System.out.println("Importing Special-Event");
		System.out.println("###############################################");
		
		///////////////////////////////////////////////////
		// System User
		///////////////////////////////////////////////////
		Users systemUser = systemService.findByExample(Users.class, null, null, null, 
				new CustomCriteriaHandler<Users>()
				{
					@Override
					public void makeCustomCriteria(Criteria baseCriteria)
					{
						baseCriteria.add(Restrictions.eq("username", BaseActionSecurity.AdminUserName));
					}
				}, 
				null, null).get(0);
		
		Users kioskUser = systemService.findByExample(Users.class, null, null, null, 
				new CustomCriteriaHandler<Users>()
				{
					@Override
					public void makeCustomCriteria(Criteria baseCriteria)
					{
						baseCriteria.add(Restrictions.eq("username", BaseActionKiosk.KioskUserName));
					}
				}, 
				null, null).get(0);
		
		///////////////////////////////////////////////////
		// Staff
		///////////////////////////////////////////////////
		List<Staff> staffList = masterService.findAll(Staff.class, null, null, null, Order.asc("id"));
		if (staffList == null || staffList.size() == 0)
		{
			String errorMsg = "[Warning] No Staff found!";
			System.out.println(errorMsg);
			Assert.fail(errorMsg);
		}
		
		
		///////////////////////////////////////////////////
		// Cloth
		///////////////////////////////////////////////////
		List<Cloth> clothList = masterService.findAll(Cloth.class, null, null, 

				new CustomLazyHandler<Cloth>()
				{
					@Override
					public void LazyList(List<Cloth> list)
					{
						for (int i = 0; i < list.size(); i++)
						{
							Cloth c = list.get(i);
							c.getStaff().getId();
						}
					}
				},
				
				Order.asc("id"));
		List<Cloth> myClothList = new ArrayList<Cloth>();
		if (clothList == null || clothList.size() == 0)
		{
			String errorMsg = "[Error] No clothes found!";
			System.out.println(errorMsg);
			Assert.fail(errorMsg);
		}
		else if (clothList.size() < NUM_OF_CLOTH)
		{
			String errorMsg = "[Error] Invalid number of clothes set!";
			System.out.println(errorMsg);
			Assert.fail(errorMsg);
		}
		else
		{
			for (int i = 0; i < NUM_OF_CLOTH; i++)
			{
				myClothList.add(clothList.get(i));
			}
		}
		
		///////////////////////////////////////////////////
		// Special Event
		///////////////////////////////////////////////////
		ArrayList<SpecialEvent> saveList = new ArrayList<SpecialEvent>();
		SpecialEvent tmpSpecialEvent = null;
		Cloth cloth = null;
		for (int j = 0; j < NUM_OF_EVENT; j++)
		{
			// Cloth Ironing return delay
			for (int i = 0; i < NUM_OF_CLOTH; i++)
			{
				cloth = myClothList.get(i);
				tmpSpecialEvent = new SpecialEvent();
				tmpSpecialEvent.setCloth(cloth);
				tmpSpecialEvent.setStaff(cloth.getStaff());
				tmpSpecialEvent.setSpecialEventName(SpecialEventName.ClothIroningDelay);
				tmpSpecialEvent.setSpecialEventStatus(SpecialEventStatus.Finished);
				tmpSpecialEvent.setCreatedBy(kioskUser);
				saveList.add(tmpSpecialEvent);
			}
			
			// Cloth lost
			for (int i = 0; i < NUM_OF_CLOTH; i++)
			{
				cloth = myClothList.get(i);
				tmpSpecialEvent = new SpecialEvent();
				tmpSpecialEvent.setCloth(null);
				tmpSpecialEvent.setStaff(cloth.getStaff());
				tmpSpecialEvent.setSpecialEventName(SpecialEventName.ClothLost);
				tmpSpecialEvent.setSpecialEventStatus(SpecialEventStatus.Followup);
				tmpSpecialEvent.setCreatedBy(kioskUser);
				saveList.add(tmpSpecialEvent);
			}
			
			// Cloth found
			for (int i = 0; i < NUM_OF_CLOTH; i++)
			{
				cloth = myClothList.get(i);
				tmpSpecialEvent = new SpecialEvent();
				tmpSpecialEvent.setCloth(cloth);
				tmpSpecialEvent.setStaff(cloth.getStaff());
				tmpSpecialEvent.setSpecialEventName(SpecialEventName.ClothFound);
				tmpSpecialEvent.setSpecialEventStatus(SpecialEventStatus.Finished);
				tmpSpecialEvent.setCreatedBy(kioskUser);
				saveList.add(tmpSpecialEvent);
			}
		}
		
		generalService.saveList(SpecialEvent.class, saveList);
		
		System.out.println("Num of Saved: " + saveList.size() );
		System.out.println("Finish! \n\n");
	}
}
