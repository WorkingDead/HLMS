package module.dao;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(BeansFactoryApplication.BEANNAME)
public class BeansFactoryApplication
{
	private static final Logger log4j = Logger.getLogger(BeansFactoryApplication.class);
	public static final String BEANNAME = "BeansFactoryApplication";

	private static BeansFactoryApplication instance = null;

	public BeansFactoryApplication()
	{
	}
	
	public static BeansFactoryApplication getInstance()
	{
		return instance;
	}

	@PostConstruct
	public void afterInit()
	{
		instance = this;
	}
	
	@Value("${hlms.ironing.delivery.prefix}")
	private String ironingDeliveryPrefix;
	@Value("${hlms.ironing.delivery.pattern}")
	private String ironingDeliveryPattern;
	
	@Value("${hlms.cloth.racking.prefix}")
	private String clothRackingPrefix;
	@Value("${hlms.cloth.racking.pattern}")
	private String clothRackingPattern;
	
	@Value("${kiosk.cloth.collection.prefix}")
	private String kioskCollectionPrefix;
	@Value("${kiosk.cloth.collection.pattern}")
	private String kioskCollectionPattern;
	
	@Value("${kiosk.cloth.distribute.prefix}")
	private String kioskDistributePrefix;
	@Value("${kiosk.cloth.distribute.pattern}")
	private String kioskDistributePattern;
	
	
	@Value("${master.all.dept.name}")
	private String allDeptName;
	
	@Value("${master.all.cloth.type.name}")
	private String allClothTypeName;
	
	@Value("${master.all.cloth.size}")
	private String allClothSize;
	
	@Value("${master.all.staff.position}")
	private String allStaffPosition;
	
	@Value("${master.all.roll.container.code}")
	private String allRollContainerCode;
	
	@Value("${system.printer.a4}")
	private String systemPrinterA4;
	
	@Value("${kiosk1.printer}")
	private String kiosk1Printer;
	
	@Value("${kiosk2.printer}")
	private String kiosk2Printer;
	
	public String getIroningDeliveryPrefix()
	{
		return ironingDeliveryPrefix;
	}
	public void setIroningDeliveryPrefix(String ironingDeliveryPrefix)
	{
		this.ironingDeliveryPrefix = ironingDeliveryPrefix;
	}
	public String getIroningDeliveryPattern()
	{
		return ironingDeliveryPattern;
	}
	public void setIroningDeliveryPattern(String ironingDeliveryPattern)
	{
		this.ironingDeliveryPattern = ironingDeliveryPattern;
	}
	public String getClothRackingPrefix()
	{
		return clothRackingPrefix;
	}
	public void setClothRackingPrefix(String clothRackingPrefix)
	{
		this.clothRackingPrefix = clothRackingPrefix;
	}
	public String getClothRackingPattern()
	{
		return clothRackingPattern;
	}
	public void setClothRackingPattern(String clothRackingPattern)
	{
		this.clothRackingPattern = clothRackingPattern;
	}
	public String getKioskCollectionPrefix()
	{
		return kioskCollectionPrefix;
	}
	public void setKioskCollectionPrefix(String kioskCollectionPrefix)
	{
		this.kioskCollectionPrefix = kioskCollectionPrefix;
	}
	public String getKioskCollectionPattern()
	{
		return kioskCollectionPattern;
	}
	public void setKioskCollectionPattern(String kioskCollectionPattern)
	{
		this.kioskCollectionPattern = kioskCollectionPattern;
	}
	public String getKioskDistributePrefix()
	{
		return kioskDistributePrefix;
	}
	public void setKioskDistributePrefix(String kioskDistributePrefix)
	{
		this.kioskDistributePrefix = kioskDistributePrefix;
	}
	public String getKioskDistributePattern()
	{
		return kioskDistributePattern;
	}
	public void setKioskDistributePattern(String kioskDistributePattern)
	{
		this.kioskDistributePattern = kioskDistributePattern;
	}
	public String getAllDeptName()
	{
		return allDeptName;
	}
	public void setAllDeptName(String allDeptName)
	{
		this.allDeptName = allDeptName;
	}
	public String getAllClothTypeName()
	{
		return allClothTypeName;
	}
	public void setAllClothTypeName(String allClothTypeName)
	{
		this.allClothTypeName = allClothTypeName;
	}
	public String getAllClothSize()
	{
		return allClothSize;
	}
	public void setAllClothSize(String allClothSize)
	{
		this.allClothSize = allClothSize;
	}
	
	public String getAllStaffPosition()
	{
		return allStaffPosition;
	}
	public void setAllStaffPosition(String allStaffPosition)
	{
		this.allStaffPosition = allStaffPosition;
	}
	public Map<String, String> getgetAllStaffPositionInMap() {
		
		Map<String, String> map = new HashMap<String, String>();
		
		String allPosition = getAllStaffPosition();
		String positionArray[] = allPosition.split(",");
		
		for ( int i = 0; i < positionArray.length; i++ ) {
			String positionKeyValue[] = positionArray[i].split("-");
			map.put(positionKeyValue[0], positionKeyValue[1]);
		}
		
		return map;
	}

	public String getAllRollContainerCode()
	{
		return allRollContainerCode;
	}
	public void setAllRollContainerCode(String allRollContainerCode)
	{
		this.allRollContainerCode = allRollContainerCode;
	}
	public String getSystemPrinterA4()
	{
		return systemPrinterA4;
	}
	public void setSystemPrinterA4(String systemPrinterA4)
	{
		this.systemPrinterA4 = systemPrinterA4;
	}
	public String getKiosk1Printer()
	{
		return kiosk1Printer;
	}
	public void setKiosk1Printer(String kiosk1Printer)
	{
		this.kiosk1Printer = kiosk1Printer;
	}
	public String getKiosk2Printer()
	{
		return kiosk2Printer;
	}
	public void setKiosk2Printer(String kiosk2Printer)
	{
		this.kiosk2Printer = kiosk2Printer;
	}

	@Value("${kiosk.securityException.pattern}")
	private String kioskSecurityExceptionPattern;

	public String getKioskSecurityExceptionPattern()
	{
		return kioskSecurityExceptionPattern;
	}
	public void setKioskSecurityExceptionPattern(String kioskSecurityExceptionPattern)
	{
		this.kioskSecurityExceptionPattern = kioskSecurityExceptionPattern;
	}

}
