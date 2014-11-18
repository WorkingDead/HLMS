package module.dao.system;

import org.springframework.stereotype.Repository;

import module.dao.DaoFactory;

import module.dao.impl.BaseCRUDDaoImpl;

import module.dao.system.GroupAuthorities;

@Repository(DaoFactory.GROUP_AUTHORITIES_DAO)
public class GroupAuthoritiesDaoImpl extends BaseCRUDDaoImpl<GroupAuthorities, Long> implements GroupAuthoritiesDao
{

}
