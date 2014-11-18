package module.dao;

import javax.annotation.PostConstruct;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(BeansFactorySystem.BEANNAME)
public class BeansFactorySystem
{
	private static final Logger log4j = Logger.getLogger(BeansFactorySystem.class);
	public static final String BEANNAME = "BeansFactorySystem";

	private static BeansFactorySystem instance = null;

	public static BeansFactorySystem getInstance()
	{
		return instance;
	}

	@PostConstruct
	public void afterInit()
	{
		instance = this;

		PrintService[] allServices = PrintServiceLookup.lookupPrintServices(null, null);
		log4j.info("Number of print services: " + allServices.length);
		
		for ( int i = 0; i < allServices.length; i++ ) {
			
			PrintService printService = allServices[i];

			log4j.info(printService  + " supports :");
			DocFlavor[] flavors = printService.getSupportedDocFlavors();
			for (int j = 0; j < flavors.length; j++ )
			{
				log4j.info("\t" + flavors[j]);
			}
		}
	}

	public BeansFactorySystem()
	{

	}



	@Value("${attachment.image.thumbnail.width}")
	private int maxThumbnailWidth;
	
	@Value("${attachment.image.thumbnail.height}")
	private int maxThumbnailHeight;



	public int getMaxThumbnailWidth() {
		return maxThumbnailWidth;
	}

	public void setMaxThumbnailWidth(int maxThumbnailWidth) {
		this.maxThumbnailWidth = maxThumbnailWidth;
	}

	public int getMaxThumbnailHeight() {
		return maxThumbnailHeight;
	}

	public void setMaxThumbnailHeight(int maxThumbnailHeight) {
		this.maxThumbnailHeight = maxThumbnailHeight;
	}
}
