package web.actions.master;

import java.util.ArrayList;
import java.util.List;

import module.dao.BeansFactoryApplication;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.Staff;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import com.opensymphony.xwork2.validator.annotations.VisitorFieldValidator;

import web.actions.BaseActionMaster;

import web.actions.others.AttachmentAction.AttachmentType;

@Results({
	@Result(name="suggestedClothTypeJson", type="json", params={
//			"root", 				"clothTypeList",
			
			"includeProperties",
									"^clothTypeList\\[\\d+\\]\\.id, " +
									"^clothTypeList\\[\\d+\\]\\.name"
	}),	
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", "create, update"}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class ClothTypeAction extends BaseActionMaster
{
	private static final long serialVersionUID = -6435577056500191246L;
	private static final Logger log4j = Logger.getLogger(ClothTypeAction.class);
	
	private List<ClothType> clothTypeList;
	private ClothType clothType;
	private List<String> clothTypeNameList;
	
	private Staff staff;

	private String searchOrder;



	public ClothTypeAction()
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

		this.setTilesKey("cloth-type.new");
		return TILES;
	}
	
	public String getListPage()
	{
		initListInputField();

		this.setTilesKey("cloth-type.list");
		return TILES;
	}
	
	public String getEditPage()
	{
		clothType = this.getMasterService().get(ClothType.class, clothType.getId(), 
				
			new CustomLazyHandler<ClothType>() {

				@Override
				public void LazyObject(ClothType obj) {

					//for attachment use
					if(obj.getAttachmentList()!=null)
					{
						obj.getAttachmentList().getCreationDate();
						if(obj.getAttachmentList().getAttachments()!=null)
						{
							obj.getAttachmentList().getAttachments().size();
						}
					}
				}
			}
		);
		
		getSession().put(ClothTypeAction.class.getSimpleName(), clothType);
		
		// Attachment
		if( clothType.getAttachmentList() != null )
			this.setBaseActionAttachmentListId( clothType.getAttachmentList().getId() );
		// Attachment
		
		initFormInputField();

		this.setTilesKey("cloth-type.edit");
		return TILES;
	}

	private void initFormInputField()
	{
		// Only this cloth type can be added (defined by Kitz)
//		clothTypeNameList = new ArrayList<String>();
		
//		BeansFactoryApplication beansFactoryApplication = this.getBeansFactoryApplication();
//		String allClothTypeName = beansFactoryApplication.getAllClothTypeName();
//		String clothTypeNameArray[] = allClothTypeName.split(",");
//		for (int i = 0; i < clothTypeNameArray.length; i++)
//		{
//			clothTypeNameList.add(clothTypeNameArray[i]);
//		}
		
		
		// for attachment use
		this.setAttachmentType(AttachmentType.SoleImage);
	}

	private void initListInputField()
	{
		
	}



	///////////////////////////////////////////
	// Validation Area
	///////////////////////////////////////////
	public void validateCreate()
	{
		if (clothType == null || clothType.getName() == null || clothType.getName().isEmpty())
		{
			addActionError(String.format(getText("errors.custom.required"), getText("clothType.name")));
		}
		else
		{
			int total = 0;
			CustomCriteriaHandler<ClothType> customCriteriaHandler = new CustomCriteriaHandler<ClothType>() 
			{
				@Override
				public void makeCustomCriteria(Criteria criteria)
				{
					if (clothType != null)
					{
						if (clothType.getName() != null && !clothType.getName().isEmpty())
						{
							criteria.add(Restrictions.eq("name", clothType.getName()));
						}
					}
				}
			};
			total = this.getMasterService().totalByExample(ClothType.class, null, customCriteriaHandler);

			if ( total != 0 )
			{
				addActionError(String.format(getText("errors.custom.already.existed"), getText("clothType.clothType"), clothType.getName()));
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
		this.getMasterService().saveClothType( clothType, this.getAddAttachments(), this.getDelAttachments() );
	}



	public void validateUpdate()
	{
		if (clothType == null || clothType.getName() == null || clothType.getName().isEmpty())
		{
			addActionError(String.format(getText("errors.custom.required"), getText("clothType.name")));
		}
		else
		{
			ClothType tmpClothType = (ClothType) getSession().get( ClothTypeAction.class.getSimpleName() );
			if (tmpClothType.getName().equals(clothType.getName()))
			{
				// Nothing to do
			}
			else
			{
				int total = 0;
				CustomCriteriaHandler<ClothType> customCriteriaHandler = new CustomCriteriaHandler<ClothType>() 
				{
					@Override
					public void makeCustomCriteria(Criteria criteria)
					{
						if (clothType != null)
						{
							if (clothType.getName() != null && !clothType.getName().isEmpty())
							{
								criteria.add(Restrictions.eq("name", clothType.getName()));
							}
						}
					}
				};
				total = this.getMasterService().totalByExample(ClothType.class, null, customCriteriaHandler);

				if ( total != 0 )
				{
					addActionError(String.format(getText("errors.custom.already.existed"), getText("clothType.clothType"), clothType.getName()));
				}
			}
		}
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
		ClothType tempClothType = (ClothType) getSession().get( ClothTypeAction.class.getSimpleName() );
		
		tempClothType.setName( clothType.getName() );
		tempClothType.setDescription( clothType.getDescription() );
		tempClothType.setEnable( clothType.getEnable() );

		this.getMasterService().saveClothType( tempClothType, this.getAddAttachments(), this.getDelAttachments() );
	}

	////////////////////////////////
	// (*) Common validation (*)
	// All need-to-validate-pages will need to pass this final validation
	////////////////////////////////
    public void validate()
    {
		if ( clothType  == null )
			addActionError(getText(ErrorMessage_OperationError));
    	
//		if (getFieldErrors().size() > 0)
//		{
//			addActionError(getText(ErrorMessage_SaveFail, new String[] { getText(ErrorMessage_DataError) }));
//		}
    }
	///////////////////////////////////////////
	// Validation Area
	///////////////////////////////////////////



//	public String getKioskSearchResultPage()
//	{
//		
//	}
	
	public String getSearchResultPage()
	{
		searchClothType( true );

		if ( this.getTilesKey().trim().isEmpty() ) 
			this.setTilesKey("cloth-type.search");

		return TILES;
	}

	public String getKioskSuggestedClothTypeJsonResult() {
		
		searchOrder = "name";
		this.setTilesKey("suggestedClothTypeJson");
		
		return getSuggestedClothTypeJsonResult();
	}
	
	public String getSuggestedClothTypeJsonResult() {

		searchClothType( false );
		
		if ( this.getTilesKey().trim().isEmpty() ) 
			this.setTilesKey("suggestedClothTypeJson");
		
		return this.getTilesKey();
	}

	private CustomCriteriaHandler<ClothType> searchClothTypeCriteria() {
		
		CustomCriteriaHandler<ClothType> customCriteriaHandler = new CustomCriteriaHandler<ClothType>() 
		{
			@Override
			public void makeCustomCriteria(Criteria criteria)
			{
				if ( clothType != null )
				{
					if ( clothType.getName() != null && !clothType.getName().isEmpty() )
					{
						criteria.add(Restrictions.like("name", clothType.getName(), MatchMode.START));
					}
					
					if ( clothType.getDescription() != null && !clothType.getDescription().isEmpty() )
					{
						criteria.add(Restrictions.like("description", clothType.getDescription(), MatchMode.START));
					}

					if ( clothType.getEnable() != null )
					{
						criteria.add(Restrictions.eq("enable", clothType.getEnable()));
					}
				}
				
				if ( staff != null )
				{
					if ( staff.getId() != null && staff.getId() > 0 )
					{
						DetachedCriteria clothDetachedCriteria = DetachedCriteria.forClass(Cloth.class);
						DetachedCriteria staffDetachedCriteria = clothDetachedCriteria.createCriteria("staff");
						staffDetachedCriteria.add( Restrictions.eq("id", staff.getId()) );
						
						clothDetachedCriteria.setProjection( Projections.property("clothType.id") );
						
						criteria.add( Property.forName("clothSet").in(clothDetachedCriteria) );
					}
				}
			}
		};
		
		return customCriteriaHandler;
	}
	
	private void searchClothType(boolean paging) {
		
		//Be careful of "searchOrder" for duplicated column ordering request in this class
		CustomCriteriaHandler<ClothType> customCriteriaHandler = searchClothTypeCriteria();
		
		if ( paging == true )
			this.loadPagination( this.getMasterService().totalByExample(ClothType.class, null, customCriteriaHandler) );
		
		clothTypeList = this.getMasterService().findByExample(
				ClothType.class,
				null, 
				( paging == true ) ? getPage().getOffset() : null, 
				( paging == true ) ? getPage().getInterval() : null,
				customCriteriaHandler, 
				null,
				( searchOrder == null || searchOrder.isEmpty() ) ? Order.asc("id") : Order.asc(searchOrder) );
	}



	///////////////////////////////////////////
	// Getter and Setter
	///////////////////////////////////////////
	
	// The validator must be placed before the get and set method. Get and set method must be placed together
    // Using validator in target class
    // if there is a import error, delete (message="") first, then import, and then add it back
    @VisitorFieldValidator(message="")					//Common Error Message
	public ClothType getClothType()
	{
		return clothType;
	}

	public void setClothType(ClothType clothType)
	{
		this.clothType = clothType;
	}

	public List<ClothType> getClothTypeList()
	{
		return clothTypeList;
	}

	public void setClothTypeList(List<ClothType> clothTypeList)
	{
		this.clothTypeList = clothTypeList;
	}

	public List<String> getClothTypeNameList()
	{
		return clothTypeNameList;
	}

	public void setClothTypeNameList(List<String> clothTypeNameList)
	{
		this.clothTypeNameList = clothTypeNameList;
	}

	public String getSearchOrder() {
		return searchOrder;
	}
	public void setSearchOrder(String searchOrder) {
		this.searchOrder = searchOrder;
	}

	public Staff getStaff() {
		return staff;
	}
	public void setStaff(Staff staff) {
		this.staff = staff;
	}
}
