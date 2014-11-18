package module.dao.system;

import org.springframework.stereotype.Repository;

import module.dao.DaoFactory;

import module.dao.impl.BaseCRUDDaoImpl;

import module.dao.system.Groups;

@Repository(DaoFactory.GROUPS_DAO)
public class GroupsDaoImpl extends BaseCRUDDaoImpl<Groups, Long> implements GroupsDao
{

}
