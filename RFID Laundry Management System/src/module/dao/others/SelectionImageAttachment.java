package module.dao.others;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import module.dao.DaoFactory;

@Entity
@Table(name=DaoFactory.SelectionImageAttachment)
public class SelectionImageAttachment extends ImageAttachment {
	
	@Column(name="selected")
	private boolean selected;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8968514169077122574L;

}
