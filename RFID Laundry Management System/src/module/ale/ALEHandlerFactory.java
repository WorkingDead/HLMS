package module.ale;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import module.ale.ALEHandler;
import module.ale.handler.IroningDeliveryHandler;
import module.ale.handler.Kiosk1ClothCollectionHandler;
import module.ale.handler.Kiosk2ClothCollectionHandler;
import module.ale.handler.Kiosk1ClothDistributeHandler;
import module.ale.handler.Kiosk2ClothDistributeHandler;

import org.springframework.stereotype.Component;

@Component(ALEHandlerFactory.BEANNAME)
public class ALEHandlerFactory
{
	public static final String BEANNAME = "ALEHandlerFactory";

	@Resource(name=ALEHandler.BEANNAME)
	public ALEHandler aleHandler;
	
	@Resource(name=IroningDeliveryHandler.BEANNAME)
	public IroningDeliveryHandler ironingDeliveryHandler;
	
	
	//////////////////////////////////////////
	// Kiosk
	//////////////////////////////////////////
	@Resource(name=Kiosk1ClothCollectionHandler.BEANNAME)
	public Kiosk1ClothCollectionHandler kiosk1ClothCollectionHandler;
	
	@Resource(name=Kiosk2ClothCollectionHandler.BEANNAME)
	public Kiosk2ClothCollectionHandler kiosk2ClothCollectionHandler;
	
	@Resource(name=Kiosk1ClothDistributeHandler.BEANNAME)
	public Kiosk1ClothDistributeHandler kiosk1ClothDistributeHandler;
	
	@Resource(name=Kiosk2ClothDistributeHandler.BEANNAME)
	public Kiosk2ClothDistributeHandler kiosk2ClothDistributeHandler;
	
	@PostConstruct
	public void init()
	{
		aleHandler.addECSpecHandler(IroningDeliveryHandler.SPECNAME, ironingDeliveryHandler);
		
		aleHandler.addECSpecHandler(Kiosk1ClothCollectionHandler.SPECNAME, kiosk1ClothCollectionHandler);
		aleHandler.addECSpecHandler(Kiosk2ClothCollectionHandler.SPECNAME, kiosk2ClothCollectionHandler);
		aleHandler.addECSpecHandler(Kiosk1ClothDistributeHandler.SPECNAME, kiosk1ClothDistributeHandler);
		aleHandler.addECSpecHandler(Kiosk2ClothDistributeHandler.SPECNAME, kiosk2ClothDistributeHandler);
	}
}
