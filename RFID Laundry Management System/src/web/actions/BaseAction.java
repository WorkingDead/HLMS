package web.actions;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import module.dao.BeansFactoryApplication;
import module.dao.BeansFactorySystem;
import module.scheduler.ReceiptStatusScheduler;
import module.scheduler.ReceiptStatusSchedulerAsyncWorker;
import module.scheduler.SpecialEventIronScheduler;
import module.scheduler.SpecialEventSchedulerAsyncWorker;
import module.service.ServiceFactory;
import module.service.all.GeneralService;
import module.service.all.MasterService;
import module.service.all.OthersService;
import module.service.all.SystemService;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.util.ServletContextAware;
import utils.spring.SpringUtils;
import utils.web.html.HTMLHelper;
import utils.web.paging.Pagination;
import web.actions.others.AttachmentAction.AttachmentType;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.conversion.annotations.Conversion;

@Conversion
public class BaseAction extends ActionSupport implements ServletRequestAware, ServletResponseAware, ServletContextAware, SessionAware
{
	private static final Logger log4j = Logger.getLogger(BaseAction.class);
	private static final long serialVersionUID = -5531132509800230746L;
	
	public static final String TILES = "tiles_result";

	///////////////////////////////////////////////////////////////////////////////
	// I18N Key Fields
	///////////////////////////////////////////////////////////////////////////////
	public static final String SuccessMessage_SaveSuccess = "msg.save.success";
	public static final String ErrorMessage_SaveFail = "errors.save.fail";
	public static final String ErrorMessage_DataError = "errors.dataError";
	public static final String ErrorMessage_OperationError = "errors.operationError";
//	public static final String ErrorMessage_Duplicated = "errors.duplicated";
//	public static final String ErrorMessage_Required = "errors.required";
//	public static final String ErrorMessage_Invalid = "errors.invalid";
//	public static final String ErrorMessage_StringLength = "errors.stringLength";
//	public static final String ErrorMessage_StringLengthMin = "errors.stringLengthMin";
//	public static final String ErrorMessage_DoubleRange = "errors.doubleRange";
//	public static final String ErrorMessage_DoubleRangeMin = "errors.doubleRangeMin";
//	public static final String ErrorMessage_IntRange = "errors.intRange";
//	public static final String ErrorMessage_IntRangeMin = "errors.intRangeMin";
//	public static final String ErrorMessage_Custom = "errors.custom";
	public static final String ErrorMessage_UsernameCannotBeDuplicated = "security.errors.usernameCannotBeDuplicated";
	public static final String ErrorMessage_WrongUsernameOrPassword = "security.errors.wrongUsernameOrPassword";
	public static final String ErrorMessage_PasswordAndConfirmPassword = "security.errors.passwordAndConfirmPassword";
	public static final String ErrorMessage_NoAuthority = "security.errors.noAuthority";
	///////////////////////////////////////////////////////////////////////////////
	// I18N Key Fields
	///////////////////////////////////////////////////////////////////////////////

	public static enum SystemLanguage
	{
		en_US("menu.level3.language.en_US", "kiosk.menu.level1.language.en_US"),
		zh_CN("menu.level3.language.zh_CN", "kiosk.menu.level1.language.zh_CN");

		private String systemResourceKey;
		private String kioskResourceKey;
		
		SystemLanguage(String systemResourceKey, String kioskResourceKey)
		{
			this.systemResourceKey = systemResourceKey;
			this.kioskResourceKey = kioskResourceKey;
		}

		public String getSystemResourceKey() {
			return systemResourceKey;
		}

		public void setSystemResourceKey(String systemResourceKey) {
			this.systemResourceKey = systemResourceKey;
		}

		public String getKioskResourceKey() {
			return kioskResourceKey;
		}

		public void setKioskResourceKey(String kioskResourceKey) {
			this.kioskResourceKey = kioskResourceKey;
		}
	}

