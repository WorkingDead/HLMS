package module.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import module.dao.general.Receipt;
import module.dao.general.Receipt.ReceiptStatus;
import module.dao.general.Receipt.ReceiptType;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.service.ServiceFactory;
import module.service.all.GeneralService;
import module.service.all.SchedulerService;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component(ReceiptStatusScheduler.BEANNAME)
public class ReceiptStatusScheduler implements ReceiptStatusSchedulerAsyncWorker
{
	public static final String BEANNAME = "ReceiptStatusScheduler";
	private static Logger log = Logger.getLogger(ReceiptStatusScheduler.class);
	
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

	private static ReceiptStatusScheduler instance;
	public static ReceiptStatusScheduler getInstance()
	{
		return instance;
	}
	@PostConstruct
	public void afterInit()
	{
		instance = this;
	}



/////////////////////////////////////////////////////
//	1. sec 		(0-59) 
//	2. min 		(0-59) 
//	3. hour 	(0-23) 
//	4. date 	(1-31) 
//	5. month 	(1-12或SUN-DEC) 
//	6. day 		(1-7或SUN-SAT) 
//	7. year 	(1970-2099)
//
//	Example:
//	0 0 10,14,16 * * ? 		每天上午10點,下午2點和下午4點 
//	0 0,15,30,45 * 1-10 * ? 每月前10天每隔15分鐘
//	30 0 0 1 1 ? 2012 		在2012年1月1日午夜過30秒時 
//	0 0 8-5 ? * MON-FRI 	每個工作日的工作時間
//	- 區間
//	* 萬用字元 
//	? 你不想設置那個欄位
/////////////////////////////////////////////////////
	
	@Scheduled(cron="0 15 0 * * ?")
//	@Scheduled(fixedDelay=5000)
	public void execute()
	{
		log.info("###########################################");
		log.info("Receipt Status Scheduler Start");
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
			log.error(ReceiptStatusScheduler.class.getSimpleName() + " has error!!!", e);
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
			log.error(ReceiptStatusScheduler.class.getSimpleName() + " has error!!!", e);
		}
	}
	
	public void executeImpl() throws Exception
	{
		synchronized (implementingLockObject)
		{
			// 1. find all UNFINISHED receipts (collect, iron)
			List<Receipt> receiptList = this.generalService.findByExample(Receipt.class, null, null, null, 
					
					new CustomCriteriaHandler<Receipt>()
					{
						@Override
						public void makeCustomCriteria(Criteria criteria)
						{
							Disjunction cases1 = Restrictions.disjunction();
							cases1.add( Restrictions.eq("receiptStatus", ReceiptStatus.Processing));
							cases1.add( Restrictions.eq("receiptStatus", ReceiptStatus.Followup));
							criteria.add(cases1);
							
							Disjunction cases2 = Restrictions.disjunction();
							cases2.add( Restrictions.eq("receiptType", ReceiptType.Collect) );
							cases2.add( Restrictions.eq("receiptType", ReceiptType.Iron) );
							criteria.add(cases2);
						}
					}, 
					
					new CustomLazyHandler<Receipt>()
					{
						@Override
						public void LazyList(List<Receipt> list)
						{
							Receipt receipt = null;
							for (int i = 0; i < list.size(); i++)
							{
								receipt = list.get(i);
								if (receipt.getHistoryClothSet() != null)
								{
									receipt.getHistoryClothSet().size();
								}
							}
						}
					},
					
					Order.asc("id"));
			log.info("UNFINISHED receiptList: " + receiptList.size());
			
			
			// 2. For each receipt, check if all clothes racked
			Receipt receipt = null;
			boolean finished;
			List<Receipt> resultList = new ArrayList<Receipt>();
			for (int i = 0; i < receiptList.size(); i++)
			{
				receipt = receiptList.get(i);
				finished = true;
				
				if (receipt.getHistoryClothSet() != null && receipt.getHistoryClothSet().size() > 0)
				{
					finished = false;
				}
				
				if (finished)
				{
					receipt.setReceiptStatus(ReceiptStatus.Finished);
					receipt.setFinishDate(Calendar.getInstance());
					resultList.add(receipt);
					log.info("Receipt '" + receipt.getCode() + "' OK!");
				}
				else
				{
					log.info("Receipt '" + receipt.getCode() + "' NOT finished yet!");
				}
				
				this.generalService.saveList(Receipt.class, resultList);
			}
		}
	}
}
