package module.service.all;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import org.springframework.stereotype.Service;

import module.dao.DaoFactory;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.others.Attachment;
import module.dao.others.AttachmentBo;
import module.dao.others.AttachmentDao;
import module.dao.others.AttachmentList;
import module.dao.others.AttachmentListDao;
import module.dao.others.FileAttachmentDao;
import module.dao.others.ImageAttachment;
import module.dao.others.ImageAttachmentDao;
import module.dao.others.SelectionImageAttachment;
import module.dao.others.SelectionImageAttachmentDao;
import module.dao.others.SoleImageAttachment;
import module.dao.others.SoleImageAttachmentDao;

import module.service.ServiceFactory;
import module.service.impl.BaseCRUDDaosServiceImpl;

@Service(ServiceFactory.OthersService)
public class OthersServiceImpl extends BaseCRUDDaosServiceImpl implements OthersService
{
	@Resource(name=DaoFactory.AttachmentDao)
	public AttachmentDao attachmentDao;
	
	@Resource(name=DaoFactory.AttachmentListDao)
	public AttachmentListDao attachmentListDao;
	
	@Resource(name=DaoFactory.ImageAttachmentDao)
	public ImageAttachmentDao imageAttachmentDao;
	
	@Resource(name=DaoFactory.SelectionImageAttachmentDao)
	public SelectionImageAttachmentDao selectionImageAttachmentDao;
	
	@Resource(name=DaoFactory.SoleImageAttachmentDao)
	public SoleImageAttachmentDao soleImageAttachmentDao;
	
	@Resource(name=DaoFactory.FileAttachmentDao)
	public FileAttachmentDao fileAttachmentDao;
	
//	Define in BeansFactorySystem.java
//	@Value("${attachment.image.thumbnail.width}")
//	private int maxThumbnailWidth;
//	
//	@Value("${attachment.image.thumbnail.height}")
//	private int maxThumbnailHeight;
//	Define in BeansFactorySystem.java

	@Override
	public <T extends AttachmentBo> void saveAttachment(T attachmentBo,
			Collection<Long> addList, Collection<Long> delList) {
		if((addList==null ||addList.size()==0) && (delList==null || delList.size()==0))
		{
			//ignore because no update
			return;
		}
		
		if(addList!=null && delList!=null)
		{
			//clear all added but deleted
			addList.removeAll(delList);
		}
		
		
		AttachmentList attachmentList = attachmentBo.getAttachmentList();
		if(attachmentList==null)
		{
			attachmentList = new AttachmentList();
			
			//Not two side JoinColumn
			//attachmentList.setAttachments(new HashSet<Attachment>());
			//attachmentList.setId( attachmentList.generateUUIDForOthers() );	//Not in use in this project because of using back Long for id
			attachmentListDao.save(attachmentList);
			attachmentList.setAttachments(new HashSet<Attachment>());
			//Not two side JoinColumn
		}
		
		if(addList!=null && addList.size()>0)
		{
			for(Iterator<Long> it = addList.iterator();it.hasNext();)
			{
				Long id = it.next();
				Attachment attachment = this.attachmentDao.get(id);
				if(attachment==null)
				{
					throw new RuntimeException("attachment id " + id + " is not found in database!");
				}
				
				//Not two side JoinColumn
				attachment.setAttachmentList(attachmentList);
				attachmentDao.save(attachment);
				attachmentList.getAttachments().add(attachment);
				//Not two side JoinColumn
			}
		}
		
		if(delList!=null && delList.size()>0)
		{
			List<Attachment> toBeDelete = new ArrayList<Attachment>();
			for(Iterator<Long> it = delList.iterator();it.hasNext();)
			{
				Long id = it.next();
				
				for(Iterator<Attachment> it2 = attachmentList.getAttachments().iterator(); it2.hasNext();)
				{
					Attachment attachment = it2.next();
					if(attachment.getId().equals(id))
					{
						toBeDelete.add(attachment);
						
//						System.out.println("delete id= " + id);
						break;
					}
				}
			}
			
			//Not two side JoinColumn
			for(Iterator<Attachment> it = toBeDelete.iterator();it.hasNext();) {
				Attachment attachment = it.next();
				attachment.setAttachmentList(null);
				attachmentDao.save(attachment);
			}
			
			attachmentList.getAttachments().removeAll(toBeDelete);
			//Not two side JoinColumn
			
			attachmentDao.deleteList(toBeDelete);
		}

		//Not two side JoinColumn
		//attachmentListDao.save(attachmentList);
		//Not two side JoinColumn

		attachmentBo.setAttachmentList(attachmentList);
	}

