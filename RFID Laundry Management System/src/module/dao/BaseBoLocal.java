package module.dao;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class BaseBoLocal extends BaseBo
{
	private static final long serialVersionUID = -6722784835567316842L;

	// Hibernate Optimistic Locking
	@Version
	@Column(name = "version")
	private Calendar version;

	public Calendar getVersion()
	{
		return version;
	}

	public void setVersion(Calendar version)
	{
		this.version = version;
	}
}
