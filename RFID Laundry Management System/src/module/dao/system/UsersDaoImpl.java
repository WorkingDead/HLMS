package module.dao.system;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;

import org.hibernate.criterion.Restrictions;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import module.dao.DaoFactory;

import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;

import module.dao.impl.BaseCRUDDaoImpl;

import module.dao.system.GroupAuthorities;
import module.dao.system.Groups;
import module.dao.system.Users;

@Repository(DaoFactory.USERS_DAO)
public class UsersDaoImpl extends BaseCRUDDaoImpl<Users, String> implements UsersDao
{
	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException
	{
		List<Users> users = findByExample(new Users(), null, null,

		new CustomCriteriaHandler<Users>()
		{
			@Override
			public void makeCustomCriteria(Criteria baseCriteria)
			{
				baseCriteria.add(Restrictions.eq("username", username));
			}
		},

		new CustomLazyHandler<Users>()
		{
			@Override
			public void LazyList(List<Users> list)
			{
				Iterator<Users> it = list.iterator();
				while (it.hasNext())
				{
					Users users = it.next();
					users.getGroups().size();

					Iterator<Groups> groupsIterator = users.getGroups().iterator();
					while (groupsIterator.hasNext())
					{
						Groups groups = groupsIterator.next();
						groups.getGroupAuthorities().size();

						Iterator<GroupAuthorities> groupAuthoritiesIterator = groups.getGroupAuthorities().iterator();
						while (groupAuthoritiesIterator.hasNext())
						{
							GroupAuthorities groupAuthorities = groupAuthoritiesIterator.next();
							groupAuthorities.getSecurityResource().size();
						}
					}
				}
			}
		},
		null);

		if (users.size() != 1)
		{
			throw new UsernameNotFoundException("User not found");
		}

		return users.get(0);
	}
}
