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
@Table(name=DaoFactory.ZONE)
@org.hibernate.annotations.Table(appliesTo=DaoFactory.ZONE,
indexes={
	@Index(name="ix_000", columnNames={"creation_date", "last_modify_date", "version", "created_by", "modified_by", "enable", "code", "description"}),
	@Index(name="ix_010", columnNames={"last_modify_date", "version", "created_by", "modified_by", "enable", "code", "description"}),
	@Index(name="ix_020", columnNames={"version", "created_by", "modified_by", "enable", "code", "description"}),
	@Index(name="ix_030", columnNames={"created_by", "modified_by", "enable", "code", "description"}),
	@Index(name="ix_040", columnNames={"modified_by", "enable", "code", "description"}),
	@Index(name="ix_050", columnNames={"enable", "code", "description"}),
	@Index(name="ix_060", columnNames={"code", "description"}),
	@Index(name="ix_070", columnNames={"description"}),
	} )
public class Zone extends BaseBoMaster
{
	private static final long serialVersionUID = -7887261540451916041L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="code", length=50, unique=true, nullable=false)
	private String code;
	
	@Column(name="description", length=1000)
	private String description;

	@OneToMany(mappedBy="zone", cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	private Set<Cloth> clothSet = new HashSet<Cloth>();



	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}

	//The validator must be placed before the get and set method. Get and set method must be placed together
//	@RequiredStringValidator( key = BaseAction.ErrorMessage_Required )				
//	@StringLengthFieldValidator( minLength = "1", maxLength = "50", key = BaseAction.ErrorMessage_StringLength )
	public String getCode()
	{
		return code;
	}
	public void setCode(String code)
	{
		this.code = trimString( code );
	}

	// @StringLengthFieldValidator( minLength = "1", maxLength = "3", key = BaseAction.ErrorMessage_StringLength )
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = trimString( description );
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
