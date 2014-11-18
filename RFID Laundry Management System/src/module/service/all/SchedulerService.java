package module.service.all;

import java.util.List;

import org.hibernate.criterion.Order;

import module.dao.master.Stf;

import module.service.iface.BaseCRUDDaosService;

public interface SchedulerService extends BaseCRUDDaosService
{
	public int houseKeeping();
	
	public void transactionDatabaseArchive();



	public void save(Stf obj);
	
	public List<Stf> findAll(Order order);
	
	public void deleteList(List<Stf> list);
}
