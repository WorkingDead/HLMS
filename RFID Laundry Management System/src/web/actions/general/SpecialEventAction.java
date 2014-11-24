package web.actions.general;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import module.dao.general.SpecialEvent;
import module.dao.general.SpecialEvent.SpecialEventName;
import module.dao.general.SpecialEvent.SpecialEventStatus;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.Department;
import module.dao.master.Staff;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

import web.actions.BaseActionGeneral;

@Results({
	
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", "update"}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class SpecialEventAction extends BaseActionGeneral
{
	private static final long serialVersionUID = -7597811330004529219L;
	private static final Logger log4j = Logger.getLogger(SpecialEventAction.class);
	
	private List<SpecialEventName> specialEventNameList;
	private List<SpecialEventStatus> specialEventStatusList;
	private List<Department> deptList;
	private List<ClothType> clothTypeList;
	
	private SpecialEvent specialEvent;
	private Staff staff;
	private Cloth cloth;
	
	private Calendar dateFrom;
	private Calendar dateTo;
	
	private List<SpecialEvent> specialEventList;
	
	public String getListPage()
	{
		this.specialEventNameList = Arrays.asList(SpecialEventName.values());
		this.specialEventStatusList = Arrays.asList(SpecialEventStatus.values());
		this.deptList = this.getMasterService().findAll(Department.class, null, null, null, Order.asc("nameCht"));
		this.clothTypeList = getMasterService().findAll(ClothType.class, null, null, null, Order.asc("id"));
		
		this.setTilesKey("special.event.list");
		return TILES;
	}
	
	public String getSearchResultPage()
	{
		CustomCriteriaHandler<SpecialEvent> customCriteriaHandler = new CustomCriteriaHandler<SpecialEvent>()
		{
			@Override
			public void makeCustomCriteria(Criteria criteria)
			{
				if (dateFrom != null)
				{
					dateFrom.set(Calendar.HOUR_OF_DAY, dateFrom.getActualMinimum(Calendar.HOUR_OF_DAY));
					dateFrom.set(Calendar.MINUTE, dateFrom.getActualMinimum(Calendar.MINUTE));
					dateFrom.set(Calendar.SECOND, dateFrom.getActualMinimum(Calendar.SECOND));
					dateFrom.getTime();
					criteria.add(  Restrictions.ge("creationDate", dateFrom) );
				}

				if (dateTo != null)
				{
					dateTo.set(Calendar.HOUR_OF_DAY, dateTo.getActualMaximum(Calendar.HOUR_OF_DAY));
					dateTo.set(Calendar.MINUTE, dateTo.getActualMaximum(Calendar.MINUTE));
					dateTo.set(Calendar.SECOND, dateTo.getActualMaximum(Calendar.SECOND));
					dateTo.getTime();
					criteria.add(  Restrictions.le("creationDate", dateTo) );
				}
				
				
				if (specialEvent != null)
				{
					if (specialEvent.getSpecialEventName() != null)
					{
						criteria.add(Restrictions.eq("specialEventName", specialEvent.getSpecialEventName()));
					}
					
					if (specialEvent.getSpecialEventStatus() != null)
					{
						criteria.add(Restrictions.eq("specialEventStatus", specialEvent.getSpecialEventStatus()));
					}
				}
				
				
				if (staff != null)
				{
					Criteria staffCriteria = criteria.createCriteria("staff");
					
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
					
					if (staff.getDept() != null && staff.getDept().getId() != null)
					{
						staffCriteria.add(  Restrictions.eq("dept.id", staff.getDept().getId()) );
					}
				}
				
				Criteria clothCriteria = criteria.createCriteria("cloth", JoinType.LEFT_OUTER_JOIN);
				clothCriteria.add(Restrictions.or(Restrictions.ne("clothStatus", Cloth.ClothStatus.Void), Restrictions.isNull("clothStatus")));
				if (cloth != null)
				{
					if (cloth.getCode() != null && !cloth.getCode().isEmpty())
					{
						//Criteria clothCriteria = criteria.createCriteria("cloth");
						clothCriteria.add(Restrictions.like("code", cloth.getCode(), MatchMode.START));
					}
					
					if (cloth.getClothType() != null)
					{
						if (cloth.getClothType().getId() != null && cloth.getClothType().getId() > 0)
						{
//							Criteria clothTypeCriteria = criteria.createCriteria("cloth.clothType");
//							clothTypeCriteria.add(Restrictions.eq("id", cloth.getClothType().getId()));
							
							Criteria clothTypeCriteria = criteria.createCriteria("clothType");
							clothTypeCriteria.add(Restrictions.eq("id", cloth.getClothType().getId()));
						}
					}
				}
			}
		};

		CustomLazyHandler<SpecialEvent> customLazyHandler = new CustomLazyHandler<SpecialEvent>()
		{
			@Override
			public void LazyList(List<SpecialEvent> list)
			{
				Iterator<SpecialEvent> it = list.iterator();
				while (it.hasNext())
				{
					SpecialEvent obj = it.next();
					obj.getSpecialEventName().getValue();
					obj.getSpecialEventStatus().getValue();
					
					if (obj.getCloth() != null)
					{
						obj.getCloth().getId();
						
						if (obj.getCloth().getClothType() != null)
						{
							obj.getCloth().getClothType().getId();
						}
					}
					
					if (obj.getStaff() != null)
					{
						obj.getStaff().getId();
						
						if (obj.getStaff().getDept() != null)
						{
							obj.getStaff().getDept().getId();
						}
					}
				}
			}
		};
		
		
		
		this.loadPagination(this.getGeneralService().totalByExample(SpecialEvent.class, null, customCriteriaHandler));
		this.specialEventList = this.getGeneralService().findByExample(
				SpecialEvent.class,
				null, 
				getPage().getOffset(), 
				getPage().getInterval(),
				customCriteriaHandler, 
				customLazyHandler,
				Order.desc("creationDate"));
		
		this.setTilesKey("special.event.search");
		return TILES;
	}
	
	
	public String getEditPage()
	{
		Long eventId = specialEvent.getId();
		
		CustomLazyHandler<SpecialEvent> customLazyHandler = new CustomLazyHandler<SpecialEvent>()
		{
			@Override
			public void LazyObject(SpecialEvent se)
			{
				if (se.getStaff() != null)
				{
					se.getStaff().getId();
					se.getStaff().getDept().getId();
				}
				
				if (se.getCloth() != null)
				{
					se.getCloth().getId();
					se.getCloth().getClothType().getId();
				}
			}
		};
		
		this.specialEvent = this.getGeneralService().get(SpecialEvent.class, eventId, customLazyHandler);
		this.specialEventStatusList = Arrays.asList(SpecialEventStatus.values());
		
		this.setTilesKey("special.event.edit");
		return TILES;
	}
	
	public void validateUpdate()
	{
		if (specialEvent.getCloth() != null && !specialEvent.getCloth().getCode().isEmpty())
		{
			int numOfFound = this.getMasterService().totalByExample(Cloth.class, null, 
					
					new CustomCriteriaHandler<Cloth>()
					{
						@Override
						public void makeCustomCriteria(Criteria criteria)
						{
							Criteria staffCriteria = criteria.createCriteria("staff");
							staffCriteria.add(Restrictions.eq("id", specialEvent.getStaff().getId()));
							
							criteria.add(Restrictions.eq("code", specialEvent.getCloth().getCode() ));
						}
					}
			);
			
			if (numOfFound != 1)
			{
				addActionError(getText("errors.no.cloth.found"));
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
				Exception cause = (Exception) e.getCause();
				if ( cause == null )
				{
					addActionError( getText (ErrorMessage_SaveFail, new String[]{e.getMessage()} ) );
					e.printStackTrace();
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
		Cloth tmpCloth = null;
		SpecialEventStatus eventStatus = specialEvent.getSpecialEventStatus();
		if (specialEvent.getCloth() != null && !specialEvent.getCloth().getCode().isEmpty())
		{
			tmpCloth = this.getMasterService().findByExample(Cloth.class, null, null, null, 

					new CustomCriteriaHandler<Cloth>()
					{
						@Override
						public void makeCustomCriteria(Criteria criteria)
						{
							Criteria staffCriteria = criteria.createCriteria("staff");
							staffCriteria.add(Restrictions.eq("id", specialEvent.getStaff().getId()));
							
							criteria.add(Restrictions.eq("code", specialEvent.getCloth().getCode() ));
						}
					}, 
					
					null, Order.asc("id")).get(0);
		}
		
		Long eventId = this.specialEvent.getId();
		this.specialEvent = this.getGeneralService().get(SpecialEvent.class, eventId);
		this.specialEvent.setSpecialEventStatus(eventStatus);
		this.specialEvent.setCloth(tmpCloth);
		this.getGeneralService().save(SpecialEvent.class, this.specialEvent);
	}
	
	
	
	public List<SpecialEventName> getSpecialEventNameList()
	{
		return specialEventNameList;
	}
	public void setSpecialEventNameList(List<SpecialEventName> specialEventNameList)
	{
		this.specialEventNameList = specialEventNameList;
	}
	public List<SpecialEventStatus> getSpecialEventStatusList()
	{
		return specialEventStatusList;
	}
	public void setSpecialEventStatusList(
			List<SpecialEventStatus> specialEventStatusList)
	{
		this.specialEventStatusList = specialEventStatusList;
	}
	public List<Department> getDeptList()
	{
		return deptList;
	}
	public void setDeptList(List<Department> deptList)
	{
		this.deptList = deptList;
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
	public List<ClothType> getClothTypeList()
	{
		return clothTypeList;
	}
	public void setClothTypeList(List<ClothType> clothTypeList)
	{
		this.clothTypeList = clothTypeList;
	}
	public Staff getStaff()
	{
		return staff;
	}
	public void setStaff(Staff staff)
	{
		this.staff = staff;
	}
	public Cloth getCloth()
	{
		return cloth;
	}
	public void setCloth(Cloth cloth)
	{
		this.cloth = cloth;
	}
	public SpecialEvent getSpecialEvent()
	{
		return specialEvent;
	}
	public void setSpecialEvent(SpecialEvent specialEvent)
	{
		this.specialEvent = specialEvent;
	}
	public List<SpecialEvent> getSpecialEventList()
	{
		return specialEventList;
	}
	public void setSpecialEventList(List<SpecialEvent> specialEventList)
	{
		this.specialEventList = specialEventList;
	}
}
