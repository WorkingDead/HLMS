package web.actions.others;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import utils.convertor.image.ImageConvertor;

import web.actions.BaseActionOthers;

import module.dao.iface.CustomLazyHandler;
import module.dao.others.Attachment;
import module.dao.others.AttachmentList;
import module.dao.others.FileAttachment;
import module.dao.others.ImageAttachment;
import module.dao.others.SelectionImageAttachment;
import module.dao.others.SoleImageAttachment;

@Results({
	@Result(name="uploadAttachmentJson", type="json", params={
			"includeProperties",	"^success, " + 
									"^attachmentId",
			"contentType", "text/plain" //fixed IE will download json as file, but IE will add <pre></pre>.
	}),
	@Result(name="attachmentReturn", type="stream", params={
			"contentType" , "${attachment.contentType}", 
			"inputName" , "attachmentInputStream", 
			"contentLength" , "${attachment.fileSize}",  
			"contentDisposition" , "filename=\"${attachment.filename}\"",  
			"bufferSize" , "1024"
	}),
	@Result(name="imageThumbnailReturn", type="stream", params={
			"contentType" , "image/jpeg", 
			"inputName" , "attachmentInputStream", 
			"contentLength" , "${imageAttachment.thumbnailFileSize}",  
			"contentDisposition" , "filename=\"${attachment.filename}\"",  
			"bufferSize" , "1024"
	}),
	@Result(name="noImageReturn", type="stream", params={
			"contentType" , "image/jpeg", 
			"inputName" , "noImageInputStream", 
			"contentLength" , "${noImageFileSize}",  
			"bufferSize" , "1024"
	}),
	@Result(name="attachmentFileInput", type="tiles", location="others.attachment.fileinput"),
	@Result(name="attachmentImageInput", type="tiles", location="others.attachment.imageinput"),
	@Result(name="attachmentSelectionImageInput", type="tiles", location="others.attachment.selectionimageinput"),
	@Result(name="attachmentSoleImageInput", type="tiles", location="others.attachment.soleimageinput"),
	
	@Result(name="attachmentFileReadonly", type="tiles", location="others.attachment.fileinput.readonly"),
	@Result(name="attachmentImageReadonly", type="tiles", location="others.attachment.imageinput.readonly"),
	@Result(name="attachmentSelectionImageReadonly", type="tiles", location="others.attachment.selectionimageinput.readonly"),
	@Result(name="attachmentSoleImageReadonly", type="tiles", location="others.attachment.soleimageinput.readonly"),
	
})
//The sequence of interceptor is extremely important, DO NOT modify it!!!
@InterceptorRefs({
	@InterceptorRef("prefixStack"),													//Must be called
	@InterceptorRef(value="validation",params={"includeMethods","uploadAttachment"}),
	@InterceptorRef("postStack")													//Must be called
})
@ParentPackage("struts-action-default")
public class AttachmentAction extends BaseActionOthers
{
	private static final long serialVersionUID = -6982956408717845087L;
	
	private static final Logger log4j = Logger.getLogger(AttachmentAction.class);
	
	private static final String ErrorMessage_FileNotFound = "attachment.error.notfound";
	private static final String ErrorMessage_WrongFileFormat = "attachment.error.wrongformat";
	private static final String ErrorMessage_FileUploadError = "attachment.error.uploaderror";
	
	public static enum AttachmentType
	{
		Image,
		SelectionImage,
		SoleImage,
		File
	}
	
	private AttachmentType attachmentType;
	
	private ImageAttachment imageAttachment;
	
	private SelectionImageAttachment selectionImageAttachment;
	
	private SoleImageAttachment soleImageAttachment;
	
	private FileAttachment fileAttachment;
	
	private boolean success;
	private Long attachmentId;
	
	//return attachment when get
	private Attachment attachment;
	private InputStream attachmentInputStream;
	private boolean thumbnail = false;
	private Integer noImageFileSize;
	private InputStream noImageInputStream;
	
	//edit
	private Long attachmentListId;
	private AttachmentList attachmentList;



