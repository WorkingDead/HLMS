package module.dao.master;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(DaoFactory.ZONE_DAO)
public class ZoneDaoImpl extends BaseCRUDDaoImpl<Zone, Long> implements ZoneDao
{

}