	//Only detect english and traditional chinese
	public SystemLanguage getCurrentSystemLanguage() {

		//They would be null when never clicking the lang button.
		//getSession().get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)
		//getSession().get(I18nInterceptor.DEFAULT_PARAMETER)
		//getSession().get(I18nInterceptor.DEFAULT_REQUESTONLY_PARAMETER)

		if ( ActionContext.getContext().getLocale().toString().equals( SystemLanguage.zh_CN.toString() ) ) {
			return SystemLanguage.zh_CN;	//If simplified chinese language is available, this should return the enum of simplified chinese language
		}
		else if ( ActionContext.getContext().getLocale().toString().split("_")[0].toString().indexOf( SystemLanguage.zh_CN.toString().split("_")[0] ) == 0 ) {
			//return SystemLanguage.zh_CN;
			
			//Returning "en_US" because the resource bundle properties files would only use "_zh_CN.properties" file
			//when the language of the browser exactly match "zh_CN".
			//Otherwise, the system would use the default file.
			//Therefore, returning "en_US" is to synchronize what the system does above.
			return SystemLanguage.en_US;
		}
		else {
			return SystemLanguage.en_US;
		}
	}
	
	public List<SystemLanguage> getSystemLanguageList() {
		
		return Arrays.asList( SystemLanguage.values() );
	}
	
	public static enum TrueFalseBoolean
	{
		True(true, "boolean.true"), False(false, "boolean.false");

		private Boolean value;
		private String resKey;
		
		private TrueFalseBoolean(Boolean value, String resKey)
		{
			this.value = value;
			this.resKey = resKey;
		}
		public Boolean getValue()
		{
			return value;
		}
		public void setValue(Boolean value)
		{
			this.value = value;
		}
		public String getResKey()
		{
			return resKey;
		}
		public void setResKey(String resKey)
		{
			this.resKey = resKey;
		}
	}
	
	public static enum YesNoInt
	{
		Yes("boolean.true", 1), No("boolean.false", -1);
		
		private String resKey;
		private Integer value;
		
		private YesNoInt(String resKey, Integer value)
		{
			this.resKey = resKey;
			this.value = value;
		}
		public String getResKey()
		{
			return resKey;
		}
		public void setResKey(String resKey)
		{
			this.resKey = resKey;
		}
		public Integer getValue()
		{
			return value;
		}
		public void setValue(Integer value)
		{
			this.value = value;
		}
	}
	
	
	public static enum EnableDisableStatus
	{
		Enable(true, "label.enable"), 
		Disable(false, "label.disable");

		private boolean value;
		private String resKey;

		EnableDisableStatus(boolean value, String resKey)
		{
			this.value = value;
			this.resKey = resKey;
		}

		public boolean getValue()
		{
			return value;
		}

		public void setValue(boolean value)
		{
			this.value = value;
		}

		public String getResKey()
		{
			return resKey;
		}

		public void setResKey(String resKey)
		{
			this.resKey = resKey;
		}
	}
	
	private List emptyList = new LinkedList();
	
	public List getEmptyList() {
		return emptyList;
	}

	public void setEmptyList(List emptyList) {
		this.emptyList = emptyList;
	}



	public BaseAction()
	{
		super();
		this.currentMethodName = ActionContext.getContext().getActionInvocation().getProxy().getMethod();
	}
	
	public String execute()
	{
		return null;	// Default Page
	}
	
	// Get text when multiple args
	public String getString(String key, String...arg)
	{
		return getText(key, arg);
	}
	
	// Trim text
	public String trimString(String string) {
		
		try {
			if ( string != null )
				return string.trim();
		}
		catch (Exception e) {
			return string;
		}
		
		return string;
	}



	///////////////////////////////////////////////////////////////
	// Action Helper (by Kan)
	///////////////////////////////////////////////////////////////
//	private String methodTitle;	// a easy way to set page title (already comment in menu.jsp)
//	
//	public String getMethodTitle()
//	{
//		return getText(methodTitle);
//	}
//	public void setMethodTitleKey(String methodTitle)
//	{
//		this.methodTitle = methodTitle;
//	}
	
