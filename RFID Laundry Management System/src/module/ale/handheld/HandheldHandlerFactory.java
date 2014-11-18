package module.ale.handheld;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import module.ale.handheld.event.HandheldEvent;
import module.ale.handheld.handler.ClothCollectionHandler;
import module.ale.handheld.handler.ClothDistributeHandler;
import module.ale.handheld.handler.ClothAssociationHandler;
import module.ale.handheld.handler.ClothRackingHandler;
import module.ale.handheld.handler.IroningDeliveryHandler;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component(HandheldHandlerFactory.BEANNAME)
public class HandheldHandlerFactory {

	private static final Logger log4j = Logger.getLogger(HandheldHandlerFactory.class);
	
	public static final String BEANNAME = "HandheldHandlerFactory";

	private Map<String, HandheldHandler> handlerMap = new HashMap<String, HandheldHandler>();
	
	@Resource(name=ClothCollectionHandler.BEANNAME)
	public ClothCollectionHandler clothCollectionHandler;
	
	@Resource(name=IroningDeliveryHandler.BEANNAME)
	public IroningDeliveryHandler ironingDeliveryHandler;
	
	@Resource(name=ClothRackingHandler.BEANNAME)
	public ClothRackingHandler clothRackingHandler;
	
	@Resource(name=ClothDistributeHandler.BEANNAME)
	public ClothDistributeHandler clothDistributeHandler;
	
	@Resource(name=ClothAssociationHandler.BEANNAME)
	public ClothAssociationHandler clothRFIDAssociationHandler;

	@PostConstruct
	public void init() {
		handlerMap.put(ClothCollectionHandler.EVENT_NAME, clothCollectionHandler); //污衣收取
		handlerMap.put(IroningDeliveryHandler.EVENT_NAME, ironingDeliveryHandler); //交付平熨
		handlerMap.put(ClothRackingHandler.EVENT_NAME, clothRackingHandler); //淨衣上架
		handlerMap.put(ClothDistributeHandler.EVENT_NAME, clothDistributeHandler); //提取衣物
		handlerMap.put(ClothAssociationHandler.EVENT_NAME, clothRFIDAssociationHandler); //RFID關聯
	}

	public HandheldHandler getHandler(String eventName) {
		if(handlerMap.containsKey(eventName)) {
			return handlerMap.get(eventName);
		}
		return null;
	}

	private int batchCount = 50;
	private Queue<HandheldEvent> waitingList = new LinkedList<HandheldEvent>();
	private Object waitingListSyncObject = new Object();

	@Scheduled(fixedDelay=2000)
	public void handle() {
		
		int count = 0;
		while(waitingList.peek()!=null && count<batchCount) {
			count++;
			HandheldEvent event = null;
			synchronized (waitingListSyncObject) {
				event = waitingList.poll();
			}
			log4j.info("event name : " + event.getEventName());
			HandheldHandler handler = this.getHandler(event.getEventName());
			if(handler != null) {
				handler.handle(event);	
			}
		}
	}

	public void addWaitingList(HandheldEvent event) {
		synchronized (waitingListSyncObject) {
			waitingList.add(event);
		}
	}
}
