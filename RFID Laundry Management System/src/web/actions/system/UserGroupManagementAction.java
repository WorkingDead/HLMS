package web.actions.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.system.GroupAuthorities;
import module.dao.system.Groups;
import module.dao.system.SecurityResource;
import module.dao.system.GroupAuthorities.GroupAuthoritiesType;

import module.service.all.SystemService;

import org.apache.log4j.Logger;

import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import web.actions.BaseAction;
import web.actions.BaseActionSecurity;

import web.actions.system.UserGroupForm;

import com.opensymphony.xwork2.validator.annotations.VisitorFieldValidator;

///////////////////////////////////////////////////
// @Results
//	name: a String return by a method, if a method return this 'name', then it will jump to the jsp page specified in tiles
//	location: a String specified in tiles.xml that tells which jsp will jump to
// 	type: specified 'tiles' means to search 'location' in tiles.xml
///////////////////////////////////////////////////
@Results({
	@Result(name="groupsCreate", location="security.user-group.create", type="tiles"),
	@Result(name="groupsUpdate", location="security.user-group.update", type="tiles"),
	@Result(name="groupsList", location="security.user-group.list", type="tiles"),
	@Result(name="groupsSearch", location="security.user-group.search", type="tiles"),

//	Not In Use Under Using AJAX
//  @Result(name="invalid.token", location="master-info.currency.currency-main", type="tiles"),	//Token For ReSubmit And Refresh
//  @Result(name=Action.INPUT, location="master-info.currency.currency-main", type="tiles")	//Validation (using AJAX response instead of this line)
})

//The sequence of interceptor is extremely important, DO NOT modify it!!!
@InterceptorRefs({
	@InterceptorRef("prefixStack"),													//Must be called

//	Call It If Necessary	
//	@InterceptorRef(value="conversionError"),										//Please have a look in struts.xml for key i18n
	
//	Not In Use Under Using AJAX
//	@InterceptorRef(value="token",params={"includeMethods","create, update"}),		//key = struts.messages.invalid.token
	
//	Custom Interceptor
//	@InterceptorRef(value="demoCustom"),
	
	@InterceptorRef(value="validation",params={"includeMethods","create, update"}),

	@InterceptorRef("postStack")													//Must be called
})

@ParentPackage("struts-action-default")												//Must be called
public class UserGroupManagementAction extends BaseActionSecurity {

	private static final long serialVersionUID = 6940665503233628311L;

	private static final Logger log4j = Logger.getLogger(UserGroupManagementAction.class);

	private String SESSION_AWARE_GROUPS_FOR_UPDATE = "SESSION_AWARE_GROUPS_FOR_UPDATE"; 

	//Store In DB
	private Groups groups;
	private List<Groups> groupsList;		//Display Groups Search Result
	//Store In DB

	//for submit form only
	private UserGroupForm userGroupForm;
	private List<Long> checkedResourceList;
	//for submit form only

	//display use
	//display use

	//input field
	private List<Groups> userGroupList;
	private List<SecurityResource> resourceList;
	private List<EnableDisableStatus> enableDisableStatusList;
	//input field

	//Search Criteria
	//Search Criteria
	
	//Session id
	private String sessionGroupsPOKey = "groups_%1$s_po";
	
	// I18N Key Fields
	// I18N Key Fields

	public UserGroupManagementAction()
	{
		// Register all DB mapped java bean properties to variable, rather than using clear string
//		registerDBMappedJavaBeanPropertiesToVariable("groups", Groups.class);
//		registerDBMappedJavaBeanPropertiesToVariable("groups", Groups.class);
//		registerDBMappedJavaBeanPropertiesToVariable("groups", Account.class);
		
		// Register all non-DB mapped 
//		registerNonDBMappedJavaBeanPropertiesToVariable("currency1", Currency.class);

		// Register the java bean properties with the its I18N key when they are not the same
		// For example, currency.exchangeRate	->		currency.default.exchange.rate
		// When they are the same, it is not necessary to register here
//		registerJavaBeanPropertiesToI18Nkey( getFullFieldName("groups", "accountNo"), "account.payment.number" );
//		registerJavaBeanPropertiesToI18Nkey( getFullFieldName("groups", "chequeNo"), "account.cheque.number" );
//		registerJavaBeanPropertiesToI18Nkey( getFullFieldName("groups", "paymentDate"), "account.payment.date" );
//		registerJavaBeanPropertiesToI18Nkey( getFullFieldName("groups", "paymentType"), "account.payment.type" );
	}

	@Override
	public String execute()
	{
		return null;
	}

	////////////////////////////////
	// Get JSP pages
	////////////////////////////////
	public String groupsCreate()
	{
		// this.setMethodTitleKey( getText("security.groups.page.title.new") ); //TODO i18n key
		
		initFormInputField();

		resourceList = getSystemService().findAll(SecurityResource.class, null, null, null, Order.asc("id"));

		return "groupsCreate";
	}

