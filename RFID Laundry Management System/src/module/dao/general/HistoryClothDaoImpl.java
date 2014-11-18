package module.dao.general;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(DaoFactory.HISTORY_CLOTH_DAO)
public class HistoryClothDaoImpl extends BaseCRUDDaoImpl<HistoryCloth, Long> implements HistoryClothDao
{

}
