package module.dao.general;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(DaoFactory.ALERT_DAO)
public class AlertDaoImpl extends BaseCRUDDaoImpl<Alert, Long> implements AlertDao
{

}
