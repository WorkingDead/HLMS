package module.scheduler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import module.service.ServiceFactory;
import module.service.all.SchedulerService;

import org.apache.log4j.Logger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component(HouseKeepingScheduler.BEANNAME)
public class HouseKeepingScheduler {
	
	public static final String BEANNAME = "HouseKeepingScheduler";
	
	private static Logger log = Logger.getLogger(HouseKeepingScheduler.class);

	private static Object processingLockObject = new Object();		//For Skipping Policy Use
	private static Object implementingLockObject = new Object();	//For Non-Skipping Policy Use

	@Resource(name=ServiceFactory.SchedulerService)
	public SchedulerService schedulerService;



	//is this job running?
	private boolean processing = false;

	public boolean isProcessing() {
		return processing;
	}



	private static HouseKeepingScheduler instance;
	
	public static HouseKeepingScheduler getInstance() {
		return instance;
	}
	
	@PostConstruct
	public void afterInit(){
		instance = this;
	}



//	@Scheduled(cron="0 00 12 * * *")
//	@Scheduled(fixedDelay=5000)//test only
	@Scheduled(fixedDelay=3600000) //clear cache pre hour
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
			log.error( HouseKeepingScheduler.class.getSimpleName() + " has error!!!", e );
		}

		//end process
		processing = false;
		return;
	}
	
	public void executeImpl() throws Exception {
		
		synchronized ( implementingLockObject ) {	//Non-Skipping Policy

			int result = schedulerService.houseKeeping();
			log.info( HouseKeepingScheduler.class.getSimpleName() + " success, removed " + result + " record" );

		}
	}
}
