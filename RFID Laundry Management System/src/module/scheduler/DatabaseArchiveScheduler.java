package module.scheduler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import module.service.ServiceFactory;
import module.service.all.SchedulerService;

import org.apache.log4j.Logger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component(DatabaseArchiveScheduler.BEANNAME)
public class DatabaseArchiveScheduler {
	
	public static final String BEANNAME = "DatabaseArchiveScheduler";
	
	private static Logger log = Logger.getLogger(DatabaseArchiveScheduler.class);
	
	private static Object processingLockObject = new Object();		//For Skipping Policy Use
	private static Object implementingLockObject = new Object();	//For Non-Skipping Policy Use

	@Resource(name=ServiceFactory.SchedulerService)
	public SchedulerService schedulerService;



	//is this job running?
	private boolean processing = false;

	public boolean isProcessing() {
		return processing;
	}



	private static DatabaseArchiveScheduler instance;
	
	public static DatabaseArchiveScheduler getInstance() {
		return instance;
	}
	
	@PostConstruct
	public void afterInit(){
		instance = this;
	}

	@Scheduled(cron="0 0 2 * * ?") //production
//	@Scheduled(fixedDelay=30000)//test only
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
			log.error( DatabaseArchiveScheduler.class.getSimpleName() + " has error!!!", e );
		}
		
		//end process
		processing = false;
		return;
	}
	
	public void executeImpl() throws Exception {
		
		synchronized ( implementingLockObject ) {	//Non-Skipping Policy
			
			//Normal job and service job
			try {
				schedulerService.transactionDatabaseArchive();
				log.info( DatabaseArchiveScheduler.class.getSimpleName() + " success!!" );
			}
			catch (Exception e) {
				log.error( DatabaseArchiveScheduler.class.getSimpleName() + ": executeImpl:", e);
			}
			//Normal job and service job
		}
	}
}
