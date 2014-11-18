package module.service;

import module.service.iface.BaseCRUDDaosService;

public interface DataplexClientService extends BaseCRUDDaosService {

	public void houseKeeping();
	
}
