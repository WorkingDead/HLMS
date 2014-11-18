package module.service.all;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import module.dao.DaoFactory;
import module.dao.general.AlertDao;
import module.dao.general.GenerateNoRecord;
import module.dao.general.GenerateNoRecordDao;
import module.dao.general.HistoryClothDao;
import module.dao.general.Receipt;
import module.dao.general.Receipt.ReceiptType;
import module.dao.general.ReceiptPatternIron;
import module.dao.general.ReceiptPatternIronDao;
import module.dao.general.SpecialEvent;
import module.dao.general.SpecialEventDao;
import module.dao.general.ReceiptDao;
import module.dao.general.Transaction;
import module.dao.general.Transaction.TransactionName;
import module.dao.general.TransactionDao;
import module.dao.general.GenerateNoRecord.GenerateNoRecordType;
import module.dao.master.Cloth;
import module.dao.master.ClothDao;
import module.dao.system.Users;

import module.service.ServiceFactory;
import module.service.impl.BaseCRUDDaosServiceImpl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import utils.convertor.StringConvertor;

import web.actions.BaseActionKiosk;

@Service(ServiceFactory.GeneralService)
public class GeneralServiceImpl extends BaseCRUDDaosServiceImpl implements GeneralService
{
	//Log4j Logger
	private final Logger log = Logger.getLogger( getClass() );



	private static Object lockObject = new Object();



	@Resource(name=DaoFactory.CLOTH_DAO)
	public ClothDao clothDao;
	
	@Resource(name=DaoFactory.ALERT_DAO)
	public AlertDao alertDao;

	@Resource(name=DaoFactory.RECEIPT_DAO)
	public ReceiptDao receiptDao;
	
	@Resource(name=DaoFactory.HISTORY_CLOTH_DAO)
	public HistoryClothDao historyClothDao;
	
	@Resource(name=DaoFactory.RECEIPT_PATTERN_IRON_DAO)
	public ReceiptPatternIronDao receiptPatternIronDao;
	
	@Resource(name=DaoFactory.TRANSACTION_DAO)
	public TransactionDao transactionDao;
	
	@Resource(name=DaoFactory.SPECIAL_EVENT_DAO)
	public SpecialEventDao specialEventDao;
	
	@Resource(name=DaoFactory.GenerateNoRecordDao)
	public GenerateNoRecordDao generateNoRecordDao;

	
	@Resource(name=ServiceFactory.SystemService)
	public SystemService systemService;
	
	////////////////////////////////////////////
	// For HLMS
	////////////////////////////////////////////
	// receipt code for Iron-Delivery Page
	@Override
	public synchronized String genIroningDeliveryReceiptCode()
	{
		synchronized (lockObject)
		{
			return generateNo(GenerateNoRecord.GenerateNoRecordType.TYPE_IRONING_DELIVERY);
		}
	}
	
	public synchronized String genClothRackingReceiptCode()
	{
		synchronized (lockObject)
		{
			return generateNo(GenerateNoRecord.GenerateNoRecordType.TYPE_CLOTH_RACKING);
		}
	}
	
