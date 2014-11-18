package web.actions.general;

import module.dao.general.Alert;
import module.dao.general.Alert.AlertType;

import module.service.all.GeneralService;

import org.apache.log4j.Logger;

import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import web.actions.BaseActionGeneral;


@Results({
	//only input need to define the result, any other tiles result can use setTilesKey and return TILES
	@Result(name="input", location="alert.list", type="tiles"),
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", "update"}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class AlertAction extends BaseActionGeneral
{
	private static final long serialVersionUID = -503650093769364280L;
	private static final Logger log4j = Logger.getLogger(AlertAction.class);
	private static final int DEFAULT_EXPIRY_PERIOD = 24;
	
	private Integer ironingExpiryPeriod;
	
	public String getListPage()
	{
		GeneralService service = this.getGeneralService();
		Alert alert = service.get(Alert.class, AlertType.IroningExpiryPeriod);
		
		if (alert == null)
		{
			this.ironingExpiryPeriod = DEFAULT_EXPIRY_PERIOD;
		}
		else
		{
			this.ironingExpiryPeriod = alert.getHours();
		}
		
		this.setTilesKey("alert.list");
		return TILES;
	}
	
	
	public void validateUpdate()
	{
		if (this.ironingExpiryPeriod == null)
		{
			this.addActionError(String.format(getText("errors.custom.invalid"), getText("alert.ironing.time.setting")));
		}
	}
	
	
	public String update()
	{
		try
		{
			updateImpl();
			addActionMessage( getText( SuccessMessage_SaveSuccess ) );
			log4j.info( getText( SuccessMessage_SaveSuccess ) );
		}
		catch (Exception e)
		{
			log4j.error( e );
			
			while ( true )
			{
				Exception cause = (Exception) e.getCause();
				if ( cause == null )
				{
					addActionError( getText (ErrorMessage_SaveFail, new String[]{e.getMessage()} ) );
					log4j.error( getText( ErrorMessage_SaveFail, new String[]{e.getMessage()} ) );
					break;
				}
				else
				{
					e = cause;
				}
			}
		}
		
		return "jsonValidateResult";
	}
	
	public void updateImpl() throws Exception
	{
		GeneralService service = this.getGeneralService();
		Alert alert = service.get(Alert.class, AlertType.IroningExpiryPeriod);
		if (alert == null)
		{
			alert = new Alert();
			alert.setId(AlertType.IroningExpiryPeriod);
		}
		alert.setHours(this.ironingExpiryPeriod);
		
		service.save(Alert.class, alert);
	}
//Washing
	public Integer getIroningExpiryPeriod()
	{
		return ironingExpiryPeriod;
	}
	public void setIroningExpiryPeriod(Integer ironingExpiryPeriod)
	{
		this.ironingExpiryPeriod = ironingExpiryPeriod;
	}

}
