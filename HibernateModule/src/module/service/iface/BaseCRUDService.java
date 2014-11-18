package module.service.iface;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.Order;

import module.dao.BaseBo;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;

/*
 * This is a base CRUD DAO
 * @author by kan 
 */
public interface BaseCRUDService<T extends BaseBo, ID extends Serializable> {
	
	//find count of row
	public Integer total();
	public Integer totalByExample(T example, CustomCriteriaHandler<T> customCriteriaHandler);
	
	//find list
	public List<T> findAll(Integer offset, Integer interval, CustomLazyHandler<T> customLazyHandler, Order orderBy);
	public List<T> findByExample(T example, Integer offset, Integer interval, CustomCriteriaHandler<T> customCriteriaHandler, CustomLazyHandler<T> customLazyHandler, Order orderBy);

	//get
	public abstract T get(ID id);
	public abstract T get(ID id, CustomLazyHandler<T> customLazyHandler);
	
	//save
	public void saveList(List<T> list);
	public void save(T obj);
	
	//delete
	public void delete(T obj);
	public void deleteList(List<T> list);
}
