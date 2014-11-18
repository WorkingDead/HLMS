package web.actions;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

import module.dao.iface.CustomCriteriaHandler;
import module.dao.system.GroupAuthorities;
import module.dao.system.Groups;
import module.dao.system.SecurityResource;
import module.dao.system.Users;

// BaseActionSecurity: 
// if a ActionClass extend BaseActionSecurity, 
// user need to login before accessing the page
public class BaseActionSecurity extends BaseAction
{
	private static final Logger log4j = Logger.getLogger(BaseActionSecurity.class);
	private static final long serialVersionUID = 7968269202503631548L;
	
	//////////////////////////////////////////
	// Security
	//////////////////////////////////////////
	public static final String SuperAdminUserName = "sadmin";
	public static final String SuperAdminUserGroupName = "Super Admin";
	public static final String AdminUserName = "admin";
	public static final String AdminUserGroupName = "Admin";
	//////////////////////////////////////////
	// Security
	//////////////////////////////////////////
	
	public BaseActionSecurity()
	{
		super();
	}
	
	public Users getUser()
	{
		Users users = null;
		UserDetails temp = getUserDetails();
		if ( temp != null ) {
			String user = temp.getUsername();
			UserDetails userDetails = getSystemService().loadUserByUsername(user);
			users = (Users) userDetails;
		}

		return users;

//		Users user = new Users();//TODO login
//		user.setUsername("S001");
//		return user;
	}

	///////////////////////////////////////////////////////////////
	// Authority
	///////////////////////////////////////////////////////////////
	public boolean hasUserGroupResourceAuthority()
	{
		ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
		
//		System.out.println("Method Name = " + invocation.getProxy().getMethod() );
//		System.out.println("Action Name = " + invocation.getProxy().getActionName() );
//		System.out.println("Namespace = " + invocation.getProxy().getNamespace() );
		
		String requestResource = invocation.getProxy().getNamespace() + "/" + 
				invocation.getProxy().getActionName() + "!" +
				invocation.getProxy().getMethod() + ".action";
		
		//Static Exception
		//This Exception should be the same in securityContext.xml
		//System.out.println("requestResource = " + requestResource);
		
		String listStr = getBeansFactoryApplication().getKioskSecurityExceptionPattern();
		String listArray[] = listStr.split(",");
		for (int i = 0; i < listArray.length; i++) {
			
			String string = listArray[i];		
			string = StringUtils.replace(string, "*", "").trim();	//Only handling one * only
			//System.out.println("string = " + string);
			if ( string == null || string.isEmpty() )
				continue;
			
			if ( requestResource.indexOf( string ) == 0 ) {
				return true;
			}
		}
		//Static Exception
		
		return hasUserGroupResourceAuthority(requestResource);
	}
	
	public boolean hasUserGroupResourceAuthority(String requestResource)	//true = has authority
	{
		String user = null;
		UserDetails temp = getUserDetails();
		if ( temp != null )
			user = temp.getUsername();

		if ( user != null && user.trim().length() > 0 )
		{
			boolean sysFlag = true;
			
			try
			{
				UserDetails userDetails = getSystemService().loadUserByUsername(user);
//				System.out.println("userDetails getUsername = " + userDetails.getUsername());

				Users users = (Users)userDetails;
				Iterator<Groups> groupsIterator = users.getGroups().iterator();
				while ( groupsIterator.hasNext() )
				{
					Groups groups = groupsIterator.next();
					
					Iterator<GroupAuthorities> groupAuthoritiesIterator = groups.getGroupAuthorities().iterator();
					while ( groupAuthoritiesIterator.hasNext() )
					{
						GroupAuthorities groupAuthorities = groupAuthoritiesIterator.next();
						 
						Iterator<SecurityResource> resourcesIterator = groupAuthorities.getSecurityResource().iterator();
						while ( resourcesIterator.hasNext() )
						{
							SecurityResource securityResource = resourcesIterator.next();
	
							String[] strings = securityResource.getResource().split(",");
							for(String string:strings)
							{ 
								if (  requestResource.indexOf( string.trim() ) >= 0 )
								{
									sysFlag = false;
								}
							}

//							if ( resource.getResource().equals( requestResource ) ) {
//								sysFlag = false;
//							}
						}
					}
				}

				if ( sysFlag == true )
				{
					//log4j.info("hasUserGroupResourceAuthority: requestResource = " + requestResource);
					//log4j.info("hasUserGroupResourceAuthority: return true");
					return true;
				}
			}
			catch (Exception e)
			{

				log4j.error(e);
			}
		}
		else
		{
			log4j.error("hasUserGroupResourceAuthority: user is null or empty.");
		}
		
		log4j.info("hasUserGroupResourceAuthority: requestResource = " + requestResource);
		log4j.info("hasUserGroupResourceAuthority: return false");
		
		return false;
	}
	
