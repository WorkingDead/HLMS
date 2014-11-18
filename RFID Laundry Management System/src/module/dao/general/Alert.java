package module.dao.general;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import module.dao.BaseBoGeneral;
import module.dao.DaoFactory;

@Entity
@Table(name=DaoFactory.ALERT)
public class Alert extends BaseBoGeneral
{
	private static final long serialVersionUID = 6248150982324212264L;

	public enum AlertType
	{
		IroningExpiryPeriod,
	}
	
	@Id
	@Enumerated(EnumType.STRING)
	private AlertType id;
	
	@Column(name="hours")
	private Integer hours;

	public AlertType getId()
	{
		return id;
	}
	public void setId(AlertType id)
	{
		this.id = id;
	}

	public Integer getHours()
	{
		return hours;
	}
	public void setHours(Integer hours)
	{
		this.hours = hours;
	}
}
