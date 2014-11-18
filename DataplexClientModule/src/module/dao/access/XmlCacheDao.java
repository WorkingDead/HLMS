package module.dao.access;

import module.dao.XmlCache;
import module.dao.iface.BaseCRUDDao;

public interface XmlCacheDao extends BaseCRUDDao<XmlCache, Long> {
	
	public void removeAllProcessed();

}
