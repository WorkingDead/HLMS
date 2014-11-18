package module.dao.master;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(DaoFactory.ROLL_CONTAINER_DAO)
public class RollContainerDaoImpl extends BaseCRUDDaoImpl<RollContainer, Long> implements RollContainerDao
{

}
