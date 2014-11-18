package module.dao.general;

import org.springframework.stereotype.Repository;
import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;

@Repository(DaoFactory.RECEIPT_PATTERN_IRON_DAO)
public class ReceiptPatternIronDaoImpl extends BaseCRUDDaoImpl<ReceiptPatternIron, Long> implements ReceiptPatternIronDao
{

}
