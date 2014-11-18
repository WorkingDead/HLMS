package module.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import module.dao.impl.BaseDaoImpl;

@Repository(DaoFactory.HQL_DAO)
public class HQLDaoImpl extends BaseDaoImpl implements HQLDao
{
	@Override
	public int removeAllProcessedXmlCache() {
		Query q = super.getSessionFactory().getCurrentSession().createQuery("delete from XmlCache where processed = :processed"); //hql
		q.setBoolean("processed", true);
		return q.executeUpdate();
	}
}
