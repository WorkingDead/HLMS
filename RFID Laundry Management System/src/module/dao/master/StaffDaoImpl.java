package module.dao.master;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;

import org.springframework.stereotype.Repository;

@Repository(DaoFactory.STAFF_DAO)
public class StaffDaoImpl extends BaseCRUDDaoImpl<Staff, Long> implements StaffDao
{

}
