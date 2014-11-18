package module.dao.others;

import org.springframework.stereotype.Repository;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;

@Repository(DaoFactory.SelectionImageAttachmentDao)
public class SelectionImageAttachmentDaoImpl extends
		BaseCRUDDaoImpl<SelectionImageAttachment, Long> implements
		SelectionImageAttachmentDao {

}
