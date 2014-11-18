package module.hibernate.interceptor;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

import module.dao.iface.OptimisticVersionControl;
import module.hibernate.listener.BaseBoHibernateInterceptor;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.type.Type;

import utils.spring.SpringUtils;

public class OptimisticVersionControlInterceptor extends BaseBoHibernateInterceptor {

	@Override
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		
		if(entity instanceof OptimisticVersionControl)
		{
			Calendar lastModifyDate = null;
			
			try {
				lastModifyDate = (Calendar) PropertyUtils.getProperty(entity, "lastModifyDate");
			} catch (IllegalAccessException e) {
				throw new RuntimeException("cannot get lastModifyDate", e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException("cannot get lastModifyDate", e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("cannot get lastModifyDate", e);
			}
			
			SessionFactory sessionFactory = SpringUtils.getBean("sessionFactory");
			Session preSession = sessionFactory.openSession();
			
			Query preQuery = preSession.createQuery("select count(*) from " + entity.getClass().getSimpleName() + " where id = :id");
			preQuery.setString("id", id.toString());
			Number preResult = (Number) preQuery.iterate().next();
			
			preSession.close();
			
			if ( preResult.intValue() == 1 ) {
				Session session = sessionFactory.openSession();
				
				Query q = session
						.createQuery("select count(*) from " + entity.getClass().getSimpleName() + " where id = :id and (lastModifyDate = null or lastModifyDate = :lastModifyDate)");
				
				q.setString("id", id.toString());
				q.setCalendar("lastModifyDate", lastModifyDate);
				
				Number result = (Number) q.iterate().next();
				
				session.close();
				
				if(result.intValue()==0)
				{
					throw new StaleObjectStateException(entity.getClass().getSimpleName(), id);
				}
			}
			else if ( preResult.intValue() > 1 ) {
				throw new StaleObjectStateException(entity.getClass().getSimpleName(), id);
			}
		}
		
		return super.onFlushDirty(entity, id, currentState, previousState,
				propertyNames, types);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -881917886866616668L;

}
