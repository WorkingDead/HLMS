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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import module.dao.BaseBoMaster;
import module.dao.DaoFactory;
import module.dao.general.SpecialEvent;
import module.dao.others.AttachmentBo;
import module.dao.others.AttachmentList;
import module.dao.others.ImageAttachment;
import module.dao.others.SelectionImageAttachment;
import module.dao.others.SoleImageAttachment;

@Entity
@Table(name=DaoFactory.CLOTH_TYPE)
@org.hibernate.annotations.Table(appliesTo=DaoFactory.CLOTH_TYPE,
indexes={
	@Index(name="ix_000", columnNames={"creation_date", "last_modify_date", "version", "created_by", "modified_by", "enable", "name", "description"}),
	@Index(name="ix_010", columnNames={"last_modify_date", "version", "created_by", "modified_by", "enable", "name", "description"}),
	@Index(name="ix_020", columnNames={"version", "created_by", "modified_by", "enable", "name", "description"}),
	@Index(name="ix_030", columnNames={"created_by", "modified_by", "enable", "name", "description"}),
	@Index(name="ix_040", columnNames={"modified_by", "enable", "name", "description"}),
	@Index(name="ix_050", columnNames={"enable", "name", "description"}),
	@Index(name="ix_060", columnNames={"name", "description"}),
	@Index(name="ix_070", columnNames={"description"}),
	} )
public class ClothType extends BaseBoMaster implements AttachmentBo
{
	private static final long serialVersionUID = 3150042254481626484L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(name="name", length=100, unique=true, nullable=false)
	private String name;	// name of cloth type
	
	@Column(name="description", length=1000)
	private String description;
	
	@OneToMany(mappedBy="clothType", cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	private Set<Cloth> clothSet = new HashSet<Cloth>();

	// for Special Event (added by Horace, plz delete this comment if Wing saw this)
	@OneToMany(mappedBy="clothType", cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	private Set<SpecialEvent> specialEventSet;
	
	//attachment use
	@OneToOne(cascade=CascadeType.DETACH, fetch=FetchType.LAZY, optional=true)
	@JoinColumn(name="attachment_list_id")
	@ForeignKey(name="fk_c_t_attachment_list_id")
	private AttachmentList attachmentList;
	
	//for show attachment
	@Transient
	private SelectionImageAttachment selectionImageAttachment;
	
	@Transient
	private ImageAttachment imageAttachment;
	
	@Transient
	private SoleImageAttachment soleImageAttachment;
	//attachment use

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
//	@StringLengthFieldValidator( minLength = "1", maxLength = "100", key = BaseAction.ErrorMessage_StringLength )
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = trimString( name );
	}
	// @StringLengthFieldValidator( minLength = "1", maxLength = "1000", key = BaseAction.ErrorMessage_StringLength )
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
	public AttachmentList getAttachmentList()
	{
		return attachmentList;
	}
	public void setAttachmentList(AttachmentList attachmentList)
	{
		this.attachmentList = attachmentList;
	}
	public SelectionImageAttachment getSelectionImageAttachment()
	{
		return selectionImageAttachment;
	}
	public void setSelectionImageAttachment(
			SelectionImageAttachment selectionImageAttachment)
	{
		this.selectionImageAttachment = selectionImageAttachment;
	}
	public ImageAttachment getImageAttachment()
	{
		return imageAttachment;
	}
	public void setImageAttachment(ImageAttachment imageAttachment)
	{
		this.imageAttachment = imageAttachment;
	}
	public SoleImageAttachment getSoleImageAttachment()
	{
		return soleImageAttachment;
	}
	public void setSoleImageAttachment(SoleImageAttachment soleImageAttachment)
	{
		this.soleImageAttachment = soleImageAttachment;
	}
	public Set<SpecialEvent> getSpecialEventSet()
	{
		return specialEventSet;
	}
	public void setSpecialEventSet(Set<SpecialEvent> specialEventSet)
	{
		this.specialEventSet = specialEventSet;
	}
}

