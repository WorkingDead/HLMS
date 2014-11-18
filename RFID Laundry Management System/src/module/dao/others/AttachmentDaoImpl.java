package module.dao.others;

import org.springframework.stereotype.Repository;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;
import module.dao.others.Attachment;

@Repository(DaoFactory.AttachmentDao)
public class AttachmentDaoImpl extends BaseCRUDDaoImpl<Attachment, Long>
		implements AttachmentDao {

}
