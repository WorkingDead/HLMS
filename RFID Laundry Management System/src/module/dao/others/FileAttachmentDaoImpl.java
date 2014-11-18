package module.dao.others;

import org.springframework.stereotype.Repository;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;

@Repository(DaoFactory.FileAttachmentDao)
public class FileAttachmentDaoImpl extends
		BaseCRUDDaoImpl<FileAttachment, Long> implements FileAttachmentDao {

}
