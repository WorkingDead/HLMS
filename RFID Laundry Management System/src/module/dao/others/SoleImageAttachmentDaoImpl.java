package module.dao.others;

import org.springframework.stereotype.Repository;

import module.dao.DaoFactory;
import module.dao.impl.BaseCRUDDaoImpl;

@Repository(DaoFactory.SoleImageAttachmentDao)
public class SoleImageAttachmentDaoImpl extends BaseCRUDDaoImpl<SoleImageAttachment, Long> implements SoleImageAttachmentDao {

}
