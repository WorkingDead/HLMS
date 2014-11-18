package z_test_junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value={
		//Master Data
		ImportSecurityResource.class,		//Clean DB Member
		ImportSecurityUserAndGroup.class,	//Clean DB Member
		ImportDept.class,					//Clean DB Member
		//ImportDeptAppendToTheEndWhenNotExist.class,
		ImportRollContainer.class,			//Clean DB Member
		ImportClothType.class,				//Clean DB Member
		//Master Data

		//Should be included when importing master data
		ImportAlertSetting.class,			//Clean DB Member
		//Should be included when importing master data



		//Import Staff And Cloth Data
		ImportStaffAndCloth.class,
		ImportStaffWithoutUniform.class,
		ImportOtherStaffWithoutUniform.class,
		//Import Staff And Cloth Data



//Development Only
//		ImportZone.class,
//		ImportStaff.class,
//Development Only

//NOT IN USE
//		ImportChangeClothStatus.class,
//		ImportReceipt.class,
//		ImportTransaction.class		// Import Receipt will create Transactions, so this is not used
//NOT IN USE
		})
public class ImportAll
{
}