	public String uploadAttachment()
	{
		success = false;
		
		Attachment attachment = null;
		switch(this.attachmentType)
		{
		case File:
			attachment = this.fileAttachment;
			break;
		case Image:
			attachment = this.imageAttachment;
			break;
		case SelectionImage:
			attachment = this.selectionImageAttachment;
			break;
		case SoleImage:
			attachment = this.soleImageAttachment;
			break;
		}
		
		File file = attachment.getUpload();
		byte[] bytes = null;
		byte[] thumbnailBytes = null;
		
		try{
			
			//open file stream
			InputStream is = new FileInputStream(file);
		    
	        // Get the size of the file
	        long length = file.length();
	        
	        if (length > Integer.MAX_VALUE) {
	        	throw new IOException("File is too large!");
	        }
	        
	        // Create the byte array to hold the data
	        bytes = new byte[(int)length];
	        
	        // Read in the bytes
	        int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length
	               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	    
	        // Ensure all the bytes have been read in
	        if (offset < bytes.length) {
	            throw new IOException("Could not completely read file "+file.getName());
	        }
	    
	        // Close the input stream and return bytes
	        is.close();
		
		}catch (Exception e) {
			
			log4j.error("cannot save file", e);
			addActionError(getText(ErrorMessage_FileUploadError, e.getMessage()));
			
		}
		
		attachment.setFileBytes(bytes);
		attachment.setFileSize(bytes.length);
		
		//generate thumbnail
        if(attachment instanceof ImageAttachment)
        {
        	ImageAttachment imga = (ImageAttachment) attachment;
        	try {
				BufferedImage oriimage = javax.imageio.ImageIO.read(file);
				imga.setImageWidth(oriimage.getWidth());
				imga.setImageHeight(oriimage.getHeight());
				
			} catch (IOException e1) {}
        	
        	try {
				BufferedImage image = ImageConvertor.createImageThumbnail(file, getBeansFactorySystem().getMaxThumbnailWidth(), getBeansFactorySystem().getMaxThumbnailHeight());
				thumbnailBytes = ImageConvertor.getImageBytes(image, "JPG");
				imga.setThumbnailWidth(image.getWidth());
				imga.setThumbnailHeight(image.getHeight());
				imga.setThumbnailFileBytes(thumbnailBytes);
				imga.setThumbnailFileSize(thumbnailBytes.length);
				image = null;
			} catch (IOException e) {}
        }
		
		attachment.setFilename(attachment.getUploadFileName());//optional can generate by time stamp
		attachment.setContentType(attachment.getUploadContentType());//TODO check by self
		
		try {
			
			switch(this.attachmentType)
			{
			case File:
				//this.fileAttachment.setId( this.fileAttachment.generateUUIDForOthers() );		//Not in use in this project because of using back Long for id
				getOthersService().save(FileAttachment.class, this.fileAttachment);
				this.attachmentId = this.fileAttachment.getId();
				break;
			case Image:
				//this.imageAttachment.setId( this.imageAttachment.generateUUIDForOthers() );	//Not in use in this project because of using back Long for id
				getOthersService().save(ImageAttachment.class, this.imageAttachment);
				this.attachmentId = this.imageAttachment.getId();
				break;
			case SelectionImage:
				this.selectionImageAttachment.setSelected(false);
				//this.selectionImageAttachment.setId( this.selectionImageAttachment.generateUUIDForOthers() );		//Not in use in this project because of using back Long for id
				getOthersService().save(SelectionImageAttachment.class, this.selectionImageAttachment);
				this.attachmentId = this.selectionImageAttachment.getId();
				break;
			case SoleImage:
				getOthersService().save(SoleImageAttachment.class, this.soleImageAttachment);
				this.attachmentId = this.soleImageAttachment.getId();
				break;
			}
			
			addActionMessage(getText(SuccessMessage_SaveSuccess));
			
			success = true;
			
			
		} catch (Exception e) {
			log4j.error("cannot save file", e);
			addActionError(getText(ErrorMessage_FileUploadError, e.getMessage()));
		}
		
		return "uploadAttachmentJson";
	}
	
