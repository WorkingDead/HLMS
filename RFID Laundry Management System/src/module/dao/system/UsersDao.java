package module.dao.system;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import module.dao.iface.BaseCRUDDao;

import module.dao.system.Users;

public interface UsersDao extends BaseCRUDDao<Users, String> 
{
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
