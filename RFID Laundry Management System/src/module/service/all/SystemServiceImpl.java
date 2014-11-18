package module.service.all;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;
import module.dao.DaoFactory;
import module.dao.system.GroupAuthorities;
import module.dao.system.GroupAuthoritiesDao;
import module.dao.system.Groups;
import module.dao.system.GroupsDao;
import module.dao.system.SecurityResource;
import module.dao.system.SecurityResourceDao;
import module.dao.system.Users;
import module.dao.system.UsersDao;
import module.dao.system.GroupAuthorities.GroupAuthoritiesType;
import module.service.ServiceFactory;
import module.service.impl.BaseCRUDDaosServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import web.actions.system.UserForm;

@Service(ServiceFactory.SystemService)
public class SystemServiceImpl extends BaseCRUDDaosServiceImpl implements SystemService {

	@Resource(name=DaoFactory.USERS_DAO)
	public UsersDao usersDao;

	@Resource(name=DaoFactory.GROUPS_DAO)
	public GroupsDao groupsDao;

	@Resource(name=DaoFactory.GROUP_AUTHORITIES_DAO)
	public GroupAuthoritiesDao groupAuthoritiesDao;

	@Resource(name=DaoFactory.SECURITY_RESOURCE_DAO)
	public SecurityResourceDao securityResourceDao;

	public void updateUsers(Users users, UserForm userForm) {
		
		if ( userForm != null && userForm.getGroups() != null && userForm.getGroups().getId() != null ) {

			Groups groups = this.get(Groups.class, userForm.getGroups().getId());
			users.setGroups( new HashSet<Groups>() );
			users.getGroups().add(groups);
		}
		
		usersDao.save(users);
	}

	public void createGroups(Groups groups, GroupAuthoritiesType groupAuthoritiesType, List<Long> addResourceList) {

		//GroupAuthorities
		GroupAuthorities groupAuthorities = new GroupAuthorities();
		groupAuthorities.setAuthority( groupAuthoritiesType );
		//GroupAuthorities

		//Resource
		for(Iterator<Long> iterator = addResourceList.iterator();iterator.hasNext();) {
			
			SecurityResource securityResource = securityResourceDao.get( iterator.next() );
			if ( securityResource != null )
				groupAuthorities.getSecurityResource().add( securityResource );
		}
		//Resource

		groups.getGroupAuthorities().add( groupAuthorities );
		
		groupsDao.save(groups);
	}
	
	public void updateGroups(Groups groupsToBeUpdate, List<Long> addResourceList) {
		
		Iterator<GroupAuthorities> groupAuthoritiesIterator = groupsToBeUpdate.getGroupAuthorities().iterator();
		while ( groupAuthoritiesIterator.hasNext() ) {
			GroupAuthorities groupAuthorities = groupAuthoritiesIterator.next();
			groupAuthorities.setSecurityResource( new HashSet<SecurityResource>() );
	
			//Resource
			for(Iterator<Long> iterator = addResourceList.iterator();iterator.hasNext();) {
				
				SecurityResource securityResource = securityResourceDao.get( iterator.next() );
				if ( securityResource != null )
					groupAuthorities.getSecurityResource().add( securityResource );
			}
			//Resource
		}

		groupsDao.save(groupsToBeUpdate);
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		return usersDao.loadUserByUsername(username);
	}
}
