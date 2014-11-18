package module.dao.others;

import javax.persistence.Entity;
import javax.persistence.Table;

import module.dao.DaoFactory;

@Entity
@Table(name=DaoFactory.SoleImageAttachment)
public class SoleImageAttachment extends ImageAttachment {

	private static final long serialVersionUID = -6299538920799074108L;
}
