package module.hibernate.listener;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import module.dao.BaseBo;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.hibernate.EmptyInterceptor;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

public class BaseBoHibernateInterceptor extends EmptyInterceptor {
	
	private static final Logger log = Logger.getLogger(BaseBoHibernateInterceptor.class);
	
	private Set<Object> inserts = new HashSet<Object>();
	private Set<Object> updates = new HashSet<Object>();
	private Set<Object> deletes = new HashSet<Object>();
	
	private static Object InsertListLock = new Object();
	private static Object UpdateListLock = new Object();
	private static Object DeleteListLock = new Object();

	@Override
	public boolean onLoad(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		if(entity instanceof BaseBo)
		{
			BaseBo bo = (BaseBo) entity;
			bo.OnLoad();
		}
		return super.onLoad(entity, id, state, propertyNames, types);
	}

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
//		System.out.println("onSave ... " + entity.getClass().getSimpleName());
		
		if(entity instanceof BaseBo)
		{
			BaseBo bo = (BaseBo) entity;
			if(!bo.OnBeforeSave())
			{
				//throw hibernate exception
				throw new HibernateException("OnBeforeSave return false");
			}
			mergeEntityState(entity, state, propertyNames);
			
			synchronized (InsertListLock) {
				inserts.add(entity);
			}
		}
		return true;
		/*
		 * If you invoke setter method for the property to be set, it will be not established.

			Also is important to return a true value in that interceptor's method to indicate hibernate that the state has been changed by your code.
			
			ref: https://forum.hibernate.org/viewtopic.php?f=1&t=999928
			
			//also need to call mergeEntityState
		 */
	}
	
	@Override
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		
//		System.out.println("onFlushDirty ... " + entity.getClass().getSimpleName());
		
		if(entity instanceof BaseBo)
		{
			BaseBo bo = (BaseBo) entity;
			if(!bo.OnBeforeUpdate())
			{
				//throw hibernate exception
				throw new HibernateException("OnBeforeUpdate return false");
			}
			mergeEntityState(bo, currentState, propertyNames);
			
			synchronized (UpdateListLock) {
				updates.add(entity);
			}
			
		}
		return true;
	}
	
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
//		System.out.println("onDelete ... " + entity.getClass().getSimpleName());
		
		if(entity instanceof BaseBo)
		{
			BaseBo bo = (BaseBo) entity;
			if(!bo.OnBeforeDelete())
			{
				//throw hibernate exception
				throw new HibernateException("OnBeforeDelete return false");
			}
			
			synchronized (DeleteListLock) {
				deletes.add(entity);
			}
			
		}
	}
	
	private void mergeEntityState(Object entity, Object[] state,
			String[] propertyNames){
		for(int i=0; i<state.length; i++){
			String pName = propertyNames[i];
			Object pValue = state[i];
			try {
				Object value = PropertyUtils.getProperty(entity, pName);
				if(value==null)
				{
					state[i] = null;
				}
				else
				{
					if(!value.equals(pValue))
					{
						state[i] = value; //set back the value
					}
				}
			} catch (IllegalAccessException e) {
				throw new HibernateException(e);
			} catch (InvocationTargetException e) {
				throw new HibernateException(e);
			} catch (NoSuchMethodException e) {
				throw new HibernateException(e);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void postFlush(Iterator entities) {
		
//		while(entities.hasNext()){
//			Object entity = entities.next();
//			if(entity instanceof BaseBo)
//			{
//				((BaseBo)entity).postFlush();
//			}
//		}
		
	}

	@Override
	public void afterTransactionCompletion(Transaction tx) {
		
		if(inserts.isEmpty() && updates.isEmpty() && deletes.isEmpty())
		{
			return;
		}
		
		Set<Object> insertsClone = new HashSet<Object>();
		Set<Object> updatesClone = new HashSet<Object>();
		Set<Object> deletesClone = new HashSet<Object>();
		
		synchronized (InsertListLock) {
			insertsClone.addAll(inserts);
		}
		
		synchronized (UpdateListLock) {
			updatesClone.addAll(updates);
		}
		
		synchronized (DeleteListLock) {
			deletesClone.addAll(deletes);
		}
		
		if(!tx.wasCommitted()) //ignore when rolled back
		{
			
			synchronized (InsertListLock) {
				inserts.removeAll(insertsClone);
			}
			
			synchronized (UpdateListLock) {
				updates.removeAll(updatesClone);
			}
			
			synchronized (DeleteListLock) {
				deletes.removeAll(deletesClone);
			}
			
			return;
		}
		
		try{
			
//			System.out.println("inserts... " + inserts.size());
//			System.out.println("updates... " + updates.size());
//			System.out.println("deletes... " + deletes.size());
			
			for (Iterator<Object> it = insertsClone.iterator(); it.hasNext();) {
				BaseBo entity = (BaseBo) it.next();
				entity.OnAfterSave();
			}	
			
			for (Iterator<Object> it = updatesClone.iterator(); it.hasNext();) {
				BaseBo entity = (BaseBo) it.next();
				entity.OnAfterUpdate();
			}	
	 
			for (Iterator<Object> it = deletesClone.iterator(); it.hasNext();) {
				BaseBo entity = (BaseBo) it.next();
				entity.OnAfterDelete();
			}
			
		}
		catch(Exception e)
		{
			//postFlush is call after transaction end, so cannot rollback!
			log.error("afterTransactionCompletion has exception", e);
		}
		finally {
			
			synchronized (InsertListLock) {
				inserts.removeAll(insertsClone);
			}
			
			synchronized (UpdateListLock) {
				updates.removeAll(updatesClone);
			}
			
			synchronized (DeleteListLock) {
				deletes.removeAll(deletesClone);
			}
			
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 572320439710598155L;

}
