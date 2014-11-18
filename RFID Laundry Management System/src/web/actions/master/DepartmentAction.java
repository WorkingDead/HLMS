package web.actions.master;

import java.util.List;

import module.dao.iface.CustomCriteriaHandler;
import module.dao.master.Department;

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
	@Result(name="suggestedDepartmentJson", type="json", params={
//			"root", 				"departmentList",
			
			"includeProperties",
									"^departmentList\\[\\d+\\]\\.id, " +
									"^departmentList\\[\\d+\\]\\.nameEng, " +
									"^departmentList\\[\\d+\\]\\.nameCht"
	}),	
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", ""}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class DepartmentAction extends BaseActionMaster {

	private static final long serialVersionUID = -631532873827703854L;
	private static final Logger log4j = Logger.getLogger(DepartmentAction.class);
	
	private List<Department> departmentList;
	private Department department;

	private String searchOrder;



	public DepartmentAction()
	{
		
	}

	@Override
	public String execute()
	{
		return null;
	}



	public String getKioskSuggestedDepartmentJsonResult() {
		
		searchOrder = "id";
		this.setTilesKey("suggestedDepartmentJson");

		return getSuggestedDepartmentJsonResult();
	}
	
	public String getSuggestedDepartmentJsonResult() {

		searchDepartment( false );
		
		if ( this.getTilesKey().trim().isEmpty() ) 
			this.setTilesKey("suggestedDepartmentJson");
		
		return this.getTilesKey();
	}
	
	private CustomCriteriaHandler<Department> searchDepartmentCriteria() {
		
		CustomCriteriaHandler<Department> customCriteriaHandler = new CustomCriteriaHandler<Department>() 
		{
			@Override
			public void makeCustomCriteria(Criteria criteria)
			{
				if ( department != null )
				{
					if ( department.getNameEng() != null && !department.getNameEng().isEmpty() )
					{
						criteria.add(Restrictions.like("nameEng", department.getNameEng(), MatchMode.START));
					}
					
					if ( department.getNameCht() != null && !department.getNameCht().isEmpty() )
					{
						criteria.add(Restrictions.like("nameCht", department.getNameCht(), MatchMode.START));
					}

					if ( department.getEnable() != null )
					{
						criteria.add(Restrictions.eq("enable", department.getEnable()));
					}
				}
			}
		};
		
		return customCriteriaHandler;
	}
	
	private void searchDepartment(boolean paging) {
		
		//Be careful of "searchOrder" for duplicated column ordering request in this class
		CustomCriteriaHandler<Department> customCriteriaHandler = searchDepartmentCriteria();
		
		if ( paging == true )
			this.loadPagination( this.getMasterService().totalByExample(Department.class, null, customCriteriaHandler) );
		
		departmentList = this.getMasterService().findByExample(
				Department.class,
				null, 
				( paging == true ) ? getPage().getOffset() : null, 
				( paging == true ) ? getPage().getInterval() : null,
				customCriteriaHandler, 
				null,
				( searchOrder == null || searchOrder.isEmpty() ) ? Order.asc("id") : Order.asc(searchOrder) );
	}



	public List<Department> getDepartmentList() {
		return departmentList;
	}
	public void setDepartmentList(List<Department> departmentList) {
		this.departmentList = departmentList;
	}

	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}

	public String getSearchOrder() {
		return searchOrder;
	}
	public void setSearchOrder(String searchOrder) {
		this.searchOrder = searchOrder;
	}
}
