package web.actions.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import module.dao.general.Transaction;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.Department;
import module.dao.master.Staff;
import module.dao.master.Cloth.ClothStatus;
import module.dao.master.Zone;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import web.actions.BaseActionGeneral;

@Results({
	
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", ""}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class InventoryAction extends BaseActionGeneral
{
	private static final long serialVersionUID = -3893644141317073223L;
	private static final Logger log4j = Logger.getLogger(InventoryAction.class);
	
	private Staff staff;
	
	private List<Cloth> clothList;
	private Cloth cloth;
	
	private List<ClothType> clothTypeList;
	private ClothType clothType;
	
	private List<ClothStatus> clothStatusList;
	private List<Department> deptList;
	
	private List<Transaction> transactionList;
	
	private Calendar dateFrom;
	private Calendar dateTo;
	
	private List<Zone> zoneList;
	private Zone zone;
	
	
	public String getListPage()
	{
		this.deptList = this.getMasterService().findAll(Department.class, null, null, null, Order.asc("nameCht"));
		this.clothTypeList = getMasterService().findAll(ClothType.class, null, null, null, Order.asc("id"));
		this.clothStatusList = Cloth.getClothStatusListWithoutLost();
		this.zoneList = this.getMasterService().findAll(Zone.class, null, null, null, Order.asc("code"));
		this.setTilesKey("inventory.list");
		return TILES;
	}
	
	public String getListPageWithReadyStatus(){
		this.deptList = this.getMasterService().findAll(Department.class, null, null, null, Order.asc("nameCht"));
		this.clothTypeList = getMasterService().findAll(ClothType.class, null, null, null, Order.asc("id"));
		this.clothStatusList = new LinkedList<ClothStatus>( Arrays.asList(
				ClothStatus.Ready
			) );
		this.zoneList = this.getMasterService().findAll(Zone.class, null, null, null, Order.asc("code"));
		this.setTilesKey("inventory.distribute.list");
		
		return TILES;
	}
	
	public String getSearchResultPage()
	{
		// For counting num of record (contains order-by)
		CustomCriteriaHandler<Cloth> customCriteriaHandler = this.createCriteria(false);
		CustomCriteriaHandler<Cloth> customCriteriaHandlerWithOrders = this.createCriteria(true);
		
		CustomLazyHandler<Cloth> customLazyHandler = new CustomLazyHandler<Cloth>()
		{
			@Override
			public void LazyList(List<Cloth> list)
			{
				Iterator<Cloth> it = list.iterator();
				while (it.hasNext())
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
		
		this.loadPagination( this.getMasterService().totalByExample(Cloth.class, null, customCriteriaHandler) );
//		List<Order> hbnOrderList = Arrays.asList(Order.asc("staff.id"), Order.asc("code"));
		this.clothList = this.getMasterService().findByExampleWithOrders(
				Cloth.class,
				null, 
				getPage().getOffset(), 
				getPage().getInterval(),
				customCriteriaHandlerWithOrders,
				customLazyHandler,
				null
		);
		
		//Collections.sort(this.clothList, new ClothComparator());		//Not In This Way To Sort
		
		this.setTilesKey("inventory.search");
		return TILES;
	}
	
	
	
//	public String getDetailPage()
//	{
//		Long clothId = cloth.getId();
////		System.out.println("cloth: " + cloth.getId());
//		
//		CustomLazyHandler<Cloth> customLazyHandler = new CustomLazyHandler<Cloth>()
//		{
//			@Override
//			public void LazyObject(Cloth cloth)
//			{
//				cloth.getStaff().getId();
//				cloth.getStaff().getDept().getId();
//				cloth.getClothType().getId();
//				
//				// for attachment use
//				if (cloth.getClothType().getAttachmentList() != null)
//				{
//					cloth.getClothType().getAttachmentList().getCreationDate();
//					if (cloth.getClothType().getAttachmentList().getAttachments() != null)
//					{
//						cloth.getClothType().getAttachmentList().getAttachments().size();
//					}
//				}
//				
//				
//				if (cloth.getZone() != null)
//					cloth.getZone().getId();
//				
//			}
//		};
//		
//		this.cloth = this.getMasterService().get(Cloth.class, clothId, customLazyHandler);
//		this.cloth.getClothType().setSelectionImageAttachment( getOthersService().getSelectedAttachment(this.cloth.getClothType()) );
//		
//		// Attachment
//		if( this.cloth.getClothType().getAttachmentList() != null )
//			this.setBaseActionAttachmentListId( cloth.getClothType().getAttachmentList().getId() );
//		// Attachment
//		
//		
//		
//		this.setTilesKey("inventory.cloth.detail");
//		return TILES;
//	}
	
//	public String getClothHistory()
//	{
//		Long clothId = cloth.getId();
////		System.out.println("MyclothId: " + clothId);
//		
//		CustomCriteriaHandler<Transaction> customCriteriaHandler = new CustomCriteriaHandler<Transaction>()
//		{
//			@Override
//			public void makeCustomCriteria(Criteria criteria)
//			{
//				criteria.add(Restrictions.eq("cloth.id", cloth.getId()));
//			}
//		};
//
//		CustomLazyHandler<Transaction> customLazyHandler = new CustomLazyHandler<Transaction>()
//		{
//			@Override
//			public void LazyList(List<Transaction> list)
//			{
//				Iterator<Transaction> it = list.iterator();
//				while (it.hasNext())
//				{
//					Transaction obj = it.next();
//					obj.getTransactionName().getValue();
//					
//					if (obj.getCreatedBy() != null)
//						obj.getCreatedBy().getUsername();
//					
//					if (obj.getTransHandledByStaff() != null)
//						obj.getTransHandledByStaff().getId();
//					
//					if (obj.getTransPickedByStaff() != null)
//						obj.getTransPickedByStaff().getId();
//				}
//			}
//		};
//		
//		
//		
//		this.loadPagination(this.getGeneralService().totalByExample(Transaction.class, null, customCriteriaHandler));
//		this.transactionList = this.getGeneralService().findByExample(
//				Transaction.class,
//				null, 
//				getPage().getOffset(), 
//				getPage().getInterval(),
//				customCriteriaHandler, 
//				customLazyHandler,
//				Order.desc("creationDate"));
//		
//		this.setTilesKey("inventory.cloth.history");
//		return TILES;
//	}
	
	
	////////////////////////////////////////////////
	// Utility Method
	////////////////////////////////////////////////
	private CustomCriteriaHandler<Cloth> createCriteria(final boolean addOrders)
	{
		// For Data (contains order-by)
		final CustomCriteriaHandler<Cloth> customCriteriaHandlerWithOrders = new CustomCriteriaHandler<Cloth>()
		{
			@Override
			public void makeCustomCriteria(Criteria criteria)
			{
				if (cloth != null)
				{
					if (cloth.getCode() != null && !cloth.getCode().isEmpty())
					{
						criteria.add(Restrictions.like("code", cloth.getCode(), MatchMode.START));
					}

					if (cloth.getRfid() != null && !cloth.getRfid().isEmpty())
					{
						criteria.add(Restrictions.eq("rfid", cloth.getRfid()));
					}
					
					if (cloth.getClothStatus() != null)
					{
						criteria.add(Restrictions.eq("clothStatus", cloth.getClothStatus()));
					}
					
					if (dateFrom != null)
					{
						dateFrom.set(Calendar.HOUR_OF_DAY, dateFrom.getActualMinimum(Calendar.HOUR_OF_DAY));
						dateFrom.set(Calendar.MINUTE, dateFrom.getActualMinimum(Calendar.MINUTE));
						dateFrom.set(Calendar.SECOND, dateFrom.getActualMinimum(Calendar.SECOND));
						dateFrom.getTime();
						criteria.add(  Restrictions.ge("lastModifyDate", dateFrom) );
					}

					if (dateTo != null)
					{
						dateTo.set(Calendar.HOUR_OF_DAY, dateTo.getActualMaximum(Calendar.HOUR_OF_DAY));
						dateTo.set(Calendar.MINUTE, dateTo.getActualMaximum(Calendar.MINUTE));
						dateTo.set(Calendar.SECOND, dateTo.getActualMaximum(Calendar.SECOND));
						dateTo.getTime();
						criteria.add(  Restrictions.le("lastModifyDate", dateTo) );
					}
					
					if (zone != null && zone.getId() != null)
					{
						criteria.add(Restrictions.eq("zone.id", zone.getId()));
					}
				}

				if (clothType != null)
				{
					if (clothType.getId() != null && clothType.getId() > 0)
					{
						Criteria clothTypeCriteria = criteria.createCriteria("clothType");
						clothTypeCriteria.add(Restrictions.eq("id", clothType.getId()));
					}
				}

				Criteria staffCriteria = null;
				Criteria deptCriteria = null;
				if (staff != null)
				{
					staffCriteria = criteria.createCriteria("staff");

					if (staff.getCode() != null && !staff.getCode().isEmpty())
					{
						staffCriteria.add(Restrictions.like("code", staff.getCode(), MatchMode.START));
					}

					if (staff.getNameCht() != null && !staff.getNameCht().isEmpty())
					{
						staffCriteria.add(Restrictions.like("nameCht", staff.getNameCht(), MatchMode.START));
					}

					if (staff.getNameEng() != null && !staff.getNameEng().isEmpty())
					{
						staffCriteria.add(Restrictions.like("nameEng", staff.getNameEng(), MatchMode.START));
					}

					if (staff.getDept() != null)
					{
						deptCriteria = staffCriteria.createCriteria("dept");
						if (staff.getDept().getId() != null && staff.getDept().getId() > 0)
						{
							staffCriteria.add(Restrictions.eq("dept.id", staff.getDept().getId()));
						}
					}
				}
				
				if (addOrders)
				{
					deptCriteria.addOrder(Order.asc("nameEng"));
					staffCriteria.addOrder(Order.asc("code"));
					criteria.addOrder(Order.asc("code"));
				}
			}
		};
		
		return customCriteriaHandlerWithOrders;
	}
	
	
	public Staff getStaff()
	{
		return staff;
	}
	public void setStaff(Staff staff)
	{
		this.staff = staff;
	}
	public List<Cloth> getClothList()
	{
		return clothList;
	}
	public void setClothList(List<Cloth> clothList)
	{
		this.clothList = clothList;
	}
	public Cloth getCloth()
	{
		return cloth;
	}
	public void setCloth(Cloth cloth)
	{
		this.cloth = cloth;
	}
	public List<Department> getDeptList()
	{
		return deptList;
	}
	public void setDeptList(List<Department> deptList)
	{
		this.deptList = deptList;
	}
	public List<ClothType> getClothTypeList()
	{
		return clothTypeList;
	}
	public void setClothTypeList(List<ClothType> clothTypeList)
	{
		this.clothTypeList = clothTypeList;
	}
	public ClothType getClothType()
	{
		return clothType;
	}
	public void setClothType(ClothType clothType)
	{
		this.clothType = clothType;
	}
	public List<ClothStatus> getClothStatusList()
	{
		return clothStatusList;
	}
	public void setClothStatusList(List<ClothStatus> clothStatusList)
	{
		this.clothStatusList = clothStatusList;
	}
	public Calendar getDateFrom()
	{
		return dateFrom;
	}
	@TypeConversion(converter="utils.convertor.struts2.SimpleDateTimeToCalendarTypeConvertor")
	public void setDateFrom(Calendar dateFrom)
	{
		this.dateFrom = dateFrom;
	}
	public Calendar getDateTo()
	{
		return dateTo;
	}
	@TypeConversion(converter="utils.convertor.struts2.SimpleDateTimeToCalendarTypeConvertor")
	public void setDateTo(Calendar dateTo)
	{
		this.dateTo = dateTo;
	}
	public List<Transaction> getTransactionList()
	{
		return transactionList;
	}
	public void setTransactionList(List<Transaction> transactionList)
	{
		this.transactionList = transactionList;
	}
	public List<Zone> getZoneList()
	{
		return zoneList;
	}
	public void setZoneList(List<Zone> zoneList)
	{
		this.zoneList = zoneList;
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

class ClothComparator implements Comparator<Cloth>
{
	@Override
	public int compare(Cloth o1, Cloth o2)
	{
		String dept1 = o1.getStaff().getDept().getNameEng();
		String dept2 = o2.getStaff().getDept().getNameEng();
		int r1 = dept1.compareTo(dept2);
		if (r1 != 0)
		{
			return r1;
		}
		else
		{
			String staffCode1 = o1.getStaff().getCode();
			String staffCode2 = o2.getStaff().getCode();
			int r2 = staffCode1.compareTo(staffCode2);
			if (r2 != 0)
			{
				return r2;
			}
			else
			{
				String clothType1 = o1.getClothType().getName();
				String clothType2 = o2.getClothType().getName();
				int r3 = clothType1.compareTo(clothType2);
				if (r3 != 0)
				{
					return r3;
				}
				else
				{
					return o1.getCode().compareTo(o2.getCode());
				}
			}
		}
	}
}