	@Override
	public <T extends AttachmentBo> void saveSelectionImageAttachment(
			T attachmentBo, Collection<Long> addList, Collection<Long> delList,
			Long selectedImageId) {
		
//		System.out.println("Here is saveSelectionImageAttachment before ... ");
		
		this.saveAttachment(attachmentBo, addList, delList);
		
//		System.out.println("Here is saveSelectionImageAttachment after ... ");
		
		if(attachmentBo.getAttachmentList()==null || attachmentBo.getAttachmentList().getAttachments()==null)
		{
//			if ( attachmentBo.getAttachmentList() == null ) 
//				System.out.println("Here is getAttachmentList null ... ");
//			if ( attachmentBo.getAttachmentList().getAttachments() == null )
//				System.out.println("Here is getAttachmentList getAttachments null ... ");
			
			//ignore when no attachments
			return; 
		}
		
		boolean found = false;
		SelectionImageAttachment selectedAttachment = null;
//		System.out.println("attachmentBo.getAttachmentList().getAttachments() size = " + attachmentBo.getAttachmentList().getAttachments().size());
		for(Iterator<Attachment> it = attachmentBo.getAttachmentList().getAttachments().iterator();it.hasNext();)
		{
			Attachment atta = it.next();
			
			if(!(atta instanceof SelectionImageAttachment))
			{
				throw new RuntimeException("AttachmentList contain some attachment not a SelectionImageAttachment!!");
			}
			
			selectedAttachment = ((SelectionImageAttachment) atta);
			
			if(selectedImageId!=null)
			{
//				System.out.println("atta id = " + atta.getId());
//				System.out.println("selectedImageId = " + selectedImageId);
				if(atta.getId().equals(selectedImageId))
				{
					found = true;
					selectedAttachment.setSelected(true);
				}
				else
				{
					selectedAttachment.setSelected(false);
				}
				this.save(Attachment.class, atta);
			}
			else
			{
				selectedAttachment.setSelected(true);
				this.save(Attachment.class, atta);
				found = true;
				break;
			}
		}
		
		if(!found)
		{
			throw new RuntimeException("selected image is not found!! selectedImageId = " + selectedImageId);
		}
		
	}

	@Override
	public <T extends AttachmentBo> SelectionImageAttachment getSelectedAttachment(
			T attachmentBo) {

		SelectionImageAttachment attachment = null;
		
		if(attachmentBo.getAttachmentList()!=null){
			final Long id = attachmentBo.getAttachmentList().getId();
			
			List<SelectionImageAttachment> list = this.findByExample(SelectionImageAttachment.class, null, null, null
				, new CustomCriteriaHandler<SelectionImageAttachment>() {

					@Override
					public void makeCustomCriteria(Criteria baseCriteria) {
						baseCriteria.add(Restrictions.eq("attachmentList.id", id));
						baseCriteria.add(Restrictions.eq("selected", true));
					}
				
				}
				, null
			, null);
			
			if(list.size()>1)
			{
				throw new RuntimeException("more that one image has selected!");
			}
			
			if(list.size()==0)
			{
				attachment = new SelectionImageAttachment();
				attachment.setId(null);//show no image photo
			}
			else
			{
				attachment = list.get(0);
			}
		}
		else
		{
			attachment = new SelectionImageAttachment();
			attachment.setId(null);//show no image photo
		}
		
		return attachment;
	}

	@Override
	public <T extends AttachmentBo> ImageAttachment getImageAttachment(
			T attachmentBo) {

		ImageAttachment attachment = null;
		
		if(attachmentBo.getAttachmentList()!=null){
			final Long id = attachmentBo.getAttachmentList().getId();
			
			List<ImageAttachment> list = this.findByExample(ImageAttachment.class, null, null, null
				, new CustomCriteriaHandler<ImageAttachment>() {

					@Override
					public void makeCustomCriteria(Criteria baseCriteria) {
						baseCriteria.add(Restrictions.eq("attachmentList.id", id));
					}
				
				}
				, null
			, null);

			if(list.size()==0)
			{
				attachment = new ImageAttachment();
				attachment.setId(null);//show no image photo
			}
			else
			{
				attachment = list.get(0);
			}
		}
		else
		{
			attachment = new ImageAttachment();
			attachment.setId(null);//show no image photo
		}
		
		return attachment;
	}

	@Override
	public <T extends AttachmentBo> SoleImageAttachment getSoleImageAttachment(
			T attachmentBo) {

		SoleImageAttachment attachment = null;
		
		if(attachmentBo.getAttachmentList()!=null){
			final Long id = attachmentBo.getAttachmentList().getId();
			
			List<SoleImageAttachment> list = this.findByExample(SoleImageAttachment.class, null, null, null
				, new CustomCriteriaHandler<SoleImageAttachment>() {

					@Override
					public void makeCustomCriteria(Criteria baseCriteria) {
						baseCriteria.add(Restrictions.eq("attachmentList.id", id));
					}
				
				}
				, null
			, null);
			
			if(list.size()>1)
			{
				throw new RuntimeException("more that one image!");
			}
			
			if(list.size()==0)
			{
				attachment = new SoleImageAttachment();
				attachment.setId(null);//show no image photo
			}
			else
			{
				attachment = list.get(0);
			}
		}
		else
		{
			attachment = new SoleImageAttachment();
			attachment.setId(null);//show no image photo
		}
		
		return attachment;
	}

//	public int getMaxThumbnailWidth() {
//		return maxThumbnailWidth;
//	}
//
//	public void setMaxThumbnailWidth(int maxThumbnailWidth) {
//		this.maxThumbnailWidth = maxThumbnailWidth;
//	}
//
//	public int getMaxThumbnailHeight() {
//		return maxThumbnailHeight;
//	}
//
//	public void setMaxThumbnailHeight(int maxThumbnailHeight) {
//		this.maxThumbnailHeight = maxThumbnailHeight;
//	}

}
