package module.dao.general;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import module.dao.BaseBoGeneral;
import module.dao.DaoFactory;
import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.Staff;

@Entity
@Table(name=DaoFactory.SPECIAL_EVENT)
@Inheritance(strategy=InheritanceType.JOINED)
@org.hibernate.annotations.Table(appliesTo=DaoFactory.SPECIAL_EVENT,
indexes={
	@Index(name="ix_000", columnNames={"creation_date", "last_modify_date", "version", "created_by", "modified_by", "sp_event_name", "sp_event_status"}),
	@Index(name="ix_010", columnNames={"last_modify_date", "version", "created_by", "modified_by", "sp_event_name", "sp_event_status"}),
	@Index(name="ix_020", columnNames={"version", "created_by", "modified_by", "sp_event_name", "sp_event_status"}),
	@Index(name="ix_030", columnNames={"created_by", "modified_by", "sp_event_name", "sp_event_status"}),
	@Index(name="ix_040", columnNames={"modified_by", "sp_event_name", "sp_event_status"}),
	@Index(name="ix_050", columnNames={"sp_event_name", "sp_event_status"}),
	@Index(name="ix_060", columnNames={"sp_event_status"}),
	} )
public class SpecialEvent extends BaseBoGeneral
{
	private static final long serialVersionUID = -8005068034376089351L;

	public static enum SpecialEventName
	{
		ClothLost("special.event.cloth.lost"),
		ClothFound("special.event.cloth.found"),
		ClothIroningDelay("special.event.ironing.delay"),
		StaffResigned("special.event.staff.resigned");
		
		private String value;

		SpecialEventName(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
		public void setValue(String value)
		{
			this.value = value;
		}
	}
	
	public static enum SpecialEventStatus
	{
		Followup("special.event.status.followup"),
		Finished("special.event.status.finished");
		
		private String value;

		SpecialEventStatus(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return value;
		}
		public void setValue(String value)
		{
			this.value = value;
		}
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="sp_event_name", nullable=false)
	@Enumerated(EnumType.STRING)
	private SpecialEventName specialEventName;
	
	@Column(name="sp_event_status", nullable=false)
	@Enumerated(EnumType.STRING)
	private SpecialEventStatus specialEventStatus;

	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.EAGER, optional=true)
	@JoinColumn(name="sp_event_cloth_type_id", nullable=true)
	@ForeignKey(name="fk_sp_event_cloth_type_id")
	private ClothType clothType;
	
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.EAGER, optional=true)
	@JoinColumn(name="sp_event_cloth_id", nullable=true)
	@ForeignKey(name="fk_sp_event_cloth_id")
	private Cloth cloth;
	
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.LAZY, optional=true)
	@JoinColumn(name="sp_event_staff_id", nullable=true)
	@ForeignKey(name="fk_sp_event_staff_id")
	private Staff staff;

	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public SpecialEventName getSpecialEventName()
	{
		return specialEventName;
	}
	public void setSpecialEventName(SpecialEventName specialEventName)
	{
		this.specialEventName = specialEventName;
	}
	public SpecialEventStatus getSpecialEventStatus()
	{
		return specialEventStatus;
	}
	public void setSpecialEventStatus(SpecialEventStatus specialEventStatus)
	{
		this.specialEventStatus = specialEventStatus;
	}
	public Cloth getCloth()
	{
		return cloth;
	}
	public void setCloth(Cloth cloth)
	{
		this.cloth = cloth;
	}
	public Staff getStaff()
	{
		return staff;
	}
	public void setStaff(Staff staff)
	{
		this.staff = staff;
	}
	public ClothType getClothType()
	{
		return clothType;
	}
	public void setClothType(ClothType clothType)
	{
		this.clothType = clothType;
	}
}
