package module.dao;

import module.dao.general.Transaction;
import module.dao.impl.BaseDaoImpl;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository(DaoFactory.SQL_DAO)
public class SQLDaoImpl extends BaseDaoImpl implements SQLDao
{
//	@Override
//	public int archiveAllTransaction() {
//		Query q = super.getSessionFactory().getCurrentSession()
//				.createSQLQuery("{call GetStocks(:stockCode, :stock)}")
//				.addEntity(ClothType.class);
//
//		q.setParameter("stockCode", "醫院護士衫(女)");
//		q.setParameter("stock", "醫院");
//		
//		return q.list().size();
//	}
	
//	@Override
//	public int archiveTesting() {
//		Query q = super.getSessionFactory().getCurrentSession()
//				.createSQLQuery("{call TestArchive(:stockCode, :stock)}")
//				.addEntity(Transaction.class);
//		
//		q.setParameter("stockCode", 1);
//		q.setParameter("stock", 2);
//		
//		return q.executeUpdate();
//	}
	
	@Override
	public void archiveTable( String tableIdentityColumnName, String targetTableName, String targetTableArchiveName, String whereColumnName, String numOfRowAffected ) {
		
		Query q = super.getSessionFactory().getCurrentSession()
				.createSQLQuery("{call ArchiveCore(:tableIdentityColumnName, :targetTableName, :targetTableArchiveName, :whereColumnName, :numOfRowAffected)}")
				.addEntity(Transaction.class);

		q.setParameter("tableIdentityColumnName", tableIdentityColumnName);
		q.setParameter("targetTableName", targetTableName);
		q.setParameter("targetTableArchiveName", targetTableArchiveName);
//		q.setParameter("whereStatementString", whereStatementString);
		q.setParameter("whereColumnName", whereColumnName);
		q.setParameter("numOfRowAffected", numOfRowAffected);

		q.executeUpdate();
	}
}