	public String attachmentInput()
	{
		if(this.attachmentListId!=null && !attachmentListId.equals(-1L) )
		{
			this.attachmentList = getOthersService().get(AttachmentList.class, this.attachmentListId, new CustomLazyHandler<AttachmentList>() {

				@Override
				public void LazyObject(AttachmentList obj) {
					if(obj!=null && obj.getAttachments()!=null)
						obj.getAttachments().size();
				}
				
			});
		}
		switch(this.attachmentType)
		{
			case File:
				
				return "attachmentFileInput";
			case Image:
				
				return "attachmentImageInput";
			case SelectionImage:
				
				return "attachmentSelectionImageInput";
			case SoleImage:

				return "attachmentSoleImageInput";
		}
		return null;
	}
	
	public String attachmentReadonly()
	{
		if(this.attachmentListId!=null && !attachmentListId.equals(-1L) )
		{
			this.attachmentList = getOthersService().get(AttachmentList.class, this.attachmentListId, new CustomLazyHandler<AttachmentList>() {

				@Override
				public void LazyObject(AttachmentList obj) {
					if(obj!=null && obj.getAttachments()!=null)
						obj.getAttachments().size();
				}
				
			});
		}
		switch(this.attachmentType)
		{
			case File:
				
				return "attachmentFileReadonly";
			case Image:
				
				return "attachmentImageReadonly";
			case SelectionImage:
				
				return "attachmentSelectionImageReadonly";
			case SoleImage:
				
				return "attachmentSoleImageReadonly";
		}
		return null;
	}

	//get attachment ( Kiosk Use Only )
	public String showKioskAttachment() {
		return showAttachment();
	}
	
