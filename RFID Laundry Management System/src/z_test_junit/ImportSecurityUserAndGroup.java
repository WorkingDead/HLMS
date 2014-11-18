package z_test_junit;

import javax.annotation.Resource;

import module.dao.system.GroupAuthorities.GroupAuthoritiesType;
import module.dao.system.GroupAuthorities;
import module.dao.system.Groups;
import module.dao.system.Users;
import module.service.ServiceFactory;
import module.service.all.SystemService;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import utils.security.MD5;

import web.actions.BaseActionKiosk;
import web.actions.BaseActionSecurity;
import web.actions.BaseAction.SystemLanguage;

import com.ibm.icu.impl.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:databaseContext.xml", "classpath:applicationContext.xml"})
public class ImportSecurityUserAndGroup {

	@Resource(name=ServiceFactory.SystemService)
	public SystemService systemService;

	/////////////////////////////////////
	//	This import shoule be synchronized with the file, Doc MS SQL/InitialSql/import superadmin and admin.txt
	/////////////////////////////////////
	
//	@Ignore("Not Complete And Not Run")
	@Test(timeout = 5000)
	public void createUserAndGroup01()
	{
		try
		{
			GroupAuthoritiesType groupAuthoritiesType = GroupAuthoritiesType.ROLE_SUPER_ADMIN;

			GroupAuthorities groupAuthorities = new GroupAuthorities();
			groupAuthorities.setAuthority(groupAuthoritiesType);

			Groups groups = new Groups();
			groups.setGroupName(BaseActionSecurity.SuperAdminUserGroupName);
			groups.getGroupAuthorities().add(groupAuthorities);
			groups.setEnabled(true);

			systemService.save(Groups.class, groups);

			Users users = new Users();
			users.setUsername(BaseActionSecurity.SuperAdminUserName);
			users.setUserDisplayName(BaseActionSecurity.SuperAdminUserName);
			users.getGroups().add(groups);
			users.setPassword(MD5.getMD5("PassWord"));	// update `oms`.`users` set
												// password=MD5("SednaPass")
												// where username= "superadmin"
			users.setLang( SystemLanguage.en_US );
			users.setEmail("");
			users.setEnabled(true);

			systemService.save(Users.class, users);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Assert.fail(e);
		}
	}

//	@Ignore("Not Complete And Not Run")
	@Test(timeout = 1000)
	public void createUserAndGroup02()
	{
		try
		{
			GroupAuthoritiesType groupAuthoritiesType = GroupAuthoritiesType.ROLE_ADMIN;

			GroupAuthorities groupAuthorities = new GroupAuthorities();
			groupAuthorities.setAuthority(groupAuthoritiesType);

			Groups groups = new Groups();
			groups.setGroupName(BaseActionSecurity.AdminUserGroupName);
			groups.getGroupAuthorities().add(groupAuthorities);
			groups.setEnabled(true);

			systemService.save(Groups.class, groups);

			Users users = new Users();
			users.setUsername(BaseActionSecurity.AdminUserName);
			users.setUserDisplayName(BaseActionSecurity.AdminUserName);
			users.getGroups().add(groups);
			users.setPassword(MD5.getMD5("admin"));	// update `oms`.`users` set
												// password=MD5("PassWord")
												// where username= "admin"
			users.setLang( SystemLanguage.en_US );
			users.setEmail("");
			users.setEnabled(true);

			systemService.save(Users.class, users);
			
			
			
			//////////////////////////////////////
			// 2nd Admin (by Horace)
			//////////////////////////////////////
//			users = new Users();
//			users.setUsername("b");
//			users.setUserDisplayName("b");
//			users.getGroups().add(groups);
//			users.setPassword(MD5.getMD5(""));
//			users.setLang( SystemLanguage.en_US );
//			users.setEmail("");
//			users.setEnabled(true);
//			systemService.save(Users.class, users);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Assert.fail(e);
		}
	}
	
//	@Ignore("Not Complete And Not Run")
	@Test(timeout = 1000)
	public void createUserAndGroup03() {
		
		try {
			GroupAuthoritiesType groupAuthoritiesType = GroupAuthoritiesType.ROLE_USER;

			GroupAuthorities groupAuthorities = new GroupAuthorities();
			groupAuthorities.setAuthority( groupAuthoritiesType );
			
			Groups groups = new Groups();
			groups.setGroupName( BaseActionKiosk.KioskUserGroupName );
			groups.getGroupAuthorities().add( groupAuthorities );
			groups.setEnabled(true);
			
			systemService.save(Groups.class, groups);



			Users users = new Users();
			users.setUsername( BaseActionKiosk.KioskUserName );
			users.setUserDisplayName( BaseActionKiosk.KioskUserName );
			users.getGroups().add(groups);
			users.setPassword( MD5.getMD5("PassWord") );		//update `oms`.`users` set password=MD5("PassWord") where username= "admin"
			users.setLang( SystemLanguage.en_US );
			users.setEmail("");
			users.setEnabled(true);

			systemService.save(Users.class, users);
		}
		catch (Exception e) {
			e.printStackTrace();
			
			Assert.fail(e);
		}
	}
}
