package module.dao.others;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import module.dao.BaseBoOthers;
import module.dao.DaoFactory;

@Entity
@Table(name=DaoFactory.AttachmentList)
public class AttachmentList extends BaseBoOthers
{

//Only one side can have JoinColumn (Handle the relationship). Otherwise, AttachmentList and Attachment cannot be transfered.
//This is because AttachmentList with id not null (Treat as Update), must have Attachment already existed.
//However, Attachment with id not null (Treat as Update), must have AttachmentList already existed also.
//	@OneToMany(cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
//	@JoinColumn(name="attachment_list_id")
//	@OrderBy("id")
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@OneToMany(mappedBy="attachmentList", cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	private Set<Attachment> attachments;

	public Set<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(Set<Attachment> attachments) {
		this.attachments = attachments;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = -1381129808863560126L;
}
