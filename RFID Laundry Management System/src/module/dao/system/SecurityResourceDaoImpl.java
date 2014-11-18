package module.dao.system;

import org.springframework.stereotype.Repository;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;

import module.dao.system.SecurityResource;

@Repository(DaoFactory.SECURITY_RESOURCE_DAO)
public class SecurityResourceDaoImpl extends BaseCRUDDaoImpl<SecurityResource, Long> implements SecurityResourceDao
{

}
