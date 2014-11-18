package module.dao.master;

import java.util.HashSet;
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

@Entity
@Table(name=DaoFactory.DEPARTMENT)
@org.hibernate.annotations.Table(appliesTo=DaoFactory.DEPARTMENT,
indexes={
	@Index(name="ix_000", columnNames={"creation_date", "last_modify_date", "version", "created_by", "modified_by", "enable", "name_eng", "name_cht"}),
	@Index(name="ix_010", columnNames={"last_modify_date", "version", "created_by", "modified_by", "enable", "name_eng", "name_cht"}),
	@Index(name="ix_020", columnNames={"version", "created_by", "modified_by", "enable", "name_eng", "name_cht"}),
	@Index(name="ix_030", columnNames={"created_by", "modified_by", "enable", "name_eng", "name_cht"}),
	@Index(name="ix_040", columnNames={"modified_by", "enable", "name_eng", "name_cht"}),
	@Index(name="ix_050", columnNames={"enable", "name_eng", "name_cht"}),
	@Index(name="ix_060", columnNames={"name_eng", "name_cht"}),
	@Index(name="ix_070", columnNames={"name_cht"}),
	} )
public class Department extends BaseBoMaster
{
	private static final long serialVersionUID = -8704801174059619127L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="name_eng", unique=true, nullable=false)
	private String nameEng;
	
	@Column(name="name_cht", unique=true, nullable=false)
	private String nameCht;

	@OneToMany(mappedBy="dept", cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	private Set<Staff> staffSet = new HashSet<Staff>();

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
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

	public Set<Staff> getStaffSet()
	{
		return staffSet;
	}

	public void setStaffSet(Set<Staff> staffSet)
	{
		this.staffSet = staffSet;
	}
}
