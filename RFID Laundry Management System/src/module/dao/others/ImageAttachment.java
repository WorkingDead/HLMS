package module.dao.others;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;

import module.dao.DaoFactory;
import module.dao.others.Attachment;

@Entity
@Table(name=DaoFactory.ImageAttachment)
@Inheritance(strategy=InheritanceType.JOINED)
public class ImageAttachment extends Attachment {
	
	public boolean checkFileFormat()
	{
		//TODO check file format
		
		return true;
	}
	
	@Column(name="thumbnail_file_bytes")
	@Lob
	@Basic(fetch = FetchType.LAZY) 
	private byte[] thumbnailFileBytes;
	
	@Column(name="image_width")
	private Integer imageWidth;
	
	@Column(name="image_height")
	private Integer imageHeight;
	
	@Column(name="thumbnail_width")
	private Integer thumbnailWidth;
	
	@Column(name="thumbnail_height")
	private Integer thumbnailHeight;
	
	@Column(name="thumbnail_file_size")
	private Integer thumbnailFileSize;

	public byte[] getThumbnailFileBytes() {
		return thumbnailFileBytes;
	}

	public void setThumbnailFileBytes(byte[] thumbnailFileBytes) {
		this.thumbnailFileBytes = thumbnailFileBytes;
	}

	public Integer getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(Integer imageWidth) {
		this.imageWidth = imageWidth;
	}

	public Integer getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(Integer imageHeight) {
		this.imageHeight = imageHeight;
	}

	public Integer getThumbnailWidth() {
		return thumbnailWidth;
	}

	public void setThumbnailWidth(Integer thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
	}

	public Integer getThumbnailHeight() {
		return thumbnailHeight;
	}

	public void setThumbnailHeight(Integer thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
	}

	public Integer getThumbnailFileSize() {
		return thumbnailFileSize;
	}

	public void setThumbnailFileSize(Integer thumbnailFileSize) {
		this.thumbnailFileSize = thumbnailFileSize;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1820066398332155970L;

}
