package module.dao.master;

import java.util.List;

import org.hibernate.criterion.Order;

public interface StfDao {

	public abstract void save(Stf obj);
	
	public abstract List<Stf> findAll(Order order);
	
	public abstract void deleteList(List<Stf> list);
}
