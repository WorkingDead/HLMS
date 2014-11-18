package module.dao.general;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import module.dao.BaseBoGeneral;
import module.dao.DaoFactory;

@Entity
@Table(name=DaoFactory.HISTORY_CLOTH)
@org.hibernate.annotations.Table(appliesTo=DaoFactory.HISTORY_CLOTH,
indexes={
	@Index(name="ix_000", columnNames={"creation_date", "last_modify_date", "version", "created_by", "modified_by", "rfid", "receipt_id"}),
	@Index(name="ix_010", columnNames={"last_modify_date", "version", "created_by", "modified_by", "rfid", "receipt_id"}),
	@Index(name="ix_020", columnNames={"version", "created_by", "modified_by", "rfid", "receipt_id"}),
	@Index(name="ix_030", columnNames={"created_by", "modified_by", "rfid", "receipt_id"}),
	@Index(name="ix_040", columnNames={"modified_by", "rfid", "receipt_id"}),
	} )
public class HistoryCloth extends BaseBoGeneral
{
	private static final long serialVersionUID = 620985911507298065L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="rfid", unique=false, nullable=false)
	private String rfid;
	
	//"optional" and "nullable" should be false in design here.
	//However, they are true just because no error in the system flow cannot be found.
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.LAZY, optional=true)
	@JoinColumn(name="receipt_id", nullable=true, insertable=false, updatable=false )
	@ForeignKey(name="fk_receipt_id")
	private Receipt receipt;
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public String getRfid()
	{
		return rfid;
	}
	public void setRfid(String rfid)
	{
		this.rfid = rfid;
	}
	public Receipt getReceipt()
	{
		return receipt;
	}
	public void setReceipt(Receipt receipt)
	{
		this.receipt = receipt;
	}
}
