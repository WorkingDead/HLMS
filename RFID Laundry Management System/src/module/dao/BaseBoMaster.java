package module.dao;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseBoMaster extends BaseBoUserModification
{
	private static final long serialVersionUID = -6446761794294778965L;
	
	@Column(name="enable", nullable=false)
	private Boolean enable;
	
	public Boolean getEnable()
	{
		return enable;
	}
	public void setEnable(Boolean enable)
	{
		this.enable = enable;
	}
}
