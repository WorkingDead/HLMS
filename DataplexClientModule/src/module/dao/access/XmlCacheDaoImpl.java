package module.dao.access;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import module.dao.DaoFactory;
import module.dao.XmlCache;
import module.dao.XmlCache.ProcessStatus;
import module.dao.impl.BaseCRUDDaoImpl;

@Repository(DaoFactory.XmlCacheDao)
public class XmlCacheDaoImpl extends BaseCRUDDaoImpl<XmlCache, Long> implements
		XmlCacheDao {

	//this function will remove all processed and status is Success XmlCache
	@Override
	public void removeAllProcessed() {
		Query q = super.getSessionFactory().getCurrentSession().createQuery("delete from XmlCache where processed = :processed and status = :status"); //hql
		q.setBoolean("processed", true);
		q.setParameter(":status", ProcessStatus.Success);
		q.executeUpdate();
	}

}
