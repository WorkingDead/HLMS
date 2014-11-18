package module.dao.iface;

import org.hibernate.Criteria;

import module.dao.BaseBo;

/*
 * This class use for making custom criteria in a query instance (base on Hibernate API) (TODO make hibernate dependency lower)
 * @author by kan
 */
public abstract class CustomCriteriaHandler<T extends BaseBo> {

	/*
	 * just call baseCriteria.add to add extra criteria
	 */
	public abstract void makeCustomCriteria(Criteria baseCriteria);
	
}
