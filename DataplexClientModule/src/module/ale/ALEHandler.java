package module.ale;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;
import module.dao.XmlCache;
import module.dao.XmlCache.ProcessStatus;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.service.DataplexClientService;
import module.service.ServiceFactory;

import org.hibernate.Criteria;
//import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import utils.xml.JaxbXmlFactory;

import epcglobal.ale.xsd._1.ECReport;
import epcglobal.ale.xsd._1.ECReportGroup;
import epcglobal.ale.xsd._1.ECReportGroupListMember;
import epcglobal.ale.xsd._1.ECReports;

@Component(ALEHandler.BEANNAME)
public class ALEHandler {
	
//	private static final Logger log = Logger.getLogger(ALEHandler.class);
	
	public static final String BEANNAME = "ALEHandler";
	public static final String ECSpecHandler_BEANNAME = "ECSpecHandler";
	
	private HashMap<String, ECSpecHandler> ECSpecHandlerMap;
	
	//this class is a spring component, so can use spring resource to get the service
	@Resource(name=ServiceFactory.DataplexClientService)
	private DataplexClientService dataplexClientService;
	
	public ALEHandler()
	{
		ECSpecHandlerMap = new HashMap<String, ECSpecHandler>();
	}
	
	public void addECSpecHandler(String ecSpec, ECSpecHandler ecSpecHandler)
	{
		if(ECSpecHandlerMap.containsKey(ecSpec))
		{
			throw new RuntimeException("ECSpec " + ecSpec + " already defined!");
		}
		ECSpecHandlerMap.put(ecSpec, ecSpecHandler);
	}
	//	aleHandler.addECSpecHandler(Kiosk1ClothCollectionHandler.SPECNAME, kiosk1ClothCollectionHandler)
	@Scheduled(fixedRate=500)
	public void execute()
	{
		int total = 0;
		
		XmlCache xmlCache = new XmlCache();
		xmlCache.setProcessed(false);
		total = getDataplexClientService().totalByExample(XmlCache.class, xmlCache, null);
		
		if(total==0)
		{
			return;
		}
		
		List<XmlCache> xmlCaches = getDataplexClientService().findByExample(XmlCache.class, xmlCache, 0, 500, new CustomCriteriaHandler<XmlCache>() 
				{
			@Override
			public void makeCustomCriteria(Criteria baseCriteria) {
				baseCriteria.add(Restrictions.eq("processed", false));
				
			}
			
		}, new CustomLazyHandler<XmlCache>() {

			@Override
			public void LazyList(List<XmlCache> list) {
				for(Iterator<XmlCache> it = list.iterator();it.hasNext();)
				{
					XmlCache cache = it.next();
					cache.getXmlContent().toString();
				}
			}
			
		}, Order.asc("creationDate"));
		
		for(Iterator<XmlCache> it = xmlCaches.iterator();it.hasNext();)
		{
			XmlCache cache = it.next();
			try {
				
				JAXBElement<ECReports> root = JaxbXmlFactory.unmarshaller(ECReports.class, new StringBuffer(cache.getXmlContent()));
				ECReports ecReports = root.getValue();
				
				this.process(ecReports);
				
				cache.setStatus(ProcessStatus.Success);
			} catch (Exception e) {
				e.printStackTrace();
				cache.setStatus(ProcessStatus.Fail);
			}
			
			cache.setProcessed(true);
			cache.setProcessedTime(Calendar.getInstance());
			
		}
		
		getDataplexClientService().saveList(XmlCache.class, xmlCaches);
	}
	
	public void process(ECReports ecReports) throws Exception
	{
		if(ecReports==null)
		{
			throw new Exception("ecReports == null");
		}
		
		String specName = ecReports.getSpecName();
		if(!this.ECSpecHandlerMap.containsKey(specName))
		{
			throw new Exception("specName " + specName + " is not defined!");
		}
		
		//grabbing rfid list for easy handle
		List<String> rawHexList = new ArrayList<String>();
		
		for(Iterator<ECReport> it = ecReports.getReports().getReport().iterator();it.hasNext();)
		{
			ECReport report = it.next();
			
			for(Iterator<ECReportGroup> it2 = report.getGroup().iterator();it2.hasNext();)
			{
				ECReportGroup group = it2.next();
				if(group.getGroupCount().getCount()>0)
				{
					for(Iterator<ECReportGroupListMember> it3 = group.getGroupList().getMember().iterator();it3.hasNext();)
					{
						ECReportGroupListMember member = it3.next();
						if(member.getRawHex()!=null)
							rawHexList.add(member.getRawHex().getValue());
					}
				}  
			}
		}
		
		ECSpecHandlerMap.get(specName).handle(rawHexList, ecReports);
		
	}

	public DataplexClientService getDataplexClientService() {
		return dataplexClientService;
	}

	public void setDataplexClientService(DataplexClientService dataplexClientService) {
		this.dataplexClientService = dataplexClientService;
	}
	
//	@Scheduled(cron="0 30 10 * * * *")
//	@Scheduled(fixedDelay=1000) //don't know why not work!!!!!!!!!!!!!!!!!
//	public void houseKeeping()
//	{
//		try {
//			getDataplexClientService().houseKeeping();
//		} catch (Exception e) {
//			log.warn("Error when house keeping", e);
//		}
//	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * Dataplex ==> CallbackServer ==> PushAction ==> Database
	 * 
	 *
	 * Scheduler (2sec) ==> ALEHandler execute() ==> Database ==> ECReports ==> process 
	 * 
	 * 
	 * 
	 */

}
