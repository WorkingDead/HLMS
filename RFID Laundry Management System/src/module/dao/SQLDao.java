package module.dao;

public interface SQLDao
{
//	public int archiveAllTransaction();
//	
//	public int archiveTesting();
	
	public void archiveTable( String tableIdentityColumnName, String targetTableName, String targetTableArchiveName, String whereColumnName, String numOfRowAffected );
}
