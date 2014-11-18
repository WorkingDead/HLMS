package module.service.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.Order;

import module.dao.BaseBo;
import module.dao.iface.BaseCRUDDao;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.service.iface.BaseCRUDService;

public abstract class BaseCRUDServiceImpl<T extends BaseBo, ID extends Serializable> implements BaseCRUDService<T, ID> {
	
	public abstract BaseCRUDDao<T, ID> getBaseDao();
	
	@Override
	public Integer total() {
		return getBaseDao().total();
	}

	@Override
	public Integer totalByExample(T example,
			CustomCriteriaHandler<T> customCriteriaHandler) {
		return getBaseDao().totalByExample(example, customCriteriaHandler);
	}

	@Override
	public List<T> findAll(Integer offset, Integer interval,
			CustomLazyHandler<T> customLazyHandler, Order orderBy) {
		return getBaseDao().findAll(offset, interval, customLazyHandler, orderBy);
	}

	@Override
	public List<T> findByExample(T example, Integer offset, Integer interval,
			CustomCriteriaHandler<T> customCriteriaHandler,
			CustomLazyHandler<T> customLazyHandler, Order orderBy) {
		return getBaseDao().findByExample(example, offset, interval, customCriteriaHandler, customLazyHandler, orderBy);
	}

	@Override
	public T get(ID id) {
		return this.getBaseDao().get(id);
	}
	
	@Override
	public T get(ID id, CustomLazyHandler<T> customLazyHandler) {
		return this.getBaseDao().get(id, customLazyHandler);
	}

	@Override
	public void saveList(List<T> list) {
		getBaseDao().saveList(list);
	}

	@Override
	public void save(T obj) {
		getBaseDao().save(obj);
	}

	@Override
	public void delete(T obj) {
		getBaseDao().delete(obj);
	}

	@Override
	public void deleteList(List<T> list) {
		getBaseDao().deleteList(list);
	}

}
