package z_test_junit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import javax.annotation.Resource;
import module.dao.general.Receipt;
import module.dao.general.Receipt.ReceiptStatus;
import module.dao.general.Receipt.ReceiptType;
import module.dao.general.ReceiptPatternIron;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.master.Cloth;
import module.dao.master.RollContainer;
import module.dao.master.Staff;
import module.dao.master.Cloth.ClothStatus;
import module.dao.system.Users;
import module.service.ServiceFactory;
import module.service.all.GeneralService;
import module.service.all.MasterService;
import module.service.all.SystemService;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.ibm.icu.impl.Assert;
import web.actions.BaseActionSecurity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:databaseContext.xml", "classpath:applicationContext.xml"})
public class ImportReceipt
{
	@Resource(name=ServiceFactory.MasterService)
	public MasterService masterService;
	
	@Resource(name=ServiceFactory.GeneralService)
	public GeneralService generalService;
	
	@Resource(name=ServiceFactory.SystemService)
	public SystemService systemService;
	
	@Test
	public void test()
	{
		System.out.println("###############################################");
		System.out.println("Importing Receipt");
		System.out.println("###############################################");
		
		///////////////////////////////////
		// Some Parameters
		///////////////////////////////////
		final int NUM_OF_EACH_RECEIPT = 1;		// e.g. ironing x 10, collect x 10, ...
		
		final int COLLECT = 1;
		final int IRONING = 2;
		final int RACKING = 3;
		final int DISTRIBUTE = 4;
		int importReceiptType = COLLECT;
		
		///////////////////////////////////
		// Data Preparation
		///////////////////////////////////
		Users user = systemService.findByExample(Users.class, null, null, null, 
				new CustomCriteriaHandler<Users>()
				{
					@Override
					public void makeCustomCriteria(Criteria baseCriteria)
					{
						baseCriteria.add(Restrictions.eq("username", BaseActionSecurity.AdminUserName));
					}
				}, 
				null, null).get(0);
		
		ArrayList<Cloth> clothList = (ArrayList<Cloth>) masterService.findAll(Cloth.class, null, null, null, Order.asc("id"));
		ArrayList<RollContainer> cartList = (ArrayList<RollContainer>) masterService.findAll(RollContainer.class, null, null, null, Order.asc("id"));
		ArrayList<Staff> staffList = (ArrayList<Staff>) masterService.findAll(Staff.class, null, null, null, Order.asc("id"));
		int staffPtr = 0;
		
		if (cartList == null || cartList.size() == 0)
		{
			String errorMsg = "No Roll-Containers found!";
			System.out.println("Error: " + errorMsg);
			Assert.fail(errorMsg);
		}
		if (clothList == null || clothList.size() == 0)
		{
			String errorMsg = "No Clothes found!";
			System.out.println("Error: " + errorMsg);
			Assert.fail(errorMsg);
		}
		
		
		//////////////////////////////////////////////////////////////
		// Save to DB
		//////////////////////////////////////////////////////////////
//		ArrayList<Receipt> saveList = new ArrayList<Receipt>();
		Receipt receipt = null;
		Staff staffHandleBy = null;
		Staff staffPickedBy = null;
		int numOfSaved = 0;
		for (int x = 0; x < NUM_OF_EACH_RECEIPT; x++)
		{
			if (importReceiptType == COLLECT)
			{
				/////////////////////////////////////
				// Receipt: Cloth Collection / Wash
				/////////////////////////////////////
				receipt = new Receipt();
				receipt.setCode(generalService.genKioskClothCollectionReceiptCode());
				receipt.setReceiptType(ReceiptType.Collect);
				receipt.setReceiptStatus(ReceiptStatus.Processing);
				receipt.setReceiptClothTotal(clothList.size());
				for (int i = 0; i < clothList.size(); i++)
				{
					Cloth c = clothList.get(i);
					c.setClothStatus(ClothStatus.Washing);
				}
				receipt.setClothSet(new HashSet<Cloth>(clothList));
				receipt.setCreatedBy(user);
				
				
				staffHandleBy = staffList.get(staffPtr);
				receipt.setStaffHandledBy(staffHandleBy);
				if (staffPtr < staffList.size()-1)
					staffPtr++;
				else
					staffPtr = 0;
				
				
				generalService.saveReceiptAndTransaction(receipt);
				numOfSaved++;
			}
			
			if (importReceiptType == IRONING)
			{
				/////////////////////////////////////
				// Receipt: Ironing Delivery
				/////////////////////////////////////
				ArrayList<ReceiptPatternIron> patternList = new ArrayList<ReceiptPatternIron>();
				int numOfPattern = cartList.size();
				ReceiptPatternIron pattern = null;
				for (int i = 0; i < numOfPattern; i++)
				{
					pattern = new ReceiptPatternIron();
					pattern.setRollContainer( cartList.get(i) );
					pattern.setPatternClothTotal(0);
					pattern.setClothSet( new HashSet<Cloth>() );
					Calendar ironTime = Calendar.getInstance();
					pattern.setIroningDeliveryTime(ironTime);
					pattern.setCreatedBy(user);
					patternList.add(pattern);
				}
				
				int patternPtr = 0;
				for (int i = 0; i < clothList.size(); i++)
				{
					Cloth c = clothList.get(i);
					c.setClothStatus(ClothStatus.Ironing);
					pattern = patternList.get(patternPtr);
					pattern.getClothSet().add(c);
					pattern.setPatternClothTotal(  pattern.getPatternClothTotal() + 1);
					
					if (patternPtr < numOfPattern-1)
						patternPtr++;
					else
						patternPtr = 0;
				}
				
				receipt = new Receipt();
				receipt.setCode(generalService.genIroningDeliveryReceiptCode());
				receipt.setReceiptType(ReceiptType.Iron);
				receipt.setReceiptStatus(ReceiptStatus.Processing);
				receipt.setReceiptPatternIronSet( new HashSet<ReceiptPatternIron>(patternList));
				receipt.setReceiptClothTotal(clothList.size());
				receipt.setCreatedBy(user);
				generalService.saveReceiptAndTransaction(receipt);
				numOfSaved++;
			}
			
			if (importReceiptType == RACKING)
			{
				
			}
			
			if (importReceiptType == DISTRIBUTE)
			{
				/////////////////////////////////////
				// Receipt: Cloth Distribute
				/////////////////////////////////////
				receipt = new Receipt();
				receipt.setCode(generalService.genKioskClothDistributeReceiptCode());
				receipt.setReceiptType(ReceiptType.Distribute);
				receipt.setReceiptStatus(ReceiptStatus.Finished);
				receipt.setReceiptClothTotal(clothList.size());
				for (int i = 0; i < clothList.size(); i++)
				{
					Cloth c = clothList.get(i);
					c.setClothStatus(ClothStatus.Using);
				}
				receipt.setClothSet( new HashSet<Cloth>(clothList) );
				receipt.setCreatedBy(user);
				
				receipt.setStaffHandledBy(staffList.get(staffPtr));
				if (staffPtr < staffList.size()-1)
					staffPtr++;
				else
					staffPtr = 0;
				
				receipt.setStaffPickedBy(staffList.get(staffPtr));
				if (staffPtr < staffList.size()-1)
					staffPtr++;
				else
					staffPtr = 0;
				
				generalService.saveReceiptAndTransaction(receipt);
				numOfSaved++;
			}
		}
		
		System.out.println("Num of Saved: " + numOfSaved );
		System.out.println("Finish! \n\n");
	}
}
