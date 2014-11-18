package module.scheduler;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import module.dao.general.SpecialEvent;
import module.dao.general.SpecialEvent.SpecialEventName;
import module.dao.general.SpecialEvent.SpecialEventStatus;
import module.dao.iface.CustomCriteriaHandler;

import module.dao.master.Cloth.ClothStatus;

import module.service.ServiceFactory;
import module.service.all.GeneralService;
import module.service.all.SchedulerService;

import org.apache.log4j.Logger;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component(SpecialEventIronScheduler.BEANNAME)
public class SpecialEventIronScheduler implements SpecialEventSchedulerAsyncWorker
{
	public static final String BEANNAME = "SpecialEventIronScheduler";
	private static Logger log = Logger.getLogger(SpecialEventIronScheduler.class);
	
	private static Object processingLockObject = new Object();		//For Skipping Policy Use
	private static Object implementingLockObject = new Object();	//For Non-Skipping Policy Use
	
	@Resource(name=ServiceFactory.SchedulerService)
	public SchedulerService schedulerService;

	@Resource(name=ServiceFactory.GeneralService)
	public GeneralService generalService;
	
	// Is this job running?
	private boolean processing = false;
	public boolean isProcessing()
	{
		return processing;
	}
	
	private static SpecialEventIronScheduler instance;
	public static SpecialEventIronScheduler getInstance()
	{
		return instance;
	}
	@PostConstruct
	public void afterInit()
	{
		instance = this;
	}
	
	
// ///////////////////////////////////////////////////
// 1. sec (0-59)
// 2. min (0-59)
// 3. hour (0-23)
// 4. date (1-31)
// 5. month (1-12或SUN-DEC)
// 6. day (1-7或SUN-SAT)
// 7. year (1970-2099)
//
// Example:
// 0 0 10,14,16 * * ? 每天上午10點,下午2點和下午4點
// 0 0,15,30,45 * 1-10 * ? 每月前10天每隔15分鐘
// 30 0 0 1 1 ? 2012 在2012年1月1日午夜過30秒時
// 0 0 8-5 ? * MON-FRI 每個工作日的工作時間
// - 區間
// * 萬用字元
// ? 你不想設置那個欄位
// ///////////////////////////////////////////////////
	@Scheduled(cron="0 20 0 * * ?") // everyday midnight 00:10
//	@Scheduled(fixedDelay=10000)
	public void execute()
	{
		log.info("###########################################");
		log.info("Special-Event Scheduler Start");
		log.info("###########################################");
		
//		synchronized ( processingLockObject ) {		//Skipping Policy
//			if( processing )
//				return;
//			else
//				processing = true;
//		}

		try
		{
			executeImpl();
		}
		catch (Exception e)
		{
			log.error(SpecialEventIronScheduler.class.getSimpleName() + " has error!", e);
		}
		
//		//end process
//		processing = false;
//		return;
	}
	
	
	@Async
	public void executeAsync()
	{
		try
		{
			executeImpl();
		}
		catch (Exception e)
		{
			log.error(SpecialEventIronScheduler.class.getSimpleName() + " has error!", e);
		}
	}
	
	public void executeImpl() throws Exception
	{

		synchronized (implementingLockObject)
		{
			// 1. Get all ClothIroningDelay-Special-Events but their cloth in Ready-status (means back)
			List<SpecialEvent> seList = this.generalService.findByExample(SpecialEvent.class, null, null, null, 
					
					new CustomCriteriaHandler<SpecialEvent>()
					{
						@Override
						public void makeCustomCriteria(Criteria criteria)
						{
							criteria.add(Restrictions.eq("specialEventStatus", SpecialEventStatus.Followup));
							criteria.add(Restrictions.eq("specialEventName", SpecialEventName.ClothIroningDelay));
							
							Criteria clothCriteria = criteria.createCriteria("cloth");
							clothCriteria.add(Restrictions.eq("clothStatus", ClothStatus.Ready));
						}
					}, null, Order.asc("id"));
			log.info("seList: " + seList.size());
			
			
			// 2. For each such Special-Event, change event-status to "Finished"
			SpecialEvent se = null;
			for (int i = 0; i < seList.size(); i++)
			{
				se = seList.get(i);
				se.setSpecialEventStatus(SpecialEventStatus.Finished);
			}
			
			this.generalService.saveList(SpecialEvent.class, seList);
		}
	}
}
