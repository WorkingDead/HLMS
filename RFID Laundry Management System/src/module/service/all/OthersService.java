package module.service.all;

import java.util.Collection;

import module.dao.others.AttachmentBo;
import module.dao.others.ImageAttachment;
import module.dao.others.SelectionImageAttachment;
import module.dao.others.SoleImageAttachment;

import module.service.iface.BaseCRUDDaosService;

public interface OthersService extends BaseCRUDDaosService
{
	
	public <T extends AttachmentBo> void saveAttachment(T attachmentBo, Collection<Long> addList, Collection<Long> delList);
	
	public <T extends AttachmentBo> void saveSelectionImageAttachment(T attachmentBo, Collection<Long> addList, Collection<Long> delList, Long selectedImageId);
	
	public <T extends AttachmentBo> SelectionImageAttachment getSelectedAttachment(T attachmentBo);
	public <T extends AttachmentBo> ImageAttachment getImageAttachment(T attachmentBo);
	public <T extends AttachmentBo> SoleImageAttachment getSoleImageAttachment(T attachmentBo);
	
	

//	Define in BeansFactorySystem.java
//	public int getMaxThumbnailWidth();
//	public int getMaxThumbnailHeight();
//	Define in BeansFactorySystem.java
}
