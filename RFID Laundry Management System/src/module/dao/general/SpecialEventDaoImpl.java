package module.dao.general;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(DaoFactory.SPECIAL_EVENT_DAO)
public class SpecialEventDaoImpl extends BaseCRUDDaoImpl<SpecialEvent, Long> implements SpecialEventDao
{
	
}
