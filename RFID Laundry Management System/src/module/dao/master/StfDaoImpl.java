package module.dao.master;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import module.dao.DaoFactory;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

@Repository(DaoFactory.STF_DAO)
public class StfDaoImpl implements StfDao {

	Logger log = Logger.getLogger(getClass());
	
	public StfDaoImpl()
	{}
	


	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}



	public void save(Stf obj) {
		
		try {
			
			//for trigger on before save and on before update
			getSessionFactory().getCurrentSession().evict(obj);
			
			getSessionFactory().getCurrentSession().saveOrUpdate(obj);
			getSessionFactory().getCurrentSession().flush();
//			obj.OnAfterSave(true);
		}
		catch(Exception e)
		{
			log.error( getClass() + " save has Exception", e);
//			obj.OnAfterSave(false);
			throw new HibernateException(e);
		}
	}
	

	@SuppressWarnings("unchecked")
	public List<Stf> findAll(Order order) {
		
		Session session = getSessionFactory().getCurrentSession();
		
		Criteria criteria = session.createCriteria( Stf.class );
		criteria.addOrder( order );
		
		List<Stf> result = criteria.list();
		
		return result;
	}
	
	public void deleteList(List<Stf> list) {
		log.debug( getClass() +  " deleteList size : " + list.size());
		Iterator<Stf> it = list.iterator(); 
		while(it.hasNext())
		{
			this.delete(it.next());
		}
	}
	
	public void delete(Stf obj) {
//		if(obj.OnBeforeDelete()) //using lifecycle
//		{
			try{
				getSessionFactory().getCurrentSession().delete(obj);
				getSessionFactory().getCurrentSession().flush();
//				obj.OnAfterDelete(true);
			}
			catch (Exception e)
			{
				log.error( getClass() +  " delete has Exception", e);
//				obj.OnAfterDelete(false);
				throw new HibernateException(e);
			}
//		}
//		else
//		{
//			obj.OnAfterDelete(false);
//			throw new HibernateException("Roll back from java bean");
//		}
	}
}
