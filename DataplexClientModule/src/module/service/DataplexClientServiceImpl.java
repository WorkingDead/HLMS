package module.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import module.dao.DaoFactory;
import module.dao.access.XmlCacheDao;
import module.service.impl.BaseCRUDDaosServiceImpl;

@Service(ServiceFactory.DataplexClientService)
public class DataplexClientServiceImpl extends BaseCRUDDaosServiceImpl
		implements DataplexClientService {
	
	@Resource(name=DaoFactory.XmlCacheDao)
	public XmlCacheDao xmlCacheDao;

	@Override
	public void houseKeeping() {
		xmlCacheDao.removeAllProcessed();
	}

}
