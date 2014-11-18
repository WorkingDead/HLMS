package web.actions.master;

import java.util.Iterator;
import java.util.List;

import module.dao.general.Transaction;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.Staff;
import module.dao.master.Zone;

import org.apache.log4j.Logger;

import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.hibernate.Criteria;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import web.actions.BaseActionMaster;

@Results({
	@Result(name="suggestedClothJson", type="json", params={
			"includeProperties",	"^clothList\\[\\d+\\]\\.staff.dept.id, " + 
									"^clothList\\[\\d+\\]\\.staff.dept.nameEng, " +
									"^clothList\\[\\d+\\]\\.staff.dept.nameCht, " + 
									"^clothList\\[\\d+\\]\\.staff.id, " + 
									"^clothList\\[\\d+\\]\\.staff.code, " + 
									"^clothList\\[\\d+\\]\\.staff.nameEng, " +
									"^clothList\\[\\d+\\]\\.staff.nameCht, " + 
									"^clothList\\[\\d+\\]\\.id, " +
									"^clothList\\[\\d+\\]\\.code, " + 
									"^clothList\\[\\d+\\]\\.rfid, " + 
									"^clothList\\[\\d+\\]\\.clothType, " +
									"^clothList\\[\\d+\\]\\.clothStatus, " + 
									"^clothList\\[\\d+\\]\\.zone.id, " +
									"^clothList\\[\\d+\\]\\.zone.code"
	}),	
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", ""}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class ClothAction extends BaseActionMaster
{
	private static final long serialVersionUID = -6435577056500191246L;
	private static final Logger log4j = Logger.getLogger(ClothAction.class);
	
	private List<Cloth> clothList;
	private Cloth cloth;
	
	private Staff staff;
	private ClothType clothType;
	private Zone zone;

	private List<Transaction> transactionList;
	
	private String searchOrder;

	private Integer pageInterval = null;



	public String getKioskDetailPage()
	{
		this.setTilesKey("cloth-inquire.detail");
		
		return getEditPage();
	}
	
	public String getInventoryDetailPage()
	{
		this.setTilesKey("inventory.cloth.detail");
		
		return getEditPage();
	}
	
	public String getEditPage()		// Read Only
	{
		Long clothId = cloth.getId();
//		System.out.println("cloth: " + cloth.getId());
		
		CustomLazyHandler<Cloth> customLazyHandler = getClothDefaultLazyHandler();
		
		cloth = this.getMasterService().get(Cloth.class, clothId, customLazyHandler);

		// Attachment
		if( cloth.getClothType().getAttachmentList() != null )
			this.setBaseActionAttachmentListId( cloth.getClothType().getAttachmentList().getId() );
		// Attachment

		if ( this.getTilesKey().trim().isEmpty() ) 
			this.setTilesKey("cloth.edit");				//if it is empty, use back its original setting

		return TILES;
	}

	public CustomLazyHandler<Cloth> getClothDefaultLazyHandler() {
		return 
			new CustomLazyHandler<Cloth>() {
	
				@Override
				public void LazyObject(Cloth obj) {
					
					obj.getStaff().getCode();
					obj.getStaff().getDept().getNameEng();
					obj.getClothType().getName();
					
					if ( obj.getZone() != null )
						obj.getZone().getCode();

					ClothType clothType = obj.getClothType();
	
					//for attachment use
					if(clothType.getAttachmentList()!=null)
					{
						clothType.getAttachmentList().getCreationDate();
						if(clothType.getAttachmentList().getAttachments()!=null)
						{
							clothType.getAttachmentList().getAttachments().size();
						}
					}

					clothType.setSoleImageAttachment( getOthersService().getSoleImageAttachment(clothType) );
					//clothType.setImageAttachment( getOthersService().getImageAttachment(clothType) );
					//clothType.setSelectionImageAttachment( getOthersService().getSelectedAttachment(clothType) );
				}
			};
	}
	
	public String getKioskClothHistory()
	{
		this.setTilesKey("cloth-inquire.history");
		this.pageInterval = 3;
		
		return getClothHistory();
	}
	
	public String getInventoryClothHistory()
	{
		this.setTilesKey("inventory.cloth.history");
		
		return getClothHistory();
	}
	
	public String getClothHistory()
	{
		Long clothId = cloth.getId();
//		System.out.println("MyclothId: " + clothId);
		
		CustomCriteriaHandler<Transaction> customCriteriaHandler = new CustomCriteriaHandler<Transaction>()
		{
			@Override
			public void makeCustomCriteria(Criteria criteria)
			{
				criteria.add(Restrictions.eq("cloth.id", cloth.getId()));
			}
		};

		CustomLazyHandler<Transaction> customLazyHandler = new CustomLazyHandler<Transaction>()
		{
			@Override
			public void LazyList(List<Transaction> list)
			{
				Iterator<Transaction> it = list.iterator();
				while (it.hasNext())
				{
					Transaction obj = it.next();
					obj.getTransactionName().getValue();
					
					if (obj.getCreatedBy() != null)
						obj.getCreatedBy().getUsername();
					
					if (obj.getTransHandledByStaff() != null)
						obj.getTransHandledByStaff().getId();
					
					if (obj.getTransPickedByStaff() != null)
						obj.getTransPickedByStaff().getId();
				}
			}
		};
		
		if ( pageInterval == null )
			this.loadPagination(this.getGeneralService().totalByExample(Transaction.class, null, customCriteriaHandler));
		else
			this.loadPagination(this.getGeneralService().totalByExample(Transaction.class, null, customCriteriaHandler), pageInterval);
			
		this.transactionList = this.getGeneralService().findByExample(
				Transaction.class,
				null, 
				getPage().getOffset(), 
				getPage().getInterval(),
				customCriteriaHandler, 
				customLazyHandler,
				Order.desc("creationDate"));
		
		if ( this.getTilesKey().trim().isEmpty() ) 
			this.setTilesKey("cloth.history");				//if it is empty, use back its original setting
		
		return TILES;
	}



	public String getKioskSearchResultPage()
	{
		this.setTilesKey("cloth-inquire.search");
		this.pageInterval = 7;
		
		return getSearchResultPage();
	}
	
	public String getSearchResultPage()
	{
		searchCloth( true, this.pageInterval );

		if ( this.getTilesKey().trim().isEmpty() ) 
			this.setTilesKey("cloth.search");			//if it is empty, use back its original setting

		return TILES;
	}

	public String getKioskSuggestedClothJsonResult()
	{
		this.setTilesKey("suggestedClothJson");
		
		return getSuggestedClothJsonResult();
	}
	
	public String getSuggestedClothJsonResult() {
		
		searchCloth( false, null );
		
		if ( this.getTilesKey().trim().isEmpty() ) 
			this.setTilesKey("suggestedClothJson");		//if it is empty, use back its original setting
		
		return this.getTilesKey();
	}
	
	private CustomCriteriaHandler<Cloth> searchClothCriteria(final boolean addOrders)
	{
		CustomCriteriaHandler<Cloth> customCriteriaHandler = new CustomCriteriaHandler<Cloth>() 
		{
			@Override
			public void makeCustomCriteria(Criteria criteria)
			{
				if (cloth != null)
				{
					if (cloth.getId() != null && cloth.getId() > 0)
					{
						criteria.add(Restrictions.eq("id", cloth.getId()));
					}
					
					if (cloth.getCode() != null && !cloth.getCode().isEmpty())
					{
						criteria.add(Restrictions.like("code", cloth.getCode(), MatchMode.START));
					}
					
					if (cloth.getClothStatus() != null)
					{
						criteria.add(Restrictions.eq("clothStatus", cloth.getClothStatus()));
					}
					
					if (cloth.getRfid() != null && !cloth.getRfid().isEmpty())
					{
						criteria.add(Restrictions.eq("rfid", cloth.getRfid()));
					}
					
					if (cloth.getEnable() != null)
					{
						criteria.add(Restrictions.eq("enable", cloth.getEnable()));
					}
				}
				
				Criteria clothTypeCriteria = null;
				if ( clothType != null )
				{
					if ( clothType.getId() != null && clothType.getId() > 0 )
					{
						clothTypeCriteria = criteria.createCriteria("clothType");
						clothTypeCriteria.add(Restrictions.eq("id", clothType.getId()));
					}
				}
				
				Criteria staffCriteria = null;
				Criteria deptCriteria = null;
				if ( staff != null )
				{
					staffCriteria = criteria.createCriteria("staff");
					
					if ( staff.getId() != null && staff.getId() > 0 )
					{
						staffCriteria.add(Restrictions.eq("id", staff.getId()));
					}
					
					if ( staff.getCode() != null && !staff.getCode().isEmpty())
					{
						staffCriteria.add(Restrictions.like("code", staff.getCode(), MatchMode.START));
					}
					
					if ( staff.getNameCht() != null && !staff.getNameCht().isEmpty())
					{
						staffCriteria.add(Restrictions.like("nameCht", staff.getNameCht(), MatchMode.START));
					}
					
					if ( staff.getNameEng() != null && !staff.getNameEng().isEmpty())
					{
						staffCriteria.add(Restrictions.like("nameEng", staff.getNameEng(), MatchMode.START));
					}
					
					if ( staff.getDept() != null )
					{
						deptCriteria = staffCriteria.createCriteria("dept");
						if ( staff.getDept().getId() != null && staff.getDept().getId() > 0 )
						{
							staffCriteria.add(Restrictions.eq("dept.id", staff.getDept().getId()));
						}
					}
				}
				
				Criteria zoneCriteria = null;
				if (zone != null && zone.getId() != null)
				{
					zoneCriteria = criteria.createCriteria("zone");
					criteria.add(Restrictions.eq("zone.id", zone.getId()));
				}
				
				if (addOrders)
				{
					if (deptCriteria != null)
						deptCriteria.addOrder(Order.asc("nameEng"));
					
					if (staffCriteria != null)
						staffCriteria.addOrder(Order.asc("code"));
					
					criteria.addOrder(Order.asc("code"));
				}
			}
		};
		
		return customCriteriaHandler;
	}
	
	private CustomLazyHandler<Cloth> searchClothLazyHandler() {
		
		CustomLazyHandler<Cloth> customLazyHandler = new CustomLazyHandler<Cloth>() {

			@Override
			public void LazyList(List<Cloth> list) {
				
				Iterator<Cloth> it = list.iterator();
				while( it.hasNext() )
				{
					Cloth obj = it.next();
					
					obj.getClothType().getName();
					obj.getStaff().getCode();
					obj.getStaff().getDept().getNameEng();
					
					if (obj.getZone() != null)
						obj.getZone().getId();
				}
			}
		};
		
		return customLazyHandler;
	}
	
	private void searchCloth(boolean paging, Integer pageInterval)
	{
		//Be careful of "searchOrder" for duplicated column ordering request in this class
		CustomCriteriaHandler<Cloth> customCriteriaHandler = this.searchClothCriteria(false);
		CustomCriteriaHandler<Cloth> customCriteriaHandlerWithOrders = this.searchClothCriteria(true);
		CustomLazyHandler<Cloth> customLazyHandler = searchClothLazyHandler();
		
		if ( paging == true ) {
			if ( pageInterval == null )
				this.loadPagination( this.getMasterService().totalByExample(Cloth.class, null, customCriteriaHandler) );
			else
				this.loadPagination( this.getMasterService().totalByExample(Cloth.class, null, customCriteriaHandler), pageInterval );
		}
		
		clothList = this.getMasterService().findByExample(
				Cloth.class,
				null, 
				( paging == true ) ? getPage().getOffset() : null, 
				( paging == true ) ? getPage().getInterval() : null,
				customCriteriaHandlerWithOrders, 
				customLazyHandler,
				( searchOrder == null || searchOrder.isEmpty() ) ? null : Order.asc(searchOrder) );
	}



	///////////////////////////////////////////
	// Getter and Setter
	///////////////////////////////////////////
	public List<Cloth> getClothList() {
		return clothList;
	}

	public void setClothList(List<Cloth> clothList) {
		this.clothList = clothList;
	}

	public Cloth getCloth() {
		return cloth;
	}

	public void setCloth(Cloth cloth) {
		this.cloth = cloth;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public ClothType getClothType() {
		return clothType;
	}

	public void setClothType(ClothType clothType) {
		this.clothType = clothType;
	}

	public List<Transaction> getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(List<Transaction> transactionList) {
		this.transactionList = transactionList;
	}

	public String getSearchOrder() {
		return searchOrder;
	}

	public void setSearchOrder(String searchOrder) {
		this.searchOrder = searchOrder;
	}

	public Zone getZone()
	{
		return zone;
	}
	public void setZone(Zone zone)
	{
		this.zone = zone;
	}
	
}
