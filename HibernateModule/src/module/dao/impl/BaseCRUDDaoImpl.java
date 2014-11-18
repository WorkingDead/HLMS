package module.dao.impl;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import module.dao.BaseBo;
import module.dao.iface.BaseCRUDDao;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;

public abstract class BaseCRUDDaoImpl<T extends BaseBo, ID extends Serializable> extends BaseDaoImpl implements BaseCRUDDao<T, ID> {
	
	/*
	 * TODO
	 * It has a another method to do lazy fetch
	 * using setFetchMode
	 * List cats = sess.createCriteria(Cat.class)
	    .add( Restrictions.like("name", "Fritz%") )
	    .setFetchMode("mate", FetchMode.EAGER)
	    .setFetchMode("kittens", FetchMode.EAGER)
	    .list();
	 */

	Logger log = Logger.getLogger(getClass());

	public BaseCRUDDaoImpl()
	{}
	
	@SuppressWarnings("unchecked")
	public Class<T> getEntityClass()
	{
		return (Class<T>) ((java.lang.reflect.ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Override
	public Integer total() {
		return this.totalByExample(null, null);
	}

	@Override
	public Integer totalByExample(T example,
			CustomCriteriaHandler<T> customCriteriaHandler) {
		Criteria criteria = createCriteria(example, getSessionFactory().getCurrentSession());

		//custom criteria
		if(customCriteriaHandler!=null)
			customCriteriaHandler.makeCustomCriteria(criteria);

		criteria.setProjection(Projections.rowCount());

		return ((Number) criteria.uniqueResult()).intValue();
	}

	@Override
	public List<T> findAll(Integer offset, Integer interval,
			CustomLazyHandler<T> customLazyHandler, Order orderBy) {
		return this.findByExample(null, offset, interval, null, customLazyHandler, orderBy);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByExample(T example, Integer offset, Integer interval,
			CustomCriteriaHandler<T> customCriteriaHandler,
			CustomLazyHandler<T> customLazyHandler, Order orderBy) {
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = createCriteria(example, session);

		//OrderBy
		if(orderBy!=null)
			criteria.addOrder(orderBy);

		//custom criteria
		if(customCriteriaHandler!=null)
			customCriteriaHandler.makeCustomCriteria(criteria);
		
		//offset
		if(offset!=null)
		{
			criteria.setFirstResult(offset);
		}
		
		//interval
		if(interval!=null)
		{
			criteria.setMaxResults(interval);
		}

		List<T> result = criteria.list();

		//Lazy
		if (customLazyHandler != null)
			customLazyHandler.LazyList(result);

		return result;
	}

	// Add Multiple Order-By (by Horace)
	@Override
	public List<T> findAllWithOrders(Integer offset, Integer interval,
			CustomLazyHandler<T> customLazyHandler, List<Order> orderByList)
	{
		return this.findByExampleWithOrders(null, offset, interval, null, customLazyHandler, orderByList);
	}
	public List<T> findByExampleWithOrders(T example, Integer offset, Integer interval, CustomCriteriaHandler<T> customCriteriaHandler, CustomLazyHandler<T> customLazyHandler, List<Order> orderByList)
	{
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = createCriteria(example, session);

		// OrderBy
		if (orderByList != null && orderByList.size() > 0)
		{
			for (int i = 0; i < orderByList.size(); i++)
			{
				criteria.addOrder(orderByList.get(i));
			}
		}

		// custom criteria
		if (customCriteriaHandler != null)
		{
			customCriteriaHandler.makeCustomCriteria(criteria);
		}
			
		// offset
		if(offset!=null)
		{
			criteria.setFirstResult(offset);
		}
		
		// interval
		if(interval!=null)
		{
			criteria.setMaxResults(interval);
		}
		
		List<T> result = criteria.list();
		
		// Lazy
		if (customLazyHandler != null)
			customLazyHandler.LazyList(result);

		return result;
	}
	
	
	@Override
	public T get(ID id) {
		return get(id, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T get(ID id, CustomLazyHandler<T> customLazyHandler) {
		T obj = (T) getSessionFactory().getCurrentSession().get(getEntityClass(), id);
		
		//Lazy
		if (customLazyHandler!=null)
			customLazyHandler.LazyObject(obj);
		
		return obj;
	}

	@Override
	public void saveList(List<T> list) {
		log.debug("HibernateModule saveList size : " + list.size());
		Iterator<T> it = list.iterator(); 
		while(it.hasNext())
		{
			this.save(it.next());
		}
	}

	@Override
	public void save(T obj) {
//		if(obj.OnBeforeSave()) //using lifecycle
//		{
			try {
				
				//for trigger on before save and on before update
				getSessionFactory().getCurrentSession().evict(obj);
				
				getSessionFactory().getCurrentSession().saveOrUpdate(obj);
				getSessionFactory().getCurrentSession().flush();
//				obj.OnAfterSave(true);
			}
			catch(Exception e)
			{
				log.error("HibernateModule save has Exception", e);
//				obj.OnAfterSave(false);
				throw new HibernateException(e);
			}
//		}
//		else
//		{
//			obj.OnAfterSave(false);
//			throw new HibernateException("Roll back from java bean");
//		}
	}

	@Override
	public void deleteList(List<T> list) {
		log.debug("HibernateModule deleteList size : " + list.size());
		Iterator<T> it = list.iterator(); 
		while(it.hasNext())
		{
			this.delete(it.next());
		}
	}

	@Override
	public void delete(T obj) {
//		if(obj.OnBeforeDelete()) //using lifecycle
//		{
			try{
				getSessionFactory().getCurrentSession().delete(obj);
				getSessionFactory().getCurrentSession().flush();
//				obj.OnAfterDelete(true);
			}
			catch (Exception e)
			{
				log.error("HibernateModule delete has Exception", e);
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

	//create criteria
	private Criteria createCriteria(T example, Session session)
	{
		Criteria criteria = session.createCriteria(getEntityClass());
		if(example!=null)
			criteria.add(Example.create(example).enableLike(MatchMode.START));
			//It is because creationDate will define when new the BO, if need to add criteria for creationDate use custom criteria handler.
		return criteria;
	}
}
