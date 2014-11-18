package module.service.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.criterion.Order;

import utils.reflect.ReflectionUtils;

import module.dao.BaseBo;
import module.dao.iface.BaseCRUDDao;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.exception.DaoNotFoundException;
import module.service.iface.BaseCRUDDaosService;

public class BaseCRUDDaosServiceImpl implements BaseCRUDDaosService {
	
	@SuppressWarnings("unchecked")
	public <T extends BaseBo> BaseCRUDDao<T, Serializable> getDao(Class<T> boType)
	{
		Field[] fs = this.getClass().getDeclaredFields();
		for(Field f : fs)
		{
			try{
				@SuppressWarnings("rawtypes")
				Class<? extends BaseCRUDDao> clz = (Class<? extends BaseCRUDDao>) f.getGenericType();
				List<Class<?>> list = ReflectionUtils.getTypeArguments(BaseCRUDDao.class, clz);
				if(list.get(0).equals(boType))
				{
					return (BaseCRUDDao<T, Serializable>) f.get(this);
				}
			}
			catch(Exception ex)
			{}
		}
		DaoNotFoundException e = new DaoNotFoundException("The dao class has not define in your service.");
		throw new RuntimeException(e);
	}

	@Override
	public Integer total(Class<? extends BaseBo> boType) {
		return getDao(boType).total();
	}

	@Override
	public <T extends BaseBo> Integer totalByExample(Class<T> boType,
			T example, CustomCriteriaHandler<T> customCriteriaHandler) {
		return getDao(boType).totalByExample(example, customCriteriaHandler);
	}

	@Override
	public <T extends BaseBo> List<T> findAll(Class<T> boType, Integer offset,
			Integer interval, CustomLazyHandler<T> customLazyHandler,
			Order orderBy) {
		return getDao(boType).findAll(offset, interval, customLazyHandler, orderBy);
	}
	@Override
	public <T extends BaseBo> List<T> findByExample(Class<T> boType, T example,
			Integer offset, Integer interval,
			CustomCriteriaHandler<T> customCriteriaHandler,
			CustomLazyHandler<T> customLazyHandler, Order orderBy) {
		return getDao(boType).findByExample(example, offset, interval, customCriteriaHandler, customLazyHandler, orderBy);
	}
	
	
	
	// Add Multiple Order-By (by Horace)
	@Override
	public <T extends BaseBo> List<T> findAllWithOrders(Class<T> boType, Integer offset,
			Integer interval, CustomLazyHandler<T> customLazyHandler,
			List<Order> orderByList)
	{
		return getDao(boType).findAllWithOrders(offset, interval, customLazyHandler, orderByList);
	}
	@Override
	public <T extends BaseBo> List<T> findByExampleWithOrders(Class<T> boType, 
			T example, Integer offset, Integer interval, 
			CustomCriteriaHandler<T> customCriteriaHandler, 
			CustomLazyHandler<T> customLazyHandler, 
			List<Order> orderByList)
	{
		return getDao(boType).findByExampleWithOrders(example, offset, interval, customCriteriaHandler, customLazyHandler, orderByList);
	}
	
	


	@Override
	public <T extends BaseBo, ID extends Serializable> T get(Class<T> boType,
			ID id) {
		return getDao(boType).get(id);
	}
	
	@Override
	public <T extends BaseBo, ID extends Serializable> T get(Class<T> boType,
			ID id, CustomLazyHandler<T> customLazyHandler) {
		return getDao(boType).get(id, customLazyHandler);
	}

	@Override
	public <T extends BaseBo> void saveList(Class<T> boType, List<T> list) {
		getDao(boType).saveList(list);
	}

	@Override
	public <T extends BaseBo> void save(Class<T> boType, T obj) {
		getDao(boType).save(obj);
	}

	@Override
	public <T extends BaseBo> void delete(Class<T> boType, T obj) {
		getDao(boType).delete(obj);
	}

	@Override
	public <T extends BaseBo> void deleteList(Class<T> boType, List<T> list) {
		getDao(boType).deleteList(list);
	}

}
