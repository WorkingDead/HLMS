package module.dao.general;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(DaoFactory.RECEIPT_DAO)
public class ReceiptDaoImpl extends BaseCRUDDaoImpl<Receipt, Long> implements ReceiptDao
{

}
