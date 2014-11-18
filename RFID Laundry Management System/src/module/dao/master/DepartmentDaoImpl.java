package module.dao.master;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;
import org.springframework.stereotype.Repository;

@Repository(DaoFactory.DEPARTMENT_DAO)
public class DepartmentDaoImpl extends BaseCRUDDaoImpl<Department, Long> implements DepartmentDao
{

}
