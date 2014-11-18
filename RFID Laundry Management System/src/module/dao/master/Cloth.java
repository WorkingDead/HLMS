package module.dao.master;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import module.dao.BaseBoMaster;
import module.dao.DaoFactory;
import module.dao.general.Receipt;
import module.dao.general.ReceiptPatternIron;
import module.dao.general.SpecialEvent;
import module.dao.general.Transaction;

@Entity
@Table(name=DaoFactory.CLOTH)
@org.hibernate.annotations.Table(appliesTo=DaoFactory.CLOTH,
indexes={
	@Index(name="ix_000", columnNames={"creation_date", "last_modify_date", "version", "created_by", "modified_by", "enable", "cloth_status", "code", "description", "last_receipt_code", "last_roll_container_code", "remark", "rfid", "washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_010", columnNames={"last_modify_date", "version", "created_by", "modified_by", "enable", "cloth_status", "code", "description", "last_receipt_code", "last_roll_container_code", "remark", "rfid", "washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_020", columnNames={"version", "created_by", "modified_by", "enable", "cloth_status", "code", "description", "last_receipt_code", "last_roll_container_code", "remark", "rfid", "washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_030", columnNames={"created_by", "modified_by", "enable", "cloth_status", "code", "description", "last_receipt_code", "last_roll_container_code", "remark", "rfid", "washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_040", columnNames={"modified_by", "enable", "cloth_status", "code", "description", "last_receipt_code", "last_roll_container_code", "remark", "rfid", "washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_050", columnNames={"enable", "cloth_status", "code", "description", "last_receipt_code", "last_roll_container_code", "remark", "rfid", "washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_060", columnNames={"cloth_status", "code", "description", "last_receipt_code", "last_roll_container_code", "remark", "rfid", "washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_070", columnNames={"code", "description", "last_receipt_code", "last_roll_container_code", "remark", "rfid", "washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_080", columnNames={"description", "last_receipt_code", "last_roll_container_code", "remark", "rfid", "washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_090", columnNames={"last_receipt_code", "last_roll_container_code", "remark", "rfid", "washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_100", columnNames={"last_roll_container_code", "remark", "rfid", "washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_110", columnNames={"remark", "rfid", "washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_120", columnNames={"remark", "rfid", "washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_130", columnNames={"rfid", "washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_140", columnNames={"washing_count", "washing_count_reset_date", "size"}),
	@Index(name="ix_150", columnNames={"washing_count_reset_date", "size"}),
	@Index(name="ix_160", columnNames={"size"}),
	} )
public class Cloth extends BaseBoMaster
{
	private static final long serialVersionUID = -7151971483644351163L;

	public static enum ClothStatus
	{
		Using("cloth.status.using"),		// wearing / dressing 
		Washing("cloth.status.washing"),	// after clothes are collected
		Ironing("cloth.status.ironing"),	// after clothes are washed
		Ready("cloth.status.ready"),		// on the cloth shelf (after washing)
		Lost("cloth.status.lost"),	// lost the cloth (Kiosk)
		Void("cloth.status.void");	// discard the cloth (Staff Page)
		
		private String value;

		ClothStatus(String value)
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
	
	public static List<ClothStatus> getClothStatusListWithoutLost()
	{
		return new LinkedList<ClothStatus>( Arrays.asList(
				ClothStatus.Using,
				ClothStatus.Washing,
				ClothStatus.Ironing,
				ClothStatus.Ready,
				ClothStatus.Void
			) );
	}



	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="code", unique=false, nullable=false)
	private String code;
	
	@Column(name="rfid", unique=true, nullable=false)
	private String rfid;
	
	@Transient
	private String oldRfid;
	
	@Column(name="washing_count")
	private Integer washingCount;

	@Column(name="washing_count_reset_date")
	private Calendar washingCountResetDate;
	
	@Column(name="description")
	private String description;
	
	@Column(name="size")
	private String size;
	
	@Column(name="remark")
	private String remark;
	
	@Column(name="last_receipt_code")
	private String lastReceiptCode;			// for Cloth-Racking (need to know Ironing-Delivery-Receipt-Code before racking)
	
	@Column(name="last_roll_container_code")
	private String lastRollContainerCode;	// for Handheld ironing-delivery-page
	
	@Column(name="cloth_status", nullable=false)
	@Enumerated(EnumType.STRING)
	private ClothStatus clothStatus;
	
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.LAZY, optional=true)
	@JoinColumn(name="zone_id", nullable=true)
	@ForeignKey(name="fk_zone_id")
	private Zone zone;
	
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.LAZY, optional=true)
	@JoinColumn(name="cloth_type_id", nullable=false)
	@ForeignKey(name="fk_cloth_type_id")
	private ClothType clothType;
	
	// Cloth is many, Staff is one
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.LAZY, optional=true)	// optional=true means 
	@JoinColumn(name="staff_id", nullable=true, insertable=false, updatable=false )	// add a "staff_id" column to cloth table, cloth cannot update staff if updatable=false  
	@ForeignKey(name="fk_staff_id")		// ForeignKey can reduce the time accessing the DB (specifically for query that join tables)
	private Staff staff;
	
	// for most receipt
	@ManyToMany(mappedBy="clothSet", fetch=FetchType.LAZY)
	private Set<Receipt> receiptSet;
	
	// special handle for ironing
	@ManyToMany(mappedBy="clothSet", fetch=FetchType.LAZY)
	private Set<ReceiptPatternIron> receiptPatternIronSet;
	
	// for Transaction
	@OneToMany(mappedBy="cloth", cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	private Set<Transaction> transactionSet;
	
	// for Special Event
	@OneToMany(mappedBy="cloth", cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	private Set<SpecialEvent> specialEventSet;
	
	@Transient
	private String displayField;
	
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
	public String getRfid()
	{
		return rfid;
	}
	public void setRfid(String rfid)
	{
		this.rfid = rfid;
	}
	public String getOldRfid() {
		return oldRfid;
	}
	public void setOldRfid(String oldRfid) {
		this.oldRfid = oldRfid;
	}
	public Integer getWashingCount() {
		return washingCount;
	}
	public void setWashingCount(Integer washingCount) {
		this.washingCount = washingCount;
	}
	public Calendar getWashingCountResetDate() {
		return washingCountResetDate;
	}
	public void setWashingCountResetDate(Calendar washingCountResetDate) {
		this.washingCountResetDate = washingCountResetDate;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getSize()
	{
		return size;
	}
	public void setSize(String size)
	{
		this.size = size;
	}
	public String getRemark()
	{
		return remark;
	}
	public void setRemark(String remark)
	{
		this.remark = remark;
	}
	public String getLastReceiptCode()
	{
		return lastReceiptCode;
	}
	public void setLastReceiptCode(String lastReceiptCode)
	{
		this.lastReceiptCode = lastReceiptCode;
	}
	public String getLastRollContainerCode()
	{
		return lastRollContainerCode;
	}
	public void setLastRollContainerCode(String lastRollContainerCode)
	{
		this.lastRollContainerCode = lastRollContainerCode;
	}
	public ClothStatus getClothStatus()
	{
		return clothStatus;
	}
	public void setClothStatus(ClothStatus clothStatus)
	{
		this.clothStatus = clothStatus;
	}
	public ClothType getClothType()
	{
		return clothType;
	}
	public void setClothType(ClothType clothType)
	{
		this.clothType = clothType;
	}
	public Staff getStaff()
	{
		return staff;
	}
	public void setStaff(Staff staff)
	{
		this.staff = staff;
	}
	public Set<Receipt> getReceiptSet()
	{
		return receiptSet;
	}
	public void setReceiptSet(Set<Receipt> receiptSet)
	{
		this.receiptSet = receiptSet;
	}
	public Set<ReceiptPatternIron> getReceiptPatternIronSet()
	{
		return receiptPatternIronSet;
	}
	public void setReceiptPatternIronSet(Set<ReceiptPatternIron> receiptPatternIronSet)
	{
		this.receiptPatternIronSet = receiptPatternIronSet;
	}
	public Zone getZone()
	{
		return zone;
	}
	public void setZone(Zone zone)
	{
		this.zone = zone;
	}
	public Set<Transaction> getTransactionSet()
	{
		return transactionSet;
	}
	public void setTransactionSet(Set<Transaction> transactionSet)
	{
		this.transactionSet = transactionSet;
	}
	public Set<SpecialEvent> getSpecialEventSet()
	{
		return specialEventSet;
	}
	public void setSpecialEventSet(Set<SpecialEvent> specialEventSet)
	{
		this.specialEventSet = specialEventSet;
	}
	public String getDisplayField()
	{
		return displayField;
	}
	public void setDisplayField(String displayField)
	{
		this.displayField = displayField;
	}
}
