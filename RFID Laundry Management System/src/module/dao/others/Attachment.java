package module.dao.others;

import java.io.File;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import module.dao.BaseBoOthers;
import module.dao.DaoFactory;

@Entity
@Table(name=DaoFactory.Attachment)
@Inheritance(strategy=InheritanceType.JOINED)
@org.hibernate.annotations.Table(appliesTo=DaoFactory.Attachment,
indexes={
	@Index(name="ix_01", columnNames={"filename", "content_type"}),
	@Index(name="ix_02", columnNames={"content_type", "filename"}),
	@Index(name="ix_03", columnNames={"creation_date"}),
	@Index(name="ix_04", columnNames={"last_modify_date"}),
	} )
public class Attachment extends BaseBoOthers
{
	
	public boolean checkFileFormat()
	{
		return true;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="filename")
	private String filename;
	
	@Column(name="content_type")
	private String contentType;
	
	//@Column(name="file_bytes") //oracle use this
	//@Column(name="file_bytes", columnDefinition="LONGBLOB") //mysql use this
	@Column(name="file_bytes")	//Using this should be enough on mysql and ms sql server	//By Wing
	@Lob
	@Basic(fetch = FetchType.LAZY) 
	private byte[] fileBytes;
	
	@Column(name="file_size")
	private Integer fileSize;
	
	@ManyToOne(cascade=CascadeType.DETACH, fetch=FetchType.LAZY)
	@JoinColumn(name="attachment_list_id")
	@ForeignKey(name="fk_attachment_attachment_list_id")
	private AttachmentList attachmentList;
	
	//receive file from struts2
	@Transient
	private File upload;
	
	@Transient
	private String uploadFileName;
	
	@Transient
	private String uploadContentType;
	//end receive file from struts2

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getFileBytes() {
		return fileBytes;
	}

	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public AttachmentList getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(AttachmentList attachmentList) {
		this.attachmentList = attachmentList;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
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
	private static final long serialVersionUID = 2766105861287251188L;

}