	public void addActionError(Exception e)
	{
		log4j.error( e );
		while ( true )
		{
			Exception cause = (Exception)e.getCause();
			if ( cause == null )
			{
				addActionError( getString ("Error: {0}", e.getMessage() ) );
				log4j.error( getString( "Error: {0}", e.getMessage() ) );
				break;
			}
			else
			{
				e = cause;
			}
		}
	}
	///////////////////////////////////////////////////////////////
	// End Action Helper
	///////////////////////////////////////////////////////////////



	///////////////////////////////////////////////////////////////
	// HTML Helper
	///////////////////////////////////////////////////////////////
	public String getJsScriptPath()
	{
		return getServletRequest().getContextPath() + "/scripts/js/";
	}
	
	public String getCssScriptPath()
	{
		return getServletRequest().getContextPath() + "/scripts/css/";
	}
	
	public String getImagesPath()
	{
		return getServletRequest().getContextPath() + "/images/";
	}
	
	public String getRealPath()
	{
		return getServletContext().getRealPath("/");
	}
	///////////////////////////////////////////////////////////////
	// End HTML Helper
	///////////////////////////////////////////////////////////////



	///////////////////////////////////////////////////////////////
	// Pagination
	///////////////////////////////////////////////////////////////
	private Pagination page;
	private int defaultInterval = 20;
	
	private String pageHeaderKey = "list.paging.header";
	private String prevNameKey = "list.paging.prev";
	private String nextNameKey = "list.paging.next";
	private String firstNameKey = "list.paging.first";
	private String lastNameKey = "list.paging.last";
	
	public void loadPagination(int total)
	{
		this.loadPagination(total, defaultInterval);
	}
	
	public void loadPagination(int total, int interval)
	{
		if (page == null)
		{
			page = new Pagination();
		}
		page.loadPagination(total, interval, getText(pageHeaderKey), getText(prevNameKey), getText(nextNameKey), getText(firstNameKey), getText(lastNameKey));
	}

	public Pagination getPage()
	{
		return page;
	}
	public void setPage(Pagination page)
	{
		this.page = page;
	}
	///////////////////////////////////////////////////////////////
	// End Pagination
	///////////////////////////////////////////////////////////////



	///////////////////////////////////////////////////////////////
	// Attachment
	///////////////////////////////////////////////////////////////
	// attachment
	public String getAttachmentInput()
	{

		Long now = System.currentTimeMillis();

		StringBuilder sb = new StringBuilder();

		sb.append(HTMLHelper.getDiv("",
				"attachmentFormDiv" + Long.toString(now)));

		sb.append("<script>" + "$(function(){" + "$('#attachmentFormDiv"
				+ Long.toString(now) + "').load('" + servletRequest.getContextPath()
				+ "/others/attachment!attachmentInput.action"
				+ "', {" + "'attachmentListId' : '" + attachmentListId + "',"
				+ "'attachmentType' : '" + this.attachmentType.toString() + "'"
				+ "});" + "});" + "</script>");

		return sb.toString();
	}

	// attachment
	public String getAttachmentReadonly()
	{
		Long now = System.currentTimeMillis();

		StringBuilder sb = new StringBuilder();

		sb.append(HTMLHelper.getDiv("",
				"attachmentFormDiv" + Long.toString(now)));

		sb.append("<script>" + "$(function(){" + "$('#attachmentFormDiv"
				+ Long.toString(now) + "').load('" + servletRequest.getContextPath()
				+ "/others/attachment!attachmentReadonly.action"
				+ "', {" + "'attachmentListId' : '" + attachmentListId + "',"
				+ "'attachmentType' : '" + this.attachmentType.toString() + "'"
				+ "});" + "});" + "</script>");

		return sb.toString();
	}

	private Long attachmentListId = null;
	private AttachmentType attachmentType;
	private List<Long> addAttachments;
	private List<Long> delAttachments;
	private Long selectedImageId;

	public Long getAttachmentListId()
	{
		return attachmentListId;
	}

//	public void setAttachmentListId(Long attachmentListId)
//	{
//		this.attachmentListId = attachmentListId;
//	}

