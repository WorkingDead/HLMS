package module.scheduler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import module.service.ServiceFactory;
import module.service.all.SchedulerService;

import org.apache.log4j.Logger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component(SystemGarbageCollectionScheduler.BEANNAME)
public class SystemGarbageCollectionScheduler {

	public static final String BEANNAME = "SystemGarbageCollectionScheduler";
	
	private static Logger log = Logger.getLogger(SystemGarbageCollectionScheduler.class);

	private static Object processingLockObject = new Object();		//For Skipping Policy Use
	private static Object implementingLockObject = new Object();	//For Non-Skipping Policy Use

	@Resource(name=ServiceFactory.SchedulerService)
	public SchedulerService schedulerService;



	//is this job running?
	private boolean processing = false;

	public boolean isProcessing() {
		return processing;
	}



	private static SystemGarbageCollectionScheduler instance;
	
	public static SystemGarbageCollectionScheduler getInstance() {
		return instance;
	}
	
	@PostConstruct
	public void afterInit(){
		instance = this;
	}



	@Scheduled(cron="0 30 6 * * ?") //production
//	@Scheduled(fixedDelay=5000)//test only
	public void execute()
	{
		synchronized ( processingLockObject ) {		//Skipping Policy
			if( processing )
				return;
			else
				processing = true;
		}
		
		try {
			executeImpl();
		}
		catch (Exception e) {
			log.error( SystemGarbageCollectionScheduler.class.getSimpleName() + " has error!!!", e );
		}
		
		//end process
		processing = false;
		return;
	}
	
	public void executeImpl() throws Exception {
		
		synchronized ( implementingLockObject ) {	//Non-Skipping Policy

//		    long maxMem = Runtime.getRuntime().maxMemory();
//		    long freeMem = Runtime.getRuntime().freeMemory();
//		    long heapMem = Runtime.getRuntime().totalMemory();
//		    long usedMem = heapMem - freeMem;
//		    System.out.println("Before Memory used: " + (int) (usedMem * 100 / maxMem) + "%");

			System.gc();

//		    maxMem = Runtime.getRuntime().maxMemory();
//		    freeMem = Runtime.getRuntime().freeMemory();
//		    heapMem = Runtime.getRuntime().totalMemory();
//		    usedMem = heapMem - freeMem;
//		    System.out.println("After Memory used: " + (int) (usedMem * 100 / maxMem) + "%");

		}
	}
}
