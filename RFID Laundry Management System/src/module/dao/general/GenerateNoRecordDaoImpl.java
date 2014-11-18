package module.dao.general;

import module.dao.DaoFactory;
import module.dao.general.GenerateNoRecord.GenerateNoRecordType;
import module.dao.impl.BaseCRUDDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(DaoFactory.GenerateNoRecordDao)
public class GenerateNoRecordDaoImpl extends BaseCRUDDaoImpl<GenerateNoRecord, GenerateNoRecordType> implements GenerateNoRecordDao
{

}
