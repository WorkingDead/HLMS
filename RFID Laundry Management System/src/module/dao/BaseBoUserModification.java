package module.dao;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.springframework.security.core.context.SecurityContextHolder;

import module.dao.system.Users;

@MappedSuperclass
public abstract class BaseBoUserModification extends BaseBoLocal
{
	private static final long serialVersionUID = -8367603454607914802L;
	
	@ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
	@JoinColumn(name = "created_by")
	private Users createdBy;

	@ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
	@JoinColumn(name = "modified_by")
	private Users modifiedBy;

	public Users getCreatedBy()
	{
		return createdBy;
	}
	public void setCreatedBy(Users createdBy)
	{
		this.createdBy = createdBy;
	}
	public Users getModifiedBy()
	{
		return modifiedBy;
	}
	public void setModifiedBy(Users modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}
	private Users getUser()
	{
		try
		{
			return (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		catch (Exception e)
		{
			return null; // no login
		}
	}

	@Override
	public boolean OnBeforeSave()
	{
		if (!super.OnBeforeSave())
			return false;

		Users user = getUser();
		if (user != null)
		{
			this.setCreatedBy(user);
		}
		
		return true;
	}

	@Override
	public boolean OnBeforeUpdate()
	{
		if (!super.OnBeforeUpdate())
			return false;

		Users user = getUser();

		if (user != null)
		{
			this.setModifiedBy(user);
		}
		return true;// no need to call super before update, it will call
					// OnBeforeSave again due to compatible for old version
	}
}
