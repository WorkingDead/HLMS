package z_test_junit;

import java.util.List;
import javax.annotation.Resource;
import module.dao.BeansFactoryApplication;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.master.Cloth;
import module.dao.master.Cloth.ClothStatus;
import module.dao.system.Users;
import module.service.ServiceFactory;
import module.service.all.MasterService;
import module.service.all.SystemService;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import web.actions.BaseActionSecurity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:databaseContext.xml", "classpath:applicationContext.xml"})
public class ImportChangeClothStatus
{
	@Resource(name=ServiceFactory.MasterService)
	public MasterService masterService;
	
	@Resource(name=ServiceFactory.SystemService)
	private SystemService systemService;
	
	@Resource(name=BeansFactoryApplication.BEANNAME)
	private BeansFactoryApplication beansFactoryApplication;
	
	@Test
	public void test()
	{
		System.out.println("###############################################");
		System.out.println("Change clothes' status");
		System.out.println("###############################################");
		
//		ClothStatus status = ClothStatus.Washing;
//		ClothStatus status = ClothStatus.Ironing;
//		ClothStatus status = ClothStatus.Ready;
		ClothStatus status = ClothStatus.Using;
		
		Integer washingCount = 0;
		
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
		
		
		List<Cloth> saveList = masterService.findAll(Cloth.class, null, null, null, Order.asc("id"));
		Cloth cloth = null;
		System.out.println("NumOfFound: " + saveList.size());
		for (int i = 0; i < saveList.size(); i++)
		{
			cloth = saveList.get(i);
			cloth.setClothStatus(status);
			cloth.setModifiedBy(user);
			
			cloth.setWashingCount(washingCount);
			
			masterService.save(Cloth.class, cloth);
			System.out.print(".");
		}
		System.out.println(".");
		
//		masterService.saveList(Cloth.class, saveList);
		
		System.out.println("Num of Updated: " + saveList.size() );
		System.out.println("Finish! \n\n");
	}
}
