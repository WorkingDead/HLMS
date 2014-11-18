package module.dao.others;

import org.springframework.stereotype.Repository;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;
import module.dao.others.AttachmentList;

@Repository(DaoFactory.AttachmentListDao)
public class AttachmentListDaoImpl extends
		BaseCRUDDaoImpl<AttachmentList, Long> implements AttachmentListDao {

}
