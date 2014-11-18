package module.dao.master;

import org.springframework.stereotype.Repository;
import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;

@Repository(DaoFactory.CLOTH_DAO)
public class ClothDaoImpl extends BaseCRUDDaoImpl<Cloth, Long> implements ClothDao
{
	
}
