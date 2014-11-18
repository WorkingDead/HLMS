package z_test_junit;
import java.util.ArrayList;
import javax.annotation.Resource;

import module.dao.iface.CustomCriteriaHandler;
import module.dao.master.Zone;
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
public class ImportZone
{
	@Resource(name=ServiceFactory.MasterService)
	public MasterService masterService;
	
	@Resource(name=ServiceFactory.SystemService)
	private SystemService systemService;
	
	@Test
	public void test()
	{
		System.out.println("###############################################");
		System.out.println("Importing Zone");
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
		
		ArrayList<Zone> saveList = new ArrayList<Zone>();
		for (int i = 1; i <= 5; i++)
		{
			String code = "Z" + i;
			Zone zone = new Zone();
			zone.setCode(code);
			zone.setDescription(code);
			zone.setEnable(true);
			zone.setCreatedBy(user);
			saveList.add(zone);
			
			System.out.println("Zone " + i + ": " + code);
		}
		
		masterService.saveList(Zone.class, saveList);
		
		System.out.println("Num of Saved: " + saveList.size() );
		System.out.println("Finish! \n\n");
	}
}
