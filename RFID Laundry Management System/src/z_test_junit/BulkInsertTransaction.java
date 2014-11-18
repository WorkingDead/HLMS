package z_test_junit;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import module.dao.general.Transaction;
import module.dao.general.Transaction.TransactionName;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.master.Cloth;
import module.dao.master.Staff;
import module.dao.system.Users;

import module.service.ServiceFactory;
import module.service.all.GeneralService;
import module.service.all.MasterService;
import module.service.all.SystemService;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import web.actions.BaseActionSecurity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:databaseContext.xml", "classpath:applicationContext.xml"})
public class BulkInsertTransaction {

	@Resource(name=ServiceFactory.MasterService)
	public MasterService masterService;
	
	@Resource(name=ServiceFactory.GeneralService)
	public GeneralService generalService;
	
	@Resource(name=ServiceFactory.SystemService)
	private SystemService systemService;
	
	@Test
	public void test()
	{
		System.out.println("###############################################");
		System.out.println("Bulk Insert Transaction");
		System.out.println("###############################################");
		
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



		Cloth cloth = masterService.get(Cloth.class, 1L);
		Staff staff = masterService.get(Staff.class, 1L);
		
		Transaction[] transaction = new Transaction[100000];
		
		TransactionName[] transactionName = Transaction.TransactionName.values();
		Random rn = new Random();
		int minimum = 0;
		int maximum = transactionName.length - 1;
		
		for ( int i = 0; i < transaction.length; i++ ) {
			transaction[i] = new Transaction();
			transaction[i].setCloth( cloth );
			transaction[i].setTransactionName( transactionName[minimum + rn.nextInt(maximum - minimum + 1)] );
			transaction[i].setTransHandledByStaff( staff );
			transaction[i].setTransPickedByStaff( staff );
			transaction[i].setCreatedBy(user);
		}
		
		List<Transaction> list = Arrays.asList(transaction);
		
		generalService.saveList(Transaction.class, list);



		System.out.println("Num of Saved: " + list.size() );
		System.out.println("Finish! \n\n");
	}
}
