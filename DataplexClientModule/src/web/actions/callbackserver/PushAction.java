package web.actions.callbackserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBElement;

import module.dao.XmlCache;
import module.service.DataplexClientService;
import module.service.ServiceFactory;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.interceptor.ServletRequestAware;

import utils.spring.SpringUtils;
import utils.xml.JaxbXmlFactory;

import com.opensymphony.xwork2.ActionSupport;

import epcglobal.ale.xsd._1.ECReports;

@Action
public class PushAction extends ActionSupport implements ServletRequestAware {
	
	public String execute() throws Exception{
		
		try {
			InputStream inA = httpServletRequest.getInputStream();
			StringBuilder xml = new StringBuilder();
			BufferedReader in = new BufferedReader( new InputStreamReader(inA));
	        String line;
	        
	        while (true) {
		        if (( line= in.readLine() ) != null ) {
		        	xml.append(line);
		        } else 
		        	break;
	        } 
			
//			JAXBElement<ECReports> root = JaxbXmlFactory.unmarshaller(ECReports.class, inA);
//			ECReports ecReports = root.getValue();
	        
			XmlCache cache = new XmlCache();
			cache.setXmlContent(xml.toString());
			cache.setProcessed(false);
			
			getDataplexClientService().save(XmlCache.class, cache);
	//		getDataplexClientService().saveList(XmlCache.class, xmlCaches);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return null;
	}
	
	private DataplexClientService dataplexClientService;
	
	public DataplexClientService getDataplexClientService()
	{
		if(dataplexClientService==null)
			dataplexClientService = SpringUtils.getBean(ServiceFactory.DataplexClientService);
		return dataplexClientService;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7994533663530084155L;
	
	private HttpServletRequest httpServletRequest;

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		this.httpServletRequest = arg0;
	}

}
