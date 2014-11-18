package module.dao.master;

import java.util.Calendar;
import java.util.HashSet;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import module.dao.BaseBoMaster;
import module.dao.DaoFactory;
import module.dao.general.Receipt;

@Entity
@Table(name=DaoFactory.STAFF)
@org.hibernate.annotations.Table(appliesTo=DaoFactory.STAFF,
indexes={
	@Index(name="ix_000", columnNames={"creation_date", "last_modify_date", "version", "created_by", "modified_by", "enable", "code", "card_number", "name_eng", "name_cht", "position", "staff_status"}),
	@Index(name="ix_010", columnNames={"last_modify_date", "version", "created_by", "modified_by", "enable", "code", "card_number", "name_eng", "name_cht", "position", "staff_status"}),
	@Index(name="ix_020", columnNames={"version", "created_by", "modified_by", "enable", "code", "card_number", "name_eng", "name_cht", "position", "staff_status"}),
	@Index(name="ix_030", columnNames={"created_by", "modified_by", "enable", "code", "card_number", "name_eng", "name_cht", "position", "staff_status"}),
	@Index(name="ix_040", columnNames={"modified_by", "enable", "code", "card_number", "name_eng", "name_cht", "position", "staff_status"}),
	@Index(name="ix_050", columnNames={"enable", "code", "card_number", "name_eng", "name_cht", "position", "staff_status"}),
	@Index(name="ix_060", columnNames={"code", "card_number", "name_eng", "name_cht", "position", "staff_status"}),
	@Index(name="ix_070", columnNames={"card_number", "name_eng", "name_cht", "position", "staff_status"}),
	@Index(name="ix_080", columnNames={"name_eng", "name_cht", "position", "staff_status"}),
	@Index(name="ix_090", columnNames={"name_cht", "position", "staff_status"}),
	@Index(name="ix_100", columnNames={"position", "staff_status"}),
	@Index(name="ix_110", columnNames={"staff_status"}),
	} )
public class Staff extends BaseBoMaster
{
	//Log4j Logger
	private static final Logger log = Logger.getLogger(Staff.class);
	
	private static final long serialVersionUID = -7609520321027366606L;

	public static enum StaffStatus
	{
		Normal("staff.status.normal"),
		Leave("staff.status.leave");
		
		private String value;

		StaffStatus(String value)
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
	
	@Column(name="card_number", unique=true, nullable=false)
	private String cardNumber;
	
	@Column(name="name_eng", nullable=false)
	private String nameEng;
	
	@Column(name="name_cht", nullable=false)
	private String nameCht;
	
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.LAZY, optional=true)
	@JoinColumn(name="dept_id", nullable=false)
	@ForeignKey(name="fk_dept_id")
	private Department dept;
	
	@Column(name="position", nullable=false)
	private String position;
	
	@Column(name="staff_status", nullable=false)
	@Enumerated(EnumType.STRING)
	private StaffStatus staffStatus;

//	@OneToMany(mappedBy="staff", cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="staff_id")	// must be same as @JoinColumn at Cloth
	@OrderBy("id")
	private Set<Cloth> clothSet = new HashSet<Cloth>();
	
	@OneToMany(mappedBy="staffHandledBy", cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	private Set<Receipt> handledByReceiptSet = new HashSet<Receipt>();
	
	@OneToMany(mappedBy="staffPickedBy", cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	private Set<Receipt> pickedByReceiptSet = new HashSet<Receipt>();



	@Override
	public boolean OnBeforeSave() {

		if( !super.OnBeforeSave() )
			return false;

		return OnBeforeSaveOrUpdateOfStaff();
	}
	
	@Override
	public boolean OnBeforeUpdate() {

		if( !super.OnBeforeUpdate() )
			return false;
		
		return OnBeforeSaveOrUpdateOfStaff();
	}
	
	public boolean OnBeforeSaveOrUpdateOfStaff() {
		
		boolean hasClothSet = this.getClothSet() != null;
		
		if ( hasClothSet ) {
			
			this.getClothSet().size();
			
			for ( Cloth cloth: this.getClothSet() ) {
			
				if ( cloth.getOldRfid() == null || cloth.getOldRfid().trim().isEmpty() ) {
					//Do Nothing
				}
				else {
					
					if ( cloth.getOldRfid().trim().equals( cloth.getRfid().trim() ) ) {
						cloth.setOldRfid( null );
					}
					else {
						cloth.setOldRfid( null );
						
						//Reset Washing Count Here
						cloth.setWashingCount( 0 );
						cloth.setWashingCountResetDate( Calendar.getInstance() );
						
						log.info( "staff id (" + this.getId() + ") cloth id (" + cloth.getId() + ") is reseted and now washing count = " 
						+ cloth.getWashingCount() + " and washing count reset date = " + cloth.getWashingCountResetDate().getTime() );
						//Reset Washing Count Here
					}
				}
			}
		}

		return true;
	}



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
	public String getCardNumber()
	{
		return cardNumber;
	}
	public void setCardNumber(String cardNumber)
	{
		this.cardNumber = cardNumber;
	}
	public String getNameEng()
	{
		return nameEng;
	}
	public void setNameEng(String nameEng)
	{
		this.nameEng = nameEng;
	}
	public String getNameCht()
	{
		return nameCht;
	}
	public void setNameCht(String nameCht)
	{
		this.nameCht = nameCht;
	}
	public Department getDept()
	{
		return dept;
	}
	public void setDept(Department dept)
	{
		this.dept = dept;
	}
	public String getPosition()
	{
		return position;
	}
	public void setPosition(String position)
	{
		this.position = position;
	}
	public StaffStatus getStaffStatus()
	{
		return staffStatus;
	}
	public void setStaffStatus(StaffStatus staffStatus)
	{
		this.staffStatus = staffStatus;
	}
	public Set<Cloth> getClothSet()
	{
		return clothSet;
	}
	public void setClothSet(Set<Cloth> clothSet)
	{
		this.clothSet = clothSet;
	}
	public Set<Receipt> getHandledByReceiptSet()
	{
		return handledByReceiptSet;
	}
	public void setHandledByReceiptSet(Set<Receipt> handledByReceiptSet)
	{
		this.handledByReceiptSet = handledByReceiptSet;
	}
	public Set<Receipt> getPickedByReceiptSet()
	{
		return pickedByReceiptSet;
	}
	public void setPickedByReceiptSet(Set<Receipt> pickedByReceiptSet)
	{
		this.pickedByReceiptSet = pickedByReceiptSet;
	}
	
}
