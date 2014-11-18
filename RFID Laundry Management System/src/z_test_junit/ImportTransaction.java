package z_test_junit;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import module.dao.general.Transaction;
import module.dao.general.Transaction.TransactionName;
import module.dao.iface.CustomCriteriaHandler;
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
public class ImportTransaction
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
		final int NUM_OF_CLOTH = 1;		// this number must <= actual number of clothes
		final int NUM_OF_TRANSACTION = 50;
		final ClothStatus clothStatusArray[] = new ClothStatus[] {
				ClothStatus.Using, 
				ClothStatus.Washing, 
				ClothStatus.Ironing,
				ClothStatus.Ready
		};
		int clothStatusPtr = 0;
		
		
		
		System.out.println("###############################################");
		System.out.println("Importing Cloth Transaction");
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
		Staff handleBy = null;
		Staff pickedBy = null;
		if (staffList != null && staffList.size() > 0)
		{
			if (staffList.size() >= 2)
			{
				handleBy = staffList.get(0);
				pickedBy = staffList.get(1);
			}
			else
			{
				pickedBy = handleBy = staffList.get(0);
			}
		}
		else
		{
			String errorMsg = "[Warning] No Staff found!";
			System.out.println(errorMsg);
			Assert.fail(errorMsg);
		}
		
		
		///////////////////////////////////////////////////
		// Cloth
		///////////////////////////////////////////////////
		List<Cloth> clothList = masterService.findAll(Cloth.class, null, null, null, Order.asc("id"));
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
		// Transactions
		///////////////////////////////////////////////////
		ArrayList<Transaction> saveList = new ArrayList<Transaction>();
		Transaction tmpTransaction = null;
		Cloth cloth = null;
		ClothStatus clothStatus = null;
		for (int j = 0; j < NUM_OF_TRANSACTION; j++)
		{
			clothStatus = clothStatusArray[clothStatusPtr];
			
			if (clothStatus.equals(ClothStatus.Washing))	// Cloth Collection
			{
				for (int i = 0; i < NUM_OF_CLOTH; i++)
				{
					cloth = myClothList.get(i);
					cloth.setClothStatus( clothStatus );
					
					tmpTransaction = new Transaction();
					tmpTransaction.setCloth(cloth);
					tmpTransaction.setTransactionName(TransactionName.Collection);
					tmpTransaction.setCreatedBy(kioskUser);
					tmpTransaction.setTransHandledByStaff(handleBy);
					tmpTransaction.setTransPickedByStaff(null);
					saveList.add(tmpTransaction);
				}
			}
			else if (clothStatus.equals(ClothStatus.Ironing))	// Cloth Ironing 
			{
				for (int i = 0; i < NUM_OF_CLOTH; i++)
				{
					cloth = myClothList.get(i);
					cloth.setClothStatus( clothStatus );
					
					tmpTransaction = new Transaction();
					tmpTransaction.setCloth(cloth);
					tmpTransaction.setTransactionName(TransactionName.IroningDelivery);
					tmpTransaction.setCreatedBy(systemUser);
					tmpTransaction.setTransHandledByStaff(null);
					tmpTransaction.setTransPickedByStaff(null);
					saveList.add(tmpTransaction);
				}
			}
			else if (clothStatus.equals(ClothStatus.Ready))	// Cloth on the rack are ready to pickup
			{
				for (int i = 0; i < NUM_OF_CLOTH; i++)
				{
					cloth = myClothList.get(i);
					cloth.setClothStatus( clothStatus );
					
					tmpTransaction = new Transaction();
					tmpTransaction.setCloth(cloth);
					tmpTransaction.setTransactionName(TransactionName.Racking);
					tmpTransaction.setCreatedBy(systemUser);
					tmpTransaction.setTransHandledByStaff(handleBy);
					tmpTransaction.setTransPickedByStaff(null);
					saveList.add(tmpTransaction);
				}
			}
			else if (clothStatus.equals(ClothStatus.Using))	// Cloth Distribution
			{
				for (int i = 0; i < NUM_OF_CLOTH; i++)
				{
					cloth = myClothList.get(i);
					cloth.setClothStatus( clothStatus );
					
					tmpTransaction = new Transaction();
					tmpTransaction.setCloth(cloth);
					tmpTransaction.setTransactionName(TransactionName.Distribution);
					tmpTransaction.setCreatedBy(kioskUser);
					tmpTransaction.setTransHandledByStaff(handleBy);
					tmpTransaction.setTransPickedByStaff(pickedBy);
					saveList.add(tmpTransaction);
				}
			}
			
			System.out.println("Cloth " + cloth.getCode() + ": " + cloth.getClothStatus());
			
			
			if (clothStatusPtr < clothStatusArray.length-1)
				clothStatusPtr++;
			else
				clothStatusPtr = 0;
		}
		
		generalService.saveList(Transaction.class, saveList);
		
		System.out.println("Num of Saved: " + saveList.size() );
		System.out.println("Finish! \n\n");
	}
}
