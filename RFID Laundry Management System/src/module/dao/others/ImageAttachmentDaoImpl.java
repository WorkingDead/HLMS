package module.dao.others;

import org.springframework.stereotype.Repository;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;

@Repository(DaoFactory.ImageAttachmentDao)
public class ImageAttachmentDaoImpl extends
		BaseCRUDDaoImpl<ImageAttachment, Long> implements ImageAttachmentDao {

}
