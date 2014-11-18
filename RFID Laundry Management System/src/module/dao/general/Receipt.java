package module.dao.general;

import java.util.Calendar;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import module.dao.BaseBoGeneral;
import module.dao.DaoFactory;
import module.dao.master.Cloth;
import module.dao.master.Staff;

@Entity
@Table(name=DaoFactory.RECEIPT)
@org.hibernate.annotations.Table(appliesTo=DaoFactory.RECEIPT,
indexes={
	@Index(name="ix_000", columnNames={"creation_date", "last_modify_date", "version", "created_by", "modified_by", "code", "finish_date", "receipt_cloth_total", "receipt_status", "receipt_type", "remark"}),
	@Index(name="ix_010", columnNames={"last_modify_date", "version", "created_by", "modified_by", "code", "finish_date", "receipt_cloth_total", "receipt_status", "receipt_type", "remark"}),
	@Index(name="ix_020", columnNames={"version", "created_by", "modified_by", "code", "finish_date", "receipt_cloth_total", "receipt_status", "receipt_type", "remark"}),
	@Index(name="ix_030", columnNames={"created_by", "modified_by", "code", "finish_date", "receipt_cloth_total", "receipt_status", "receipt_type", "remark"}),
	@Index(name="ix_040", columnNames={"modified_by", "code", "finish_date", "receipt_cloth_total", "receipt_status", "receipt_type", "remark"}),
	@Index(name="ix_050", columnNames={"code", "finish_date", "receipt_cloth_total", "receipt_status", "receipt_type", "remark"}),
	@Index(name="ix_060", columnNames={"finish_date", "receipt_cloth_total", "receipt_status", "receipt_type", "remark"}),
	@Index(name="ix_070", columnNames={"receipt_cloth_total", "receipt_status", "receipt_type", "remark"}),
	@Index(name="ix_080", columnNames={"receipt_status", "receipt_type", "remark"}),
	@Index(name="ix_090", columnNames={"receipt_type", "remark"}),
	@Index(name="ix_100", columnNames={"remark"}),
	} )
public class Receipt extends BaseBoGeneral
{
	private static final long serialVersionUID = 2790045926159914634L;

	public static enum ReceiptType
	{
		Collect("receipt.type.collect"),
		Iron("receipt.type.iron"),
		Rack("receipt.type.rack"),
		Distribute("receipt.type.distribute");
		
		private String value;

		ReceiptType(String value)
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
	
	public static enum ReceiptStatus
	{
		Processing("receipt.status.processing"),
		Finished("receipt.status.finished"),
		Followup("receipt.status.followup");
		
		private String value;

		ReceiptStatus(String value)
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
	
	@Column(name="code", unique=true, nullable=false)
	private String code;
	
	@Column(name="receipt_type", nullable=false)
	@Enumerated(EnumType.STRING)
	private ReceiptType receiptType;
	
	@Column(name="receipt_status", nullable=false)
	@Enumerated(EnumType.STRING)
	private ReceiptStatus receiptStatus;
	
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.LAZY, optional=true)
	@JoinColumn(name="staff_handled_by_id", nullable=true)
	@ForeignKey(name="fk_staff_handled_by_id")
	private Staff staffHandledBy;
	
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.LAZY, optional=true)
	@JoinColumn(name="staff_picked_by_id", nullable=true)
	@ForeignKey(name="fk_staff_picked_by_id")
	private Staff staffPickedBy;
	
	
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinTable(	name="receipt_x_cloth", 
			joinColumns={@JoinColumn(name="receipt_id", nullable=false, insertable=false, updatable=false) }, 
			inverseJoinColumns={ @JoinColumn(name="cloth_id", nullable=false, insertable=false, updatable=false) })
	@ForeignKey(name="fk_cr_id", inverseName = "fk_cloth_id")
	private Set<Cloth> clothSet;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="receipt_id")
	@OrderBy("id")
	private Set<ReceiptPatternIron> receiptPatternIronSet;
	
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="receipt_id")
	@OrderBy("id")
	private Set<HistoryCloth> historyClothSet;
	
	
	// already declared in SuperClass: createdBy
//	private Users handledByUser;
	
	// already declared in SuperClass
//	private Calendar creationDate;
	
	@Column(name="finish_date")
	private Calendar finishDate;	// finishing time of this receipt
	
	
//	@Column(name="ironing_delivery_time")
//	private Calendar ironingDeliveryTime;	// for ironing delivery
	
	
	@Column(name="receipt_cloth_total")
	private Integer receiptClothTotal;
	
	
	@Column(name="remark")
	private String remark;
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}

	public String getCode()
	{
		return code;
	}
	public void setCode(String code)
	{
		this.code = code;
	}

	public ReceiptType getReceiptType()
	{
		return receiptType;
	}
	public void setReceiptType(ReceiptType receiptType)
	{
		this.receiptType = receiptType;
	}

	public ReceiptStatus getReceiptStatus()
	{
		return receiptStatus;
	}
	public void setReceiptStatus(ReceiptStatus receiptStatus)
	{
		this.receiptStatus = receiptStatus;
	}
	
	public Calendar getFinishDate()
	{
		return finishDate;
	}
	public void setFinishDate(Calendar finishDate)
	{
		this.finishDate = finishDate;
	}
	public Integer getReceiptClothTotal()
	{
		return receiptClothTotal;
	}
	public void setReceiptClothTotal(Integer receiptClothTotal)
	{
		this.receiptClothTotal = receiptClothTotal;
	}
	public String getRemark()
	{
		return remark;
	}
	public void setRemark(String remark)
	{
		this.remark = remark;
	}
	public Set<ReceiptPatternIron> getReceiptPatternIronSet()
	{
		return receiptPatternIronSet;
	}
	public void setReceiptPatternIronSet(Set<ReceiptPatternIron> receiptPatternIronSet)
	{
		this.receiptPatternIronSet = receiptPatternIronSet;
	}
	public Set<Cloth> getClothSet()
	{
		return clothSet;
	}
	public void setClothSet(Set<Cloth> clothSet)
	{
		this.clothSet = clothSet;
	}
	public Staff getStaffHandledBy()
	{
		return staffHandledBy;
	}
	public void setStaffHandledBy(Staff staffHandledBy)
	{
		this.staffHandledBy = staffHandledBy;
	}
	public Staff getStaffPickedBy()
	{
		return staffPickedBy;
	}
	public void setStaffPickedBy(Staff staffPickedBy)
	{
		this.staffPickedBy = staffPickedBy;
	}
	public Set<HistoryCloth> getHistoryClothSet()
	{
		return historyClothSet;
	}
	public void setHistoryClothSet(Set<HistoryCloth> historyClothSet)
	{
		this.historyClothSet = historyClothSet;
	}
}