	public void saveReceiptAndTransaction(Receipt receipt)
	{
		TransactionName transactionName = null;
		if (receipt.getReceiptType().equals(ReceiptType.Iron))
		{
			transactionName = TransactionName.IroningDelivery;
		}
		else if (receipt.getReceiptType().equals(ReceiptType.Collect))
		{
			transactionName = TransactionName.Collection;
		}
		else if (receipt.getReceiptType().equals(ReceiptType.Rack))
		{
			transactionName = TransactionName.Racking;
		}
		else if (receipt.getReceiptType().equals(ReceiptType.Distribute))
		{
			transactionName = TransactionName.Distribution;
		}
		else
		{
			System.out.println("[Error] Invalid Receipt Type!");
		}
		
		// Record Transaction 
		List<Transaction> transList = new ArrayList<Transaction>();
		Transaction tmpTransaction = null;
		
		Iterator<Cloth> itCloth = null;
		Cloth cloth = null;
		if (transactionName.equals(TransactionName.IroningDelivery))
		{
			///////////////////////////////////////////////////
			// Ironing Delivery (System User)
			///////////////////////////////////////////////////
			Iterator<ReceiptPatternIron> itPattern = receipt.getReceiptPatternIronSet().iterator();
			ReceiptPatternIron pat = null;
			
			while (itPattern.hasNext())
			{
				pat = itPattern.next();
				itCloth = pat.getClothSet().iterator();
				while (itCloth.hasNext())
				{
					cloth = itCloth.next();
					tmpTransaction = new Transaction();
					tmpTransaction.setCloth(cloth);
					tmpTransaction.setTransactionName(transactionName);
					tmpTransaction.setTransHandledByStaff(null);
					tmpTransaction.setTransPickedByStaff(null);
					transList.add(tmpTransaction);
				}
			}
		}
		else if (transactionName.equals(TransactionName.Collection))
		{
			///////////////////////////////////////////////////
			// Cloth Collection (Kiosk User)
			///////////////////////////////////////////////////
			Users createdByUser = (Users) this.systemService.loadUserByUsername( BaseActionKiosk.KioskUserName );
			itCloth = receipt.getClothSet().iterator();
			while (itCloth.hasNext())
			{
				cloth = itCloth.next();
				tmpTransaction = new Transaction();
				tmpTransaction.setCloth(cloth);
				tmpTransaction.setTransactionName(TransactionName.Collection);
				tmpTransaction.setCreatedBy( createdByUser );
				tmpTransaction.setTransHandledByStaff(receipt.getStaffHandledBy());
				tmpTransaction.setTransPickedByStaff(null);
				transList.add(tmpTransaction);
				
				//////////////////////////////////////////////
				// Save the cloth count
				//////////////////////////////////////////////
				if ( cloth.getWashingCount() == null ) {
					cloth.setWashingCount( 0 );
					log.info( "cloth id (" + cloth.getId() + ") washing count is null and then becomes 0" );
				}
				
				cloth.setWashingCount( cloth.getWashingCount() + 1 );
				log.info( "cloth id (" + cloth.getId() + ") washing count +1" );
			}
		}
		else if (transactionName.equals(TransactionName.Racking))
		{
			///////////////////////////////////////////////////
			// Cloth Racking (System User)
			///////////////////////////////////////////////////
			itCloth = receipt.getClothSet().iterator();
			while (itCloth.hasNext())
			{
				cloth = itCloth.next();
				tmpTransaction = new Transaction();
				tmpTransaction.setCloth(cloth);
				tmpTransaction.setTransactionName(TransactionName.Racking);
				tmpTransaction.setTransHandledByStaff(null);
				tmpTransaction.setTransPickedByStaff(null);
				transList.add(tmpTransaction);
			}
		}
		else if (transactionName.equals(TransactionName.Distribution))
		{
			///////////////////////////////////////////////////
			// Cloth Dist (Kiosk User)
			///////////////////////////////////////////////////
			Users createdByUser = (Users) this.systemService.loadUserByUsername( BaseActionKiosk.KioskUserName );
			itCloth = receipt.getClothSet().iterator();
			while (itCloth.hasNext())
			{
				cloth = itCloth.next();
				
				tmpTransaction = new Transaction();
				tmpTransaction.setCloth(cloth);
				tmpTransaction.setTransactionName(transactionName);
				tmpTransaction.setCreatedBy( createdByUser );
				tmpTransaction.setTransHandledByStaff(receipt.getStaffHandledBy());
				tmpTransaction.setTransPickedByStaff(receipt.getStaffPickedBy());
				transList.add(tmpTransaction);
			}
		}
		
		
		//////////////////////////////////////////////
		// 1. Save the receipt
		//////////////////////////////////////////////
		this.receiptDao.save(receipt);
		
		//////////////////////////////////////////////
		// 2. Save the Transaction
		//////////////////////////////////////////////
		this.transactionDao.saveList(transList);
	}
	

	
	
	
	////////////////////////////////////////////
	// For Kiosk
	////////////////////////////////////////////
	// receipt code for cloth-collection
	@Override
	synchronized public String genKioskClothCollectionReceiptCode()
	{
		synchronized (lockObject)
		{
			return generateNo(GenerateNoRecordType.TYPE_KIOSK_COLLECTION);
		}
	}

	// receipt code for cloth-distribute
	@Override
	synchronized public String genKioskClothDistributeReceiptCode()
	{
		synchronized (lockObject)
		{
			return generateNo(GenerateNoRecordType.TYPE_KIOSK_DISTRIBUTE);
		}
	}



	
	////////////////////////////////////////////
	// General Function
	////////////////////////////////////////////
	public String generateNo(GenerateNoRecordType type)
	{
		// getting current date
		Calendar cal = Calendar.getInstance();
		
		// getting the last information from database
		GenerateNoRecord gRecord = generateNoRecordDao.get( type );
		
		// if database record not exist, then create the new one
		if(gRecord == null)
		{
			gRecord = new GenerateNoRecord();
			gRecord.setId( type );
			gRecord.setLastGenerateDate(cal.getTime());
			gRecord.setLastGenerateNo(0);
		}
		
		Calendar lastGenerate = Calendar.getInstance();
		lastGenerate.setTime(gRecord.getLastGenerateDate());
		
		// checking is it the same DAY_OF_YEAR from last generate date
		if (cal.get(Calendar.DAY_OF_YEAR) != lastGenerate.get(Calendar.DAY_OF_YEAR))
		{
			// not the same DAY_OF_YEAR
			// so reset to 0
			gRecord.setLastGenerateDate(cal.getTime());
			gRecord.setLastGenerateNo(0);
		}
		
		// getting next index for this Kiosk no
		int index = gRecord.getLastGenerateNo() + 1;
		
		// change to 3 digit for the index
		String indexStr = StringConvertor.addBefore(Integer.toString(index), '0', 3);
		
		// add prefix
		String genNum = GenerateNoRecord.getGenerateNoRecordPrefix( type );
		
		// get number pattern for generating no
		genNum += GenerateNoRecord.getGenerateNoRecordPattern( type );
		
		// preparing the data
		genNum = genNum.replace("year", Integer.toString(cal.get(Calendar.YEAR)));
		genNum = genNum.replace("month", StringConvertor.addBefore(Integer.toString(cal.get(Calendar.MONTH) + 1), '0', 2));//auto make to two digit for month, +1 because the return value is start from 0
		genNum = genNum.replace("day", StringConvertor.addBefore(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)), '0', 2));//auto make to two digit for month
		genNum = genNum.replace("index", indexStr);
		
		// update the database record
		gRecord.setLastGenerateNo(index);
		gRecord.setLastGenerateDate(cal.getTime());
		generateNoRecordDao.save(gRecord);
		
		// return generated data
		return genNum;
	}

	@Override
	public void saveIroningExpirationSpecialEvent(Receipt receipt, List<SpecialEvent> eventList)
	{
		this.receiptDao.save(receipt);
		this.specialEventDao.saveList(eventList);
	}
}
