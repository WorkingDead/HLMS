package module.dao.master;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import module.dao.BaseBoMaster;
import module.dao.DaoFactory;
import module.dao.general.ReceiptPatternIron;

@Entity
@Table(name=DaoFactory.ROLL_CONTAINER)
@org.hibernate.annotations.Table(appliesTo=DaoFactory.ROLL_CONTAINER,
indexes={
	@Index(name="ix_000", columnNames={"creation_date", "last_modify_date", "version", "created_by", "modified_by", "enable", "code"}),
	@Index(name="ix_010", columnNames={"last_modify_date", "version", "created_by", "modified_by", "enable", "code"}),
	@Index(name="ix_020", columnNames={"version", "created_by", "modified_by", "enable", "code"}),
	@Index(name="ix_030", columnNames={"created_by", "modified_by", "enable", "code"}),
	@Index(name="ix_040", columnNames={"modified_by", "enable", "code"}),
	@Index(name="ix_050", columnNames={"enable", "code"}),
	@Index(name="ix_060", columnNames={"code"}),
	} )
public class RollContainer extends BaseBoMaster
{
	private static final long serialVersionUID = -4788404401275157790L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="code", unique=true, nullable=false)
	private String code;
	
	@OneToMany(mappedBy="rollContainer", cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	private Set<ReceiptPatternIron> receiptPatternIronSet;
	
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
	public Set<ReceiptPatternIron> getReceiptPatternIronSet()
	{
		return receiptPatternIronSet;
	}
	public void setReceiptPatternIronSet(Set<ReceiptPatternIron> receiptPatternIronSet)
	{
		this.receiptPatternIronSet = receiptPatternIronSet;
	}
}
