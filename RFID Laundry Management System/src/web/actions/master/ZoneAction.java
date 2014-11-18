package web.actions.master;

import java.util.List;

import module.dao.iface.CustomCriteriaHandler;
import module.dao.master.Zone;
import module.service.all.MasterService;

import org.apache.log4j.Logger;

import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.opensymphony.xwork2.validator.annotations.VisitorFieldValidator;

import web.actions.BaseActionMaster;

@Results({
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", "create, update"}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class ZoneAction extends BaseActionMaster
{
	private static final long serialVersionUID = 6693698727467372672L;
	private static final Logger log4j = Logger.getLogger(ZoneAction.class);
	
	private List<Zone> zoneList;
	private Zone zone;

	public ZoneAction()
	{
	}
	
	@Override
	public String execute()
	{
		return null;
	}
	
	public String getNewPage()
	{
		initFormInputField();

		this.setTilesKey("zone.new");
		return TILES;
	}
	
	public String getListPage()
	{
		initListInputField();

		this.setTilesKey("zone.list");
		return TILES;
	}
	
	public String getEditPage()
	{
		initFormInputField();
		
		MasterService masterService = this.getMasterService();
		zone = masterService.get(Zone.class, zone.getId());

		getSession().put( ZoneAction.class.getSimpleName(), zone );

		this.setTilesKey("zone.edit");
		return TILES;
	}

	private void initFormInputField()
	{

	}

	private void initListInputField()
	{

	}



	///////////////////////////////////////////
	// Validation Area
	///////////////////////////////////////////
	public void validateCreate()
	{
		if (zone == null || zone.getCode().isEmpty())
		{
			addActionError(String.format(getText("errors.custom.required"), getText("zone.code")));
		}
		else
		{
			int total = 0;
			CustomCriteriaHandler<Zone> customCriteriaHandler = new CustomCriteriaHandler<Zone>() 
			{
				@Override
				public void makeCustomCriteria(Criteria criteria)
				{
					if (zone != null)
					{
						if (zone.getCode() != null && !zone.getCode().isEmpty())
						{
							criteria.add(Restrictions.eq("code", zone.getCode()));
						}
					}
				}
			};
			total = this.getMasterService().totalByExample(Zone.class, null, customCriteriaHandler);

			if ( total != 0 )
			{
				addActionError(String.format(getText("errors.custom.already.existed"), getText("zone.code"), zone.getCode()));
			}
		}
	}
	
	public String create()
	{
		try
		{
			createImpl();
			addActionMessage( getText( SuccessMessage_SaveSuccess ) );
			log4j.info( getText( SuccessMessage_SaveSuccess ) );
		}
		catch (Exception e)
		{
			log4j.error( e );
			
			while ( true )
			{
				Exception cause = (Exception)e.getCause();
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
	
	public void createImpl() throws Exception
	{
		MasterService masterService = this.getMasterService();
		masterService.save(Zone.class, zone);
	}



	public void validateUpdate()
	{
	}
	
	public String update() throws Exception
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
				Exception cause = (Exception)e.getCause();
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
		// Hibernate Optimistic Locking is using, so you must get the existing object and then modify it
		
		//System.out.println("Update!");
		
		MasterService masterService = this.getMasterService();
		Zone tmpZone = (Zone)getSession().get( ZoneAction.class.getSimpleName() );

		//tmpZone.setCode( zone.getCode() );
		tmpZone.setDescription( zone.getDescription() );
		tmpZone.setEnable( zone.getEnable() );
		
		masterService.save(Zone.class, tmpZone);
	}
	
	////////////////////////////////
	// (*) Common validation (*)
	// All need-to-validate-pages will need to pass this final validation
	////////////////////////////////
    public void validate()
    {
		if (zone == null)
			addActionError(getText(ErrorMessage_OperationError));

//		if (getFieldErrors().size() > 0)
//		{
//			addActionError(getText(ErrorMessage_SaveFail, new String[] { getText(ErrorMessage_DataError) }));
//		}
    }
	///////////////////////////////////////////
	// Validation Area
	///////////////////////////////////////////



	public String getSearchResultPage()
	{
		CustomCriteriaHandler<Zone> customCriteriaHandler = new CustomCriteriaHandler<Zone>() 
		{
			@Override
			public void makeCustomCriteria(Criteria criteria)
			{
				if (zone != null)
				{
					if (zone.getCode() != null && !zone.getCode().isEmpty())
					{
						criteria.add(Restrictions.like("code", zone.getCode(), MatchMode.START));
					}
					
					if (zone.getDescription() != null && !zone.getDescription().isEmpty())
					{
						criteria.add(Restrictions.like("description", zone.getDescription(), MatchMode.START));
					}
					
				}
				
			}
		};
		
		MasterService masterService = this.getMasterService();
		this.loadPagination(masterService.totalByExample(Zone.class, null, customCriteriaHandler));
		
		zoneList = masterService.findByExample(
				Zone.class, 
				null,
				getPage().getOffset(), 
				getPage().getInterval(),
				customCriteriaHandler, 
				null, 
				Order.asc("code"));

		this.setTilesKey("zone.search");
		return TILES;
	}



	///////////////////////////////////////////
	// Getter and Setter
	///////////////////////////////////////////
	
	// The validator must be placed before the get and set method. Get and set method must be placed together
    // Using validator in target class
    // if there is a import error, delete (message="") first, then import, and then add it back
    @VisitorFieldValidator(message="")					//Common Error Message
	public Zone getZone()
	{
		return zone;
	}
	public void setZone(Zone zone)
	{
		this.zone = zone;
	}
    
	public List<Zone> getZoneList()
	{
		return zoneList;
	}
	public void setZoneList(List<Zone> zoneList)
	{
		this.zoneList = zoneList;
	}
}
