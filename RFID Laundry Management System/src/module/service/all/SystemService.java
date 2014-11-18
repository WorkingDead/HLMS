package module.service.all;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import web.actions.system.UserForm;

import module.dao.system.Groups;
import module.dao.system.Users;
import module.dao.system.GroupAuthorities.GroupAuthoritiesType;
import module.service.iface.BaseCRUDDaosService;

public interface SystemService extends BaseCRUDDaosService, UserDetailsService {

	public abstract void updateUsers(Users users, UserForm userForm);
	
	public abstract void createGroups(Groups groups, GroupAuthoritiesType groupAuthoritiesType, List<Long> addResourceList);

	public abstract void updateGroups(Groups groupsToBeUpdate, List<Long> addResourceList);
}
