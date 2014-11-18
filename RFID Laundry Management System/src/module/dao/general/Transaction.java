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

import module.dao.BaseBoGeneral;
import module.dao.DaoFactory;
import module.dao.master.Cloth;
import module.dao.master.Staff;

//Cannot use this because HibernateModule is fixed currently
//@NamedNativeQueries(  
//		  value = {
//				  @NamedNativeQuery(
//						  name = "callTransactionDatabaseArchiveStoreProcedure",
//						  query = "CALL GetStocks(:stockCode)",
//						  resultClass = Transaction.class)
//		  }
//		)
//Cannot use this because HibernateModule is fixed currently
@Entity
@Table(name=DaoFactory.TRANSACTION)
@Inheritance(strategy=InheritanceType.JOINED)
//@org.hibernate.annotations.Table(appliesTo=DaoFactory.TRANSACTION,		//Cannot escape the table name!!!
//indexes={
//	@Index(name="ix_000", columnNames={"creation_date", "last_modify_date", "version", "created_by", "modified_by", "trans_name"}),
//	@Index(name="ix_010", columnNames={"last_modify_date", "version", "created_by", "modified_by", "trans_name"}),
//	@Index(name="ix_020", columnNames={"version", "created_by", "modified_by", "trans_name"}),
//	@Index(name="ix_030", columnNames={"created_by", "modified_by", "trans_name"}),
//	@Index(name="ix_040", columnNames={"modified_by", "trans_name"}),
//	@Index(name="ix_050", columnNames={"trans_name"}),
//	} )
public class Transaction extends BaseBoGeneral
{
	private static final long serialVersionUID = -3337375100389661734L;
	
	public static enum TransactionName
	{
		Collection("transaction.collection"),
		IroningDelivery("transaction.ironing.delivery"),
		Racking("transaction.racking"),
		Distribution("transaction.distribution");
		
		private String value;

		TransactionName(String value)
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
	
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.EAGER, optional=true)
	@JoinColumn(name="trans_cloth_id", nullable=false)
	@ForeignKey(name="fk_trans_cloth_id")
	private Cloth cloth;
	
	@Column(name="trans_name", nullable=false)
	@Enumerated(EnumType.STRING)
	private TransactionName transactionName;
	
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.LAZY, optional=true)
	@JoinColumn(name="handled_by_staff_id", nullable=true)
	@ForeignKey(name="fk_hbs_id")
	private Staff transHandledByStaff;
	
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.LAZY, optional=true)
	@JoinColumn(name="picked_by_staff_id", nullable=true)
	@ForeignKey(name="fk_pbs_id")
	private Staff transPickedByStaff;
	
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public Cloth getCloth()
	{
		return cloth;
	}
	public void setCloth(Cloth cloth)
	{
		this.cloth = cloth;
	}
	public TransactionName getTransactionName()
	{
		return transactionName;
	}
	public void setTransactionName(TransactionName transactionName)
	{
		this.transactionName = transactionName;
	}
	public Staff getTransHandledByStaff()
	{
		return transHandledByStaff;
	}
	public void setTransHandledByStaff(Staff transHandledByStaff)
	{
		this.transHandledByStaff = transHandledByStaff;
	}
	public Staff getTransPickedByStaff()
	{
		return transPickedByStaff;
	}
	public void setTransPickedByStaff(Staff transPickedByStaff)
	{
		this.transPickedByStaff = transPickedByStaff;
	}
}