	public void setBaseActionAttachmentListId(Long attachmentListId)
	{
		this.attachmentListId = attachmentListId;
	}

	public List<Long> getAddAttachments()
	{
		return addAttachments;
	}

	public void setAddAttachments(List<Long> addAttachments)
	{
		this.addAttachments = addAttachments;
	}

	public List<Long> getDelAttachments()
	{
		return delAttachments;
	}

	public void setDelAttachments(List<Long> delAttachments)
	{
		this.delAttachments = delAttachments;
	}

	public AttachmentType getAttachmentType()
	{
		return attachmentType;
	}

	public void setAttachmentType(AttachmentType attachmentType)
	{
		this.attachmentType = attachmentType;
	}

	public Long getSelectedImageId()
	{
		return selectedImageId;
	}

	public void setSelectedImageId(Long selectedImageId)
	{
		this.selectedImageId = selectedImageId;
	}
	///////////////////////////////////////////////////////////////
	// Attachment
	///////////////////////////////////////////////////////////////



	///////////////////////////////////////////////////////////////
	// Service
	///////////////////////////////////////////////////////////////
	private GeneralService generalService;
	private MasterService masterService;
	private OthersService othersService;
	private SystemService systemService;
	
	public GeneralService getGeneralService()
	{
		if (generalService == null)
			generalService = SpringUtils.getBean(ServiceFactory.GeneralService);
		
		return generalService;
	}

	public MasterService getMasterService()
	{
		if ( masterService == null )
			masterService = SpringUtils.getBean(ServiceFactory.MasterService);

		return masterService;
	}

	public OthersService getOthersService()
	{
		if ( othersService == null )
			othersService = SpringUtils.getBean(ServiceFactory.OthersService);

		return othersService;
	}

	public SystemService getSystemService()
	{
		if ( systemService == null )
			systemService = SpringUtils.getBean(ServiceFactory.SystemService);

		return systemService;
	}
	///////////////////////////////////////////////////////////////
	// End Service
	///////////////////////////////////////////////////////////////



	///////////////////////////////////////////////////////////////
	// Scheduler
	///////////////////////////////////////////////////////////////
	private ReceiptStatusSchedulerAsyncWorker receiptStatusSchedulerAsyncWorker;
	private SpecialEventSchedulerAsyncWorker specialEventSchedulerAsyncWorker;
	
	public ReceiptStatusSchedulerAsyncWorker getReceiptStatusSchedulerAsyncWorker()
	{
		if ( receiptStatusSchedulerAsyncWorker == null )
			receiptStatusSchedulerAsyncWorker = SpringUtils.getBean(ReceiptStatusScheduler.BEANNAME);
		
		return receiptStatusSchedulerAsyncWorker;
	} 
	
	public SpecialEventSchedulerAsyncWorker getSpecialEventIronSchedulerAsyncWorker()
	{
		if ( specialEventSchedulerAsyncWorker == null )
			specialEventSchedulerAsyncWorker = SpringUtils.getBean(SpecialEventIronScheduler.BEANNAME);
		
		return specialEventSchedulerAsyncWorker;
	}

	///////////////////////////////////////////////////////////////
	// End Scheduler
	///////////////////////////////////////////////////////////////






	///////////////////////////////////////////////////////////////
	// Printer printing through JasperReport
	///////////////////////////////////////////////////////////////
	public final String JASPER_FOLDER = "jasper_report/";
	
