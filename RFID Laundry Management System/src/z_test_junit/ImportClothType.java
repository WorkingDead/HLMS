package z_test_junit;
import java.util.ArrayList;
import javax.annotation.Resource;
import module.dao.BeansFactoryApplication;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.master.ClothType;
import module.dao.system.Users;
import module.service.ServiceFactory;
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
public class ImportClothType
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
		System.out.println("Importing ClothType");
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
		
		String allClothTypeName = beansFactoryApplication.getAllClothTypeName();
		String clothTypeNameArray[] = allClothTypeName.split(",");
		
		ArrayList<ClothType> saveList = new ArrayList<ClothType>();
		ClothType clothType = null;
		for (int i = 0; i < clothTypeNameArray.length; i++)
		{
			clothType = new ClothType();
			clothType.setName(clothTypeNameArray[i]);
			clothType.setEnable(true);
			clothType.setCreatedBy(user);
			saveList.add(clothType);
			
			System.out.println("clothType " + i + ": " + clothType.getName() );
		}
		
		masterService.saveList(ClothType.class, saveList);
		
		System.out.println("Num of Saved: " + saveList.size() );
		System.out.println("Finish! \n\n");
	}

}
