package z_test_junit;

import javax.annotation.Resource;

import module.dao.general.Alert;
import module.dao.general.Alert.AlertType;
import module.service.ServiceFactory;
import module.service.all.GeneralService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:databaseContext.xml", "classpath:applicationContext.xml"})

public class ImportAlertSetting
{
	@Resource(name=ServiceFactory.GeneralService)
	public GeneralService generalService;
	
	@Test
	public void test()
	{
		Alert alert = new Alert();
		alert.setId(AlertType.IroningExpiryPeriod);
		alert.setHours(24);
		generalService.save(Alert.class, alert);
		
		System.out.println("Default Ironing-Period: " + alert.getHours() );
		System.out.println("Finish! \n\n");
	}
}