	public void printUnderWindowDriver(String reportFilePath, Map parameters,
			List dataSourceList, int numOfCopy, String printerName)
			throws Exception
	{
		log4j.info("printUnderWindowDriver: printerName = " + printerName);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(reportFilePath,
				parameters, new JRBeanCollectionDataSource(dataSourceList));
		// JasperPrint jasperPrint =
		// JasperFillManager.fillReport(reportFilePath, parameters, new
		// JREmptyDataSource()); //For Empty Use

		// Set in IReport
		// jasperPrint.setPageWidth(684);
		// jasperPrint.setPageHeight(792);
		// jasperPrint.setBottomMargin(0);
		// jasperPrint.setLeftMargin(0);
		// jasperPrint.setRightMargin(0);
		// jasperPrint.setTopMargin(0);
		// Set in IReport

		PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
		// printRequestAttributeSet.add( MediaSizeName.ISO_A4 ); //Set in IReport
		// MediaSizeName mediaSizeName = MediaSize.findMedia(9.5f, 11f, MediaPrintableArea.INCH); //Set in IReport
		// printRequestAttributeSet.add(mediaSizeName); //Set in IReport
		// printRequestAttributeSet.add( //Set in IReport
		// new MediaPrintableArea(0, 0, 241, 279, MediaPrintableArea.MM)
		// );

		printRequestAttributeSet.add(new Copies(numOfCopy));

		PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		printServiceAttributeSet.add(new PrinterName(printerName, null));

		JRPrintServiceExporter exporter = new JRPrintServiceExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(
				JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET,
				printRequestAttributeSet);
		exporter.setParameter(
				JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET,
				printServiceAttributeSet);
		exporter.setParameter(
				JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG,
				Boolean.FALSE);
		exporter.setParameter(
				JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG,
				Boolean.FALSE);
		exporter.exportReport();
	}
	
	




	///////////////////////////////////////////////////////////////
	// 
	///////////////////////////////////////////////////////////////
	private BeansFactoryApplication beansFactoryApplication;
	private BeansFactorySystem beansFactorySystem;
	
	public BeansFactoryApplication getBeansFactoryApplication()
	{
		if ( beansFactoryApplication == null )
			beansFactoryApplication = SpringUtils.getBean(BeansFactoryApplication.BEANNAME);
		
		return beansFactoryApplication;
	}
	
	public BeansFactorySystem getBeansFactorySystem()
	{
		if ( beansFactorySystem == null )
			beansFactorySystem = SpringUtils.getBean(BeansFactorySystem.BEANNAME);
		
		return beansFactorySystem;
	}
	
	
	
	///////////////////////////////////////////////////////////////
	// 
	///////////////////////////////////////////////////////////////



	///////////////////////////////////////////////////////////////
	// Other
	///////////////////////////////////////////////////////////////
	private String tilesKey = "";
	private String currentMethodName = "";		//Current called method
	private Map<String, Object> session;
	private HttpServletResponse servletResponse;
	private HttpServletRequest servletRequest;
	private ServletContext servletContext;
	
	public String getTilesKey()
	{
		return tilesKey;
	}
	public void setTilesKey(String tilesKey)
	{
		this.tilesKey = tilesKey;
	}
	public String getCurrentMethodName()
	{
		return currentMethodName;
	}
	public void setCurrentMethodName(String currentMethodName)
	{
		this.currentMethodName = currentMethodName;
	}
	
	//Don't allow "getSession().clear()"
	//This is because it would clear all data in the session and
	//there may be some data used by libraries or systems.
	public Map<String, Object> getSession()
	{
		return session;
	}
	public HttpServletResponse getServletResponse()
	{
		return servletResponse;
	}
	public HttpServletRequest getServletRequest()
	{
		return servletRequest;
	}
	public ServletContext getServletContext()
	{
		return servletContext;
	}

	@Override
	public void setSession(Map<String, Object> arg0)
	{
		this.session = arg0;
	}
	@Override
	public void setServletResponse(HttpServletResponse arg0)
	{
		this.servletResponse = arg0;
	}
	@Override
	public void setServletRequest(HttpServletRequest arg0)
	{
		this.servletRequest = arg0;
	}
	@Override
	public void setServletContext(ServletContext arg0)
	{
		this.servletContext = arg0;
	}
	///////////////////////////////////////////////////////////////
	// End Other
	///////////////////////////////////////////////////////////////
	
	
	
	
	//////////////////////////////////////////
	// Utility Method
	//////////////////////////////////////////
	private static final int CARD_ID_STARTING_INDEX = 3;
	
	public String computeCardId(String cardNum)
	{
		return cardNum.substring(CARD_ID_STARTING_INDEX);	// take 4th - 8th char
	}
}