	public UserDetails getUserDetails()
	{
		UserDetails userDetails = null;

		try
		{
			userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		catch (Exception exception)
		{
			log4j.error("No Logged User Found!!", exception);
		}

		return userDetails;
	} 

	public boolean hasGroup(Users users, String targetGroupName)
	{
		boolean sysFlag = false;
		Iterator<Groups> groupsIterator = users.getGroups().iterator();
		while (groupsIterator.hasNext())
		{
			Groups group = groupsIterator.next();
			if (group.getGroupName().equals(targetGroupName))
				sysFlag = true;
		}

		return sysFlag;
	}
	
	//The user in "Super Admin" group can see all groups and users
	//The user in "Admin" group can seel all groups and users except "Super Admin" group and its user
	public void filterAdminAndSuperAdminAndKioskGroupAndItsUsers(Criteria criteria)
	{
		Users users = null;
		UserDetails temp = getUserDetails();
		if ( temp != null ) {
			String user = temp.getUsername();
			UserDetails userDetails = getSystemService().loadUserByUsername(user);
			users = (Users) userDetails;
		}

		if ( users != null )
		{
			if ( hasGroup(users, BaseActionSecurity.SuperAdminUserGroupName) == false )
			{
				criteria.add(Restrictions.ne("groupName", BaseActionSecurity.SuperAdminUserGroupName));
				criteria.add(Restrictions.ne("groupName", BaseActionKiosk.KioskUserGroupName));
				
				if ( hasGroup(users, BaseActionSecurity.AdminUserGroupName) == false )
				{
					criteria.add(Restrictions.ne("groupName", BaseActionSecurity.AdminUserGroupName));
				}
			}
		}
		else
		{
			criteria.add(Restrictions.ne("groupName", BaseActionSecurity.AdminUserGroupName));
			criteria.add(Restrictions.ne("groupName", BaseActionSecurity.SuperAdminUserGroupName));
			criteria.add(Restrictions.ne("groupName", BaseActionKiosk.KioskUserGroupName));
		}
	}
	///////////////////////////////////////////////////////////////
	// End Authority
	///////////////////////////////////////////////////////////////
	
	public boolean isUsersFieldValueAlreadyInDb(final String FIELD_NAME, final String FIELD_VALUE) {
		
		Integer n = getSystemService().totalByExample(Users.class, null, 
				new CustomCriteriaHandler<Users>()
				{
					@Override
					public void makeCustomCriteria(Criteria criteria)
					{
						criteria.add(Restrictions.eq(FIELD_NAME, FIELD_VALUE) );
					}
				}
		);

		if (n > 0)
			return true;
		else
			return false;
	}
	
	public boolean isGroupsFieldValueAlreadyInDb(final String FIELD_NAME, final String FIELD_VALUE) {
		
		Integer n = getSystemService().totalByExample(Groups.class, null, 
				new CustomCriteriaHandler<Groups>()
				{
					@Override
					public void makeCustomCriteria(Criteria criteria)
					{
						criteria.add(Restrictions.eq(FIELD_NAME, FIELD_VALUE) );
					}
				}
		);

		if (n > 0)
			return true;
		else
			return false;
	}
}
