package module.dao.general;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(DaoFactory.TRANSACTION_DAO)
public class TransactionDaoImpl extends BaseCRUDDaoImpl<Transaction, Long> implements TransactionDao
{

}
