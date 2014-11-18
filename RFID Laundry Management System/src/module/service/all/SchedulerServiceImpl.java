package module.service.all;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;

import org.springframework.stereotype.Service;

import module.dao.DaoFactory;
import module.dao.HQLDao;
import module.dao.SQLDao;
import module.dao.master.Stf;
import module.dao.master.StfDao;

import module.service.ServiceFactory;
import module.service.impl.BaseCRUDDaosServiceImpl;

@Service(ServiceFactory.SchedulerService)
public class SchedulerServiceImpl extends BaseCRUDDaosServiceImpl implements SchedulerService
{
	//Log4j Logger
	private final Logger log = Logger.getLogger( getClass() );
	
	@Resource(name=DaoFactory.HQL_DAO)
	public HQLDao hqlDao;
	
	@Resource(name=DaoFactory.SQL_DAO)
	public SQLDao sqlDao;

	@Resource(name=DaoFactory.STF_DAO)
	public StfDao stfDao;



	@Override
	public int houseKeeping() {

		return hqlDao.removeAllProcessedXmlCache();

		//TODO
		//return 0;
	}
	
	@Override
	public void transactionDatabaseArchive() {
		
//		Calendar c = Calendar.getInstance();
//		c.add(Calendar.MONTH, -3);				//Three Months Ago
//		c.getTime();
		
//		System.out.println();
//		System.out.println( "C = " + DateConverter.format(c, DateConverter.DB_DATETIME_FORMAT));
//		System.out.println();
		
		String tableIdentityColumnName = "id";
		String targetTableName = "transaction";
		String targetTableArchiveName = "transactionArchive";
//		String whereStatementString = "creation_date <= '" + DateConverter.format(c, DateConverter.DB_DATETIME_FORMAT) + "'";
		String whereColumnName = "creation_date";
		String numOfRowAffected = "500";

		sqlDao.archiveTable( tableIdentityColumnName, targetTableName, targetTableArchiveName, whereColumnName, numOfRowAffected );
	}



	@Override
	public void save(Stf obj) {
		stfDao.save(obj);
	}
	
	@Override
	public List<Stf> findAll(Order order) {
		return stfDao.findAll(order);
	}
	
	@Override
	public void deleteList(List<Stf> list) {
		stfDao.deleteList(list);
	}
}