	public String groupsList()
	{
		// this.setMethodTitleKey( getText("security.groups.page.title.list") ); //TODO i18n key
		
		initListInputField();

		return "groupsList";
	}
	

	////////////////////////////////
	// Retrieve DB data
	////////////////////////////////
	public String groupsSearch()
	{
		if ( groups == null )
			groups = new Groups();
		
//Not Use In This Project	
//		if ( groups.getEnabled().equals(false) )
//			groups.setEnabled(null);
			
		SystemService service = this.getSystemService();

		CustomCriteriaHandler<Groups> customerCriteriaHandler = new CustomCriteriaHandler<Groups>() {

			@Override
			public void makeCustomCriteria(Criteria baseCriteria) {
				
				if ( groups != null ) {

				}

				filterAdminAndSuperAdminAndKioskGroupAndItsUsers(baseCriteria);
			}
		};		

		//load paging
		int total = service.totalByExample(Groups.class, groups, customerCriteriaHandler);
		this.loadPagination(total);
		
		groupsList = (ArrayList<Groups>) service.findByExample(Groups.class, groups, this.getPage().getOffset(), this.getPage().getInterval(), customerCriteriaHandler, 

				new CustomLazyHandler<Groups>() {
					
					@Override
					public void LazyList(List<Groups> list) {
						Iterator<Groups> it = list.iterator();
						while(it.hasNext())
						{
							Groups groups = it.next();

							//TODO
						}
					}
					
				}

				, Order.asc("id"));
		
		if ( groupsList == null )
			groupsList = new LinkedList<Groups>();
			
		return "groupsSearch";
	}

	public String groupsUpdate()
	{
		initFormInputField();

		if ( groups == null || groups.getId() == null ) {
			
			addActionError( "groupsUpdate: groups is null or groups.getUsername() is null or empty");
			log4j.error("groupsUpdate: groups is null or groups.getUsername() is null or empty");

			return "jsonValidateResult";
		}
		
		Long id = groups.getId();
		SystemService service = this.getSystemService();
		groups = (Groups) service.get(Groups.class, id, 
				
					new CustomLazyHandler<Groups>() {
						
						@Override
						public void LazyObject(Groups groups) {
			
							groups.getGroupAuthorities().size();
							
							for(Iterator<GroupAuthorities> it = groups.getGroupAuthorities().iterator();it.hasNext();) {
								it.next().getSecurityResource().size();
							}
						}
					}
				);

		if ( groups == null ) {
			
			addActionError( "groupsUpdate: groups is null" );
			log4j.error("groupsUpdate: groups is null");
			
			return "jsonValidateResult";
		}



		resourceList = getSystemService().findAll(SecurityResource.class, null, null, null, Order.asc("id"));
		for(Iterator<SecurityResource> it = resourceList.iterator();it.hasNext();) {
			
			SecurityResource securityResource = it.next();
			
			for(Iterator<GroupAuthorities> groupAuthoritiesIt = groups.getGroupAuthorities().iterator();groupAuthoritiesIt.hasNext();) {
				
				GroupAuthorities groupAuthorities = groupAuthoritiesIt.next();
				for(Iterator<SecurityResource> securityResourceIt = groupAuthorities.getSecurityResource().iterator();securityResourceIt.hasNext();) {
					
					SecurityResource bannedSecurityResource = securityResourceIt.next();
					if ( bannedSecurityResource.getId().equals( securityResource.getId() ) )
						securityResource.setChecked(false);
				}
			}
		}



		Map curSession = this.getSession();
		curSession.put(SESSION_AWARE_GROUPS_FOR_UPDATE, groups);

		return "groupsUpdate";
	}

	////////////////////////////////
	// Form data from JSP to DB
	////////////////////////////////
	public void validateCreate()
	{
		if ( groups == null || groups.getGroupName().isEmpty() )
		{
			addActionError(String.format(getText("errors.custom.required"), getText("security.groups.name")));
		}
		else
		{
			if ( isGroupsFieldValueAlreadyInDb("groupName", groups.getGroupName()) )
				addActionError( String.format(getText("errors.custom.already.existed"), getText("security.groups.name"), groups.getGroupName()) );
		}
	}

	public String create() throws Exception 
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
		
		return "jsonValidateResult";	// JSON - check this String in struts.xml
	}

	public boolean createImport() throws Exception
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
					addActionError( getText( ErrorMessage_SaveFail, new String[]{e.getMessage()} ) );
					log4j.error( getText( ErrorMessage_SaveFail, new String[]{e.getMessage()} ) );
					break;
				}
				else 
				{
					e = cause;
				}
			}
		}
		
		return true;
	}

	public void createImpl() throws Exception
	{
		SystemService service = this.getSystemService();

		//Temp Default = ROLE_USER
		GroupAuthoritiesType groupAuthoritiesType = GroupAuthoritiesType.ROLE_USER;
		//Temp Default = ROLE_USER

		List<Long> addResourceList = new LinkedList<Long>();
		if ( checkedResourceList != null ) {

			resourceList = getSystemService().findAll(SecurityResource.class, null, null, null, Order.asc("id"));			
			for(Iterator<SecurityResource> it = resourceList.iterator();it.hasNext();) {
				
				Long idInDB = it.next().getId();
				
				boolean sysFlag = false;
				for(Iterator<Long> iterator = checkedResourceList.iterator();iterator.hasNext();) {
				
					if ( idInDB.equals( iterator.next() ) )
						sysFlag = true;
				}
				
				if ( sysFlag  == false )
					addResourceList.add(idInDB);
			}
		}

		service.createGroups(groups, groupAuthoritiesType, addResourceList);
	}

	public void validateUpdate()
	{
		//Nothing to do now
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

	public boolean updateImport() throws Exception
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
		
		return true;
	}

	public void updateImpl() throws Exception
	{
		// Hibernate Optimistic Locking is using, so you must get the existing object and then modify it
		
		// get the currency obj from browser-scope-session and update it
		Map curSession = this.getSession();
		Groups groupsToBeUpdate = (Groups) curSession.get(SESSION_AWARE_GROUPS_FOR_UPDATE);

//		groupsToBeUpdate.setCode( groups.getCode() );
		groupsToBeUpdate.setEnabled( groups.getEnabled() );


		List<Long> addResourceList = new LinkedList<Long>();
		if ( checkedResourceList != null ) {

			resourceList = getSystemService().findAll(SecurityResource.class, null, null, null, Order.asc("id"));			
			for(Iterator<SecurityResource> it = resourceList.iterator();it.hasNext();) {
				
				Long idInDB = it.next().getId();
				
				boolean sysFlag = false;
				for(Iterator<Long> iterator = checkedResourceList.iterator();iterator.hasNext();) {
				
					if ( idInDB.equals( iterator.next() ) )
						sysFlag = true;
				}
				
				if ( sysFlag  == false )
					addResourceList.add(idInDB);
			}
		}



		SystemService service = this.getSystemService();
		service.updateGroups(groupsToBeUpdate, addResourceList);
//		service.save(Groups.class, groupsToBeUpdate);
		
		// Important! Delete the session var for edit (avoid error)
		curSession.put(SESSION_AWARE_GROUPS_FOR_UPDATE, null);
	}



	////////////////////////////////
	// (*) Common validation (*)
	// All need-to-validate-pages will need to pass this final validation
	////////////////////////////////
    public void validate()
    {
		if ( groups == null ) {
			addActionError( getText( ErrorMessage_OperationError ) );
		}
		
		if ( checkedResourceList == null ) {
			addActionError( getText( ErrorMessage_OperationError ) );
		}
		
//		if( getFieldErrors().size() > 0 ) {
//			addActionError( getText( ErrorMessage_SaveFail, new String[] { getText( ErrorMessage_DataError ) } ) );
//		}
    }

	// The validator must be placed before the get and set method. Get and set method must be placed together
    // Using validator in target class
    // if there is a import error, delete (message="") first, then import, and then add it back
    @VisitorFieldValidator(message="")					//Common Error Message
	public Groups getGroups() {
		return groups;
	}

	public void setGroups(Groups groups) {
		this.groups = groups;
	}

	public List<Groups> getGroupsList() {
		return groupsList;
	}

	public void setGroupsList(List<Groups> groupsList) {
		this.groupsList = groupsList;
	}

	public List<Groups> getUserGroupList() {
		return userGroupList;
	}

	public void setUserGroupList(List<Groups> userGroupList) {
		this.userGroupList = userGroupList;
	}

	public List<SecurityResource> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<SecurityResource> resourceList) {
		this.resourceList = resourceList;
	}

	public List<EnableDisableStatus> getEnableDisableStatusList() {
		return enableDisableStatusList;
	}

	public void setEnableDisableStatusList(
			List<EnableDisableStatus> enableDisableStatusList) {
		this.enableDisableStatusList = enableDisableStatusList;
	}

	public List<Long> getCheckedResourceList() {
		return checkedResourceList;
	}

	public void setCheckedResourceList(List<Long> checkedResourceList) {
		this.checkedResourceList = checkedResourceList;
	}

	// The validator must be placed before the get and set method. Get and set method must be placed together
    // Using validator in target class
    // if there is a import error, delete (message="") first, then import, and then add it back
    @VisitorFieldValidator(message="")					//Common Error Message
	public UserGroupForm getUserGroupForm() {
		return userGroupForm;
	}
	public void setUserGroupForm(UserGroupForm userGroupForm) {
		this.userGroupForm = userGroupForm;
	}
	
	/////////////////
	//private function
	/////////////////
	private void initFormInputField() {
		
		enableDisableStatusList = Arrays.asList( BaseAction.EnableDisableStatus.values() );
	}
	
	private void initListInputField() {
		
		enableDisableStatusList = Arrays.asList( BaseAction.EnableDisableStatus.values() );
	}
	
	
	
}
