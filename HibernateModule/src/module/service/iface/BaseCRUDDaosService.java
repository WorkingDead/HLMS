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
public interface BaseCRUDDaosService {
	
	//find count of row
	public Integer total(Class<? extends BaseBo> boType);
	public <T extends BaseBo> Integer totalByExample(Class<T> boType, T example, CustomCriteriaHandler<T> customCriteriaHandler);
	//find list
	public <T extends BaseBo> List<T> findAll(Class<T> boType, Integer offset, Integer interval, CustomLazyHandler<T> customLazyHandler, Order orderBy);
	public <T extends BaseBo> List<T> findByExample(Class<T> boType, T example, Integer offset, Integer interval, CustomCriteriaHandler<T> customCriteriaHandler, CustomLazyHandler<T> customLazyHandler, Order orderBy);
	
	// Add Multiple Order-By (by Horace)
	public <T extends BaseBo> List<T> findAllWithOrders(Class<T> boType, Integer offset, Integer interval, CustomLazyHandler<T> customLazyHandler, List<Order> orderByList);
	public <T extends BaseBo> List<T> findByExampleWithOrders(Class<T> boType, T example, Integer offset, Integer interval, CustomCriteriaHandler<T> customCriteriaHandler, CustomLazyHandler<T> customLazyHandler, List<Order> orderByList);


	//get
	public <T extends BaseBo, ID extends Serializable> T get(Class<T> boType, ID id);
	public <T extends BaseBo, ID extends Serializable> T get(Class<T> boType, ID id, CustomLazyHandler<T> customLazyHandler);
	
	//save
	public <T extends BaseBo> void saveList(Class<T> boType, List<T> list);
	public <T extends BaseBo> void save(Class<T> boType, T obj);
	
	//delete
	public <T extends BaseBo> void delete(Class<T> boType, T obj);
	public <T extends BaseBo> void deleteList(Class<T> boType, List<T> list);
}
