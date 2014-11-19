package module.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import module.dao.general.Alert;
import module.dao.general.Alert.AlertType;
import module.dao.general.HistoryCloth;
import module.dao.general.Receipt;
import module.dao.general.ReceiptPatternIron;
import module.dao.general.SpecialEvent;
import module.dao.general.Receipt.ReceiptStatus;
import module.dao.general.Receipt.ReceiptType;
import module.dao.general.SpecialEvent.SpecialEventName;
import module.dao.general.SpecialEvent.SpecialEventStatus;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.service.ServiceFactory;
import module.service.all.GeneralService;
import module.service.all.SchedulerService;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component(ClothIroningExpirationScheduler.BEANNAME)
public class ClothIroningExpirationScheduler {
	
	public static final String BEANNAME = "ClothIroningExpirationScheduler";
	private static Logger log = Logger.getLogger(ClothIroningExpirationScheduler.class);
	
	private static Object processingLockObject = new Object();		//For Skipping Policy Use
	private static Object implementingLockObject = new Object();	//For Non-Skipping Policy Use
	
	private static final long HR_TO_MS_RATIO = 3600000;		// ms = hr x HR_TO_MS_RATIO (hr * 60 * 60 * 1000 = ms)
	
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

	private static ClothIroningExpirationScheduler instance;
	public static ClothIroningExpirationScheduler getInstance()
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
	@Scheduled(cron="0 0 17 * * ?")
//	@Scheduled(fixedDelay=30000)
	public void execute()
	{
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
			log.error(ClothIroningExpirationScheduler.class.getSimpleName()
					+ " has error!!!", e);
		}
		
//		//end process
//		processing = false;
//		return;
	}

	public void executeImpl() throws Exception
	{
		synchronized (implementingLockObject)
		{
			System.out.println("###########################################");
			System.out.println("Ironing Expiration Scheduler Start");
			System.out.println("###########################################");
			
			// Must get from DB everytime as the period may be changed by user
			Alert alert = generalService.get(Alert.class, AlertType.IroningExpiryPeriod);
			if (alert != null)
			{
				// 1. get the Ironing Expiry Period
				Integer hr = alert.getHours();
				long periodInMs = hr * HR_TO_MS_RATIO;
				
				CustomCriteriaHandler<Receipt> criteriaHandler = new CustomCriteriaHandler<Receipt>(){
					@Override
					public void makeCustomCriteria(Criteria criteria)
					{
						Disjunction cases1 = Restrictions.disjunction();
						cases1.add( Restrictions.eq("receiptStatus", ReceiptStatus.Processing));
						cases1.add( Restrictions.eq("receiptStatus", ReceiptStatus.Followup));
						criteria.add(cases1);
						
						Disjunction cases2 = Restrictions.disjunction();
						cases2.add( Restrictions.eq("receiptType", ReceiptType.Iron) );
						criteria.add(cases2);
					}
				};
				
				// For Individual Object ONLY
				CustomLazyHandler<Receipt> lazyHandler = new CustomLazyHandler<Receipt>(){
					
					@Override
					public void LazyObject(Receipt receipt)
					{
						Iterator<ReceiptPatternIron> itPattern = null;
						ReceiptPatternIron pattern = null;
						Iterator<Cloth> itCloth = null;
						Cloth tmpCloth = null;
						
						if (receipt.getHistoryClothSet() != null)
						{
							receipt.getHistoryClothSet().size();
						}
						
						if (receipt.getClothSet() != null)
						{
							receipt.getClothSet().size();
						}
						
						itPattern = receipt.getReceiptPatternIronSet().iterator();
						while (itPattern.hasNext())
						{
							pattern = itPattern.next();
							itCloth = pattern.getClothSet().iterator();
							while(itCloth.hasNext())
							{
								tmpCloth = itCloth.next();
								tmpCloth.getClothType().getId();
							}
						}
					}
				};
				
				// 1. find all UNFINISHED iron-receipt
				List<Receipt> receiptList1 = this.generalService.findByExample(Receipt.class, null, null, null, 
						criteriaHandler, 
						null,
						Order.asc("id"));
				System.out.println("receiptList: " + receiptList1.size());
				
				
				// 3. For each receipt, check if all clothes expired ironing period
				Long receiptId = null;
				Receipt receipt = null;
				Iterator<ReceiptPatternIron> itPattern = null;
				Iterator<Cloth> itCloth = null;
				ReceiptPatternIron pattern = null;
				Cloth cloth = null;
				Set<HistoryCloth> historyClothSet = null;
				Iterator<HistoryCloth> itHistoryCloth = null;
				Set<String> historyRfidSet = null;
				ArrayList<SpecialEvent> specialEventList = null;
				SpecialEvent se = null;
				Calendar curTime = Calendar.getInstance();
				Calendar pastTime = null;
				boolean changeStatus;
				for (int i = 0; i < receiptList1.size(); i++)
				{
					receiptId = receiptList1.get(i).getId();
					receipt = this.generalService.get(Receipt.class, receiptId, lazyHandler);
					specialEventList = new ArrayList<SpecialEvent>();
					changeStatus = false;
					
					historyClothSet = receipt.getHistoryClothSet();
					if (historyClothSet != null && historyClothSet.size() > 0)
					{
						itHistoryCloth = historyClothSet.iterator();
						historyRfidSet = new HashSet<String>();
						while (itHistoryCloth.hasNext())
						{
							historyRfidSet.add(itHistoryCloth.next().getRfid());
						}
					}
					
					
					// check HistoryClothSet, if it is Empty, this receipt must be FINISHED
					System.out.println("historyRfidSet: " + historyRfidSet.size());
					if (historyRfidSet != null && historyRfidSet.size() > 0)
					{
						itPattern = receipt.getReceiptPatternIronSet().iterator();
						while ( itPattern.hasNext() )
						{
							pattern = itPattern.next();
							pastTime = pattern.getIroningDeliveryTime();
							
							// check Roll-Container delivery time
							// must do every time!!! because diff container has diff delivery time
							long diff = curTime.getTimeInMillis() - pastTime.getTimeInMillis();
							
							log.info("Delay: " + (periodInMs - diff));
							if (diff > periodInMs)
							{
								itCloth = pattern.getClothSet().iterator();
								while (itCloth.hasNext() )
								{
									cloth = itCloth.next();
									
									// check is every cloth exist in the historyCloth set, if yes, it is late-back
									if (historyRfidSet.contains(cloth.getRfid()))
									{
										se = new SpecialEvent();
										se.setCloth(cloth);
										se.setClothType(cloth.getClothType());
										se.setStaff(cloth.getStaff());
										se.setSpecialEventName(SpecialEventName.ClothIroningDelay);
										se.setSpecialEventStatus(SpecialEventStatus.Followup);
										specialEventList.add(se);
										
										if (!changeStatus)
										{
											changeStatus = true;
											// we do not break the loop here because we add all late-back-cloth to Special Event
										}
									}
								}
							}
						}
						
						if ( changeStatus )
						{
							// We get a receipt without cloth info can prevent save this receipt also save the cloth
							Receipt resultReceipt = this.generalService.get(Receipt.class, receiptId);
							if (receipt.getReceiptStatus().equals(ReceiptStatus.Processing))
							{
								resultReceipt.setReceiptStatus(ReceiptStatus.Followup);
								log.info("Receipt '" + receipt.getCode() + "' changed to 'Follow-Up'!");
							}
							else
							{
								log.info("Receipt '" + receipt.getCode() + "' already marked!");
							}
							
							// (*) MUST be called every time because some clothes in Receipt change status

							if (specialEventList.size() > 0)
							{
								// Caution!! Receipt must be saved one by one
								// Because multiple receipt can contain save cloth, if these receipt try to save the cloth data at same time, Hibernate will fk you!!!
								try
								{
									this.generalService.saveIroningExpirationSpecialEvent(resultReceipt, specialEventList);
								}
								catch (Exception e)
								{
									log.error("[IroningSchedulerError] Receipt '" + resultReceipt.getId() + "' failed to change status to 'Follow-Up'");
								}
							}
						}
						else
						{
							if (receipt.getReceiptStatus().equals(ReceiptStatus.Processing))
								log.info("Receipt '" + receipt.getCode() + "' still have time.");
							else
								log.info("Receipt '" + receipt.getCode() + "' already marked.");
						}
					}
					else
					{
						log.info("Receipt '" + receipt.getCode() + "' already FINISHED! All clothes racked!");
					}
				}
			}
			else
			{
				log.info("Ironing Period not set, default setting '24' is applied and set!");
				
				// If no alert set, set a default one (by Horace)
				alert = new Alert();
				alert.setId(AlertType.IroningExpiryPeriod);
				alert.setHours(24);
				generalService.save(Alert.class, alert);
			}
		}
	}
}
