package module.dao.master;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(DaoFactory.CLOTH_TYPE_DAO)
public class ClothTypeImpl extends BaseCRUDDaoImpl<ClothType, Long> implements ClothTypeDao
{

}
