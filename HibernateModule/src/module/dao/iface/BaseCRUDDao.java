package module.dao.iface;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.Order;

import module.dao.BaseBo;

/*
 * This is a base CRUD DAO
 * @author by kan 
 */
public interface BaseCRUDDao<T extends BaseBo, ID extends Serializable> {
	
	//find count of row
	public abstract Integer total();
	public abstract Integer totalByExample(T example, CustomCriteriaHandler<T> customCriteriaHandler);
	
	//find list
	public abstract List<T> findAll(Integer offset, Integer interval, CustomLazyHandler<T> customLazyHandler, Order orderBy);
	public abstract List<T> findByExample(T example, Integer offset, Integer interval, CustomCriteriaHandler<T> customCriteriaHandler, CustomLazyHandler<T> customLazyHandler, Order orderBy);
	// Add Multiple Order-By (by Horace)
	public abstract List<T> findAllWithOrders(Integer offset, Integer interval, CustomLazyHandler<T> customLazyHandler, List<Order> orderByList);
	public abstract List<T> findByExampleWithOrders(T example, Integer offset, Integer interval, CustomCriteriaHandler<T> customCriteriaHandler, CustomLazyHandler<T> customLazyHandler, List<Order> orderByList);
	
	//get
	public abstract T get(ID id);
	public abstract T get(ID id, CustomLazyHandler<T> customLazyHandler);
	
	//save
	public abstract void saveList(List<T> list);
	public abstract void save(T obj);
	
	//delete
	public abstract void delete(T obj);
	public abstract void deleteList(List<T> list);
}