	//get attachment
	public String showAttachment()
	{
		getServletResponse().setHeader("Pragma", "No-cache");
		getServletResponse().setHeader("Cache-Control", "no-cache");
		getServletResponse().setDateHeader("Expires", 0L);
		
		//if( this.attachmentId == null || this.attachmentId.length() <= 0 )	//Not in use in this project because of using back Long for id
		if( this.attachmentId == null )
		{
			String noImagePath = getServletContext().getRealPath("/images/attachment/noImageAvailable.jpg");
			File file = new File(noImagePath);
			if(!file.exists())
			{
				return null;
			}
			
			
			try {
				
				this.noImageFileSize = (int) file.length();
				this.noImageInputStream = new FileInputStream(file);

				return "noImageReturn";
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		else
		{
			if(thumbnail)
			{
				this.imageAttachment = getOthersService().get(ImageAttachment.class, this.attachmentId, new CustomLazyHandler<ImageAttachment>() {
					
					@Override
					public void LazyObject(ImageAttachment obj) {
						obj.getThumbnailFileBytes();
					}
					
				});
				
				this.attachmentInputStream = new ByteArrayInputStream(imageAttachment.getThumbnailFileBytes());
				return "imageThumbnailReturn";
			}
			else
			{
				this.attachment = getOthersService().get(Attachment.class, this.attachmentId, new CustomLazyHandler<Attachment>() {
		
					@Override
					public void LazyObject(Attachment obj) {
						obj.getFileBytes();
					}
					
				});
				
				this.attachmentInputStream = new ByteArrayInputStream(this.attachment.getFileBytes());
				return "attachmentReturn";
			}
		}
	}
	
	public void validateUploadAttachment()
	{
		if(this.attachmentType==null)
		{
			addFieldError("upload", getText(ErrorMessage_OperationError));
		}
		else
		{
			switch(this.attachmentType)
			{
				case File:
					if(this.fileAttachment==null)
					{
						addFieldError("upload", getText(ErrorMessage_OperationError));
					}
					else
					{
						if(this.fileAttachment.getUpload()==null)
						{
							addFieldError("upload", getText(ErrorMessage_FileNotFound));
						}
					}
					break;
				case Image:
					if(this.imageAttachment==null)
					{
						addFieldError("upload", getText(ErrorMessage_OperationError));
					}
					else
					{
						if(this.imageAttachment.getUpload()==null)
						{
							addFieldError("upload", getText(ErrorMessage_FileNotFound));
						}
					}
					break;
				case SelectionImage:
					if(this.selectionImageAttachment==null)
					{
						addFieldError("upload", getText(ErrorMessage_OperationError));
					}
					else
					{
						if(this.selectionImageAttachment.getUpload()==null)
						{
							addFieldError("upload", getText(ErrorMessage_FileNotFound));
						}
					}
					break;
				case SoleImage:
					if(this.soleImageAttachment==null)
					{
						addFieldError("upload", getText(ErrorMessage_OperationError));
					}
					else
					{
						if(this.soleImageAttachment.getUpload()==null)
						{
							addFieldError("upload", getText(ErrorMessage_FileNotFound));
						}
					}
					break;
			}
			
			Attachment attachment = null;
			switch(this.attachmentType)
			{
				case File:
					attachment = this.fileAttachment;
					break;
				case Image:
					attachment = this.imageAttachment;
					break;
				case SelectionImage:
					attachment = this.selectionImageAttachment;
					break;
				case SoleImage:
					attachment = this.soleImageAttachment;
					break;
			}
			
			if(attachment!=null)
			{
				if(!attachment.checkFileFormat())
				{
					addFieldError("upload", getText(ErrorMessage_WrongFileFormat));
				}
			}
		}
	}



	public AttachmentType getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(AttachmentType attachmentType) {
		this.attachmentType = attachmentType;
	}

	public ImageAttachment getImageAttachment() {
		return imageAttachment;
	}

	public void setImageAttachment(ImageAttachment imageAttachment) {
		this.imageAttachment = imageAttachment;
	}

	public SelectionImageAttachment getSelectionImageAttachment() {
		return selectionImageAttachment;
	}

	public void setSelectionImageAttachment(
			SelectionImageAttachment selectionImageAttachment) {
		this.selectionImageAttachment = selectionImageAttachment;
	}

	public SoleImageAttachment getSoleImageAttachment() {
		return soleImageAttachment;
	}

	public void setSoleImageAttachment(SoleImageAttachment soleImageAttachment) {
		this.soleImageAttachment = soleImageAttachment;
	}

	public FileAttachment getFileAttachment() {
		return fileAttachment;
	}

	public void setFileAttachment(FileAttachment fileAttachment) {
		this.fileAttachment = fileAttachment;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Attachment getAttachment() {
		return attachment;
	}

	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}

	public InputStream getAttachmentInputStream() {
		return attachmentInputStream;
	}

	public void setAttachmentInputStream(InputStream attachmentInputStream) {
		this.attachmentInputStream = attachmentInputStream;
	}

	public Long getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public Long getAttachmentListId() {
		return attachmentListId;
	}

	public void setAttachmentListId(Long attachmentListId) {
		this.attachmentListId = attachmentListId;
	}



//Don't know why it can not use 'override' for OGNL (OGNL would use the method in BaseAction) when the namespace and action is pointed to here correctly.
//Therefore, BaseAction cannot have 'setAttachmentListId' method.
//Otherwise, OGNL would use 'setAttachmentListId' method in BaseAction instead of in here.
	public void setAttachmentListId(String attachmentListId) {

		if ( attachmentListId == null || attachmentListId.equals("null") || attachmentListId.equals("NULL") || attachmentListId.isEmpty() )
			this.attachmentListId = null;
		else {
			try {
				setAttachmentListId( Long.parseLong( attachmentListId ) );
			}
			catch (Exception e) {
				
				log4j.error("setAttachmentListId: ", e);
				
				this.attachmentListId = null;
			}
		}
	}
	
	public void setAttachmentListId(String[] attachmentListId) {
		
		setAttachmentListId(attachmentListId[0]);
	}



	public AttachmentList getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(AttachmentList attachmentList) {
		this.attachmentList = attachmentList;
	}

	public boolean isThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(boolean thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Integer getNoImageFileSize() {
		return noImageFileSize;
	}

	public void setNoImageFileSize(Integer noImageFileSize) {
		this.noImageFileSize = noImageFileSize;
	}

	public InputStream getNoImageInputStream() {
		return noImageInputStream;
	}

	public void setNoImageInputStream(InputStream noImageInputStream) {
		this.noImageInputStream = noImageInputStream;
	}
}
