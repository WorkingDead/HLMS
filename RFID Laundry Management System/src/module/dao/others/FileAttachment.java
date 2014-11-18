package module.dao.others;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import module.dao.DaoFactory;
import module.dao.others.Attachment;

@Entity
@Table(name=DaoFactory.FileAttachment)
public class FileAttachment extends Attachment {
	
	//for display ico use
	public static enum FileType
	{
		TEXT("text.png"),
		PDF("pdf.png"),
		IMAGE("image.png"),
		OTHER("other.png")
		;
		
		String icoName;
		
		FileType(String icoName)
		{
			this.icoName = icoName;
		}

		public String getIcoName() {
			return icoName;
		}

		public void setIcoName(String icoName) {
			this.icoName = icoName;
		}
	}
	
	@Column(name="file_type")
	@Enumerated(EnumType.STRING)
	private FileType fileType;
	
	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -817095501707989946L;

}
