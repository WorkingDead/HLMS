package module.dao;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

/*
 * This is use for calling some custom function before or after doing database work such as saving to database
 * Also, common field will put here  (base on javax.persistence)
 * @author by kan
 * testing
 */
@MappedSuperclass
public abstract class BaseBo implements Serializable {
	
	public BaseBo()// must have no parameter constructor!
	{}
	
	//Hibernate Optimistic Locking
	@Column(name="last_modify_date")
	@OptimisticLock(excluded=true)
	private Calendar lastModifyDate;
	
	//default creationDate
	@Column(name="creation_date", updatable=false)
	@OptimisticLock(excluded=true)
	private Calendar creationDate;

	public Calendar getLastModifyDate() {
		return lastModifyDate;
	}

	public void setLastModifyDate(Calendar lastModifyDate) {
		this.lastModifyDate = lastModifyDate;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}
	
	/*
	 * This function will call when object going to save to database
	 * if you don't want to save, you can return false or throw Exception for cut down the transaction
	 */
	public boolean OnBeforeSave()
	{
		creationDate = Calendar.getInstance();
		lastModifyDate = Calendar.getInstance();
		return true; //return true if it can be save
	}
	
	/*
	 * This function will call when object going to delete from database
	 * if you don't want to delete, you can return false or throw Exception for cut down the transaction
	 */
	public boolean OnBeforeDelete()
	{
		return true; //return true if it can be delete
	}

	/*
	 * This function will call when object going to update from database
	 * if you don't want to update, you can return false or throw Exception for cut down the transaction
	 */
	public boolean OnBeforeUpdate()
	{
		lastModifyDate = Calendar.getInstance();
		return true;
	}
	
	/*
	 * This function will call when query from database
	 */
	public void OnLoad(){}
	
	public String trimString(String string) {
		
		try {
			if ( string != null )
				return string.trim();
		}
		catch (Exception e) {
			return string;
		}
		
		return string;
	}
	
	@Deprecated
	public void postFlush(){}
	
	public void OnAfterSave(){}
	
	public void OnAfterUpdate(){}
	
	public void OnAfterDelete(){}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
