package module.dao.general;

import java.util.Calendar;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import module.dao.BaseBoGeneral;
import module.dao.DaoFactory;
import module.dao.master.Cloth;
import module.dao.master.RollContainer;

@Entity
@Table(name=DaoFactory.RECEIPT_PATTERN_IRON)
@org.hibernate.annotations.Table(appliesTo=DaoFactory.RECEIPT_PATTERN_IRON,
indexes={
	@Index(name="ix_000", columnNames={"creation_date", "last_modify_date", "version", "created_by", "modified_by", "ironing_delivery_time", "cloth_total"}),
	@Index(name="ix_010", columnNames={"last_modify_date", "version", "created_by", "modified_by", "ironing_delivery_time", "cloth_total"}),
	@Index(name="ix_020", columnNames={"version", "created_by", "modified_by", "ironing_delivery_time", "cloth_total"}),
	@Index(name="ix_030", columnNames={"created_by", "modified_by", "ironing_delivery_time", "cloth_total"}),
	@Index(name="ix_040", columnNames={"modified_by", "ironing_delivery_time", "cloth_total"}),
	@Index(name="ix_050", columnNames={"ironing_delivery_time", "cloth_total"}),
	@Index(name="ix_060", columnNames={"cloth_total"}),
	} )
public class ReceiptPatternIron extends BaseBoGeneral
{
	private static final long serialVersionUID = 1400299522935976046L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="cloth_total")
	private Integer patternClothTotal;	// how many clothes in this roll container
	
	@Column(name="ironing_delivery_time")
	private Calendar ironingDeliveryTime;
	
	// ReceiptPattern is many, Receipt is one
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.LAZY, optional=true)
	@JoinColumn(name="receipt_id", nullable=true, insertable=false, updatable=false )
	@ForeignKey(name="fk_receipt_id")
	private Receipt receipt;
	
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.EAGER, optional=true)
	@JoinColumn(name="roll_container_id", nullable=false)
	@ForeignKey(name="fk_roll_container_id")
	private RollContainer rollContainer;
	
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinTable(	name="receipt_pattern_iron_x_cloth",
				joinColumns={@JoinColumn(name="pattern_id", nullable=false, insertable=false, updatable=false) }, 
				inverseJoinColumns={ @JoinColumn(name="cloth_id", nullable=false, insertable=false, updatable=false) })
	@ForeignKey(name="fk_rpi_id", inverseName = "fk_cloth_id")
	private Set<Cloth> clothSet;

	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public Integer getPatternClothTotal()
	{
		return patternClothTotal;
	}
	public void setPatternClothTotal(Integer patternClothTotal)
	{
		this.patternClothTotal = patternClothTotal;
	}
	public Calendar getIroningDeliveryTime()
	{
		return ironingDeliveryTime;
	}
	public void setIroningDeliveryTime(Calendar ironingDeliveryTime)
	{
		this.ironingDeliveryTime = ironingDeliveryTime;
	}
	public Receipt getReceipt()
	{
		return receipt;
	}
	public void setReceipt(Receipt receipt)
	{
		this.receipt = receipt;
	}
	public RollContainer getRollContainer()
	{
		return rollContainer;
	}
	public void setRollContainer(RollContainer rollContainer)
	{
		this.rollContainer = rollContainer;
	}
	public Set<Cloth> getClothSet()
	{
		return clothSet;
	}
	public void setClothSet(Set<Cloth> clothSet)
	{
		this.clothSet = clothSet;
	}
}
