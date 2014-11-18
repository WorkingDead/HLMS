package web.actions.system;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;

import module.dao.system.Groups;
import module.dao.system.Users;

import module.service.all.SystemService;

import org.apache.log4j.Logger;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import utils.security.MD5;

import web.actions.BaseAction;
import web.actions.BaseActionSecurity;

import web.actions.system.UserForm;

import com.opensymphony.xwork2.validator.annotations.VisitorFieldValidator;

///////////////////////////////////////////////////
// @Results
//	name: a String return by a method, if a method return this 'name', then it will jump to the jsp page specified in tiles
//	location: a String specified in tiles.xml that tells which jsp will jump to
// 	type: specified 'tiles' means to search 'location' in tiles.xml
///////////////////////////////////////////////////
@Results({
	@Result(name="usersCreate", location="security.user.create", type="tiles"),
	@Result(name="usersUpdate", location="security.user.update", type="tiles"),
	@Result(name="usersList", location="security.user.list", type="tiles"),
	@Result(name="usersSearch", location="security.user.search", type="tiles"),

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
public class UserManagementAction extends BaseActionSecurity {

	private static final long serialVersionUID = 6940665503233628311L;
	private static final Logger log4j = Logger.getLogger(UserManagementAction.class);

	private String SESSION_AWARE_USERS_FOR_UPDATE = "SESSION_AWARE_USERS_FOR_UPDATE";

	// Store In DB
	private Users users;
	private List<Users> usersList; 		// Display Users Search Result
	// Store In DB

	// for submit form only
	private UserForm userForm;

	// input field
	private List<Groups> userGroupList;
	private List<EnableDisableStatus> enableDisableStatusList;
	
	// Session id
	private String sessionUsersPOKey = "users_%1$s_po";
	

	public UserManagementAction()
	{
	}

	@Override
	public String execute()
	{
		return null;
	}

	////////////////////////////////
	// Get JSP pages
	////////////////////////////////
	public String usersCreate()
	{
		initFormInputField();
		return "usersCreate";
	}

	public String usersList()
	{
		initListInputField();
		return "usersList";
	}
	

	////////////////////////////////
	// Retrieve DB data
	////////////////////////////////
	public String usersSearch()		//Have Pagination = true
	{
		return usersSearchImpl(true);
	}
	
	public String usersSearchImpl(boolean bool)
	{
		if ( users == null )
			users = new Users();
		
		SystemService service = this.getSystemService();
		CustomCriteriaHandler<Users> customerCriteriaHandler = new CustomCriteriaHandler<Users>() {

			@Override
			public void makeCustomCriteria(Criteria baseCriteria) {
				
				Criteria groupsCriteria = baseCriteria.createCriteria("groups").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
				
				if ( userForm != null && userForm.getGroups() != null && userForm.getGroups().getId() != null ) {
					groupsCriteria.add( Restrictions.eq("id", userForm.getGroups().getId()) );
				}

				filterAdminAndSuperAdminAndKioskGroupAndItsUsers(groupsCriteria);
			}
		};

		CustomLazyHandler<Users> customLazyHandler = new CustomLazyHandler<Users>() {
			
			@Override
			public void LazyList(List<Users> list) {
				Iterator<Users> it = list.iterator();
				while(it.hasNext())
				{
					Users users = it.next();
					users.getGroups().size();
					//TODO
				}
			}
		};

		if ( bool == true ) {
			//load paging
			int total = service.totalByExample(Users.class, users, customerCriteriaHandler);
			this.loadPagination(total);
			
			usersList = (ArrayList<Users>) service.findByExample(Users.class, users, this.getPage().getOffset(), this.getPage().getInterval(), customerCriteriaHandler, 
					customLazyHandler,
					Order.asc("username"));
		}
		else {
			usersList = (ArrayList<Users>) service.findByExample(Users.class, users, null, null, customerCriteriaHandler, 
					customLazyHandler,
					Order.asc("username"));
		}

		if ( usersList == null )
			usersList = new LinkedList<Users>();

		return "usersSearch";
	}

	public String usersUpdate()
	{
		initFormInputField();
		
		if ( users == null || users.getUsername() == null || users.getUsername().trim().length() <= 0 ) {
			
			addActionError( "usersUpdate: users is null or users.getUsername() is null or empty");
			log4j.error("usersUpdate: users is null or users.getUsername() is null or empty");

			return "jsonValidateResult";
		}
		
		String userName = users.getUsername();
		SystemService service = this.getSystemService();
		users = (Users) service.get(Users.class, userName);

		if ( users == null ) {
			
			addActionError( "usersUpdate: users is null");
			log4j.error("usersUpdate: users is null");
			
			return "jsonValidateResult";
		}



		Iterator<Groups> iterator = users.getGroups().iterator();
		while ( iterator.hasNext() ) {
			Groups groups = iterator.next();
			userForm = new UserForm();
			userForm.setGroups(groups);
			break;
		}		



		Map curSession = this.getSession();
		curSession.put(SESSION_AWARE_USERS_FOR_UPDATE, users);

		return "usersUpdate";
	}

	public String usersExport() {

		return "downloadExcel";
	}
	
	public InputStream getInputUserData() throws Exception {   

		usersSearchImpl(false);

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		HSSFWorkbook workbook = new HSSFWorkbook();   
		HSSFSheet sheet = workbook.createSheet("sheet1"); 
		HSSFRow row = sheet.createRow(0); 
		
		HSSFCell cell = row.createCell(0); 
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);   
		cell.setCellValue( new HSSFRichTextString( getText("security.users.group") ) );	
		
		cell = row.createCell(1); 
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);   
		cell.setCellValue( new HSSFRichTextString( getText("security.users.username") ) );	
		
		cell = row.createCell(2); 
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);   
		cell.setCellValue( new HSSFRichTextString( getText("security.users.status") ) );	
		
		
		for ( int i = 1; i <= usersList.size(); i++ ) {
			
			Users users = usersList.get(i-1);
			
			HSSFRow dataRow = sheet.createRow(i);
			
			HSSFCell dataCell = dataRow.createCell(0); 
			dataCell.setCellType(HSSFCell.CELL_TYPE_STRING);   
			dataCell.setCellValue( new HSSFRichTextString( users.getGroupName() ) );	
			
			dataCell = dataRow.createCell(1); 
			dataCell.setCellType(HSSFCell.CELL_TYPE_STRING);   
			dataCell.setCellValue( new HSSFRichTextString( users.getUsername() ) );	

			dataCell = dataRow.createCell(2); 
			dataCell.setCellType(HSSFCell.CELL_TYPE_STRING);   
			if ( users.getEnabled() )
				dataCell.setCellValue( new HSSFRichTextString( getText("label.enable") ) );	
			else
				dataCell.setCellValue( new HSSFRichTextString( getText("label.disable") ) );	
		}

		workbook.write( out );

		return new ByteArrayInputStream(out.toByteArray());
	}
	
	public String getDownloadFileName() {

		return "user.xls";   
	}   

	////////////////////////////////
	// Form data from JSP to DB
	////////////////////////////////
	public void validateCreate()
	{
		if ( users == null || users.getUsername().isEmpty() )
		{
			addActionError(String.format(getText("errors.custom.required"), getText("security.users.username")));
		}
		else if ( isUsersFieldValueAlreadyInDb("username", users.getUsername()) )
		{
			addActionError( String.format(getText("errors.custom.already.existed"), getText("security.users.username"), users.getUsername()) );
		}
		
		if ( users == null || users.getPassword() == null || users.getPassword().trim().isEmpty() )
		{
			addActionError(String.format(getText("errors.custom.required"), getText("security.users.password")));
		}
		else if (users.getConfirmPassword() == null || users.getConfirmPassword().trim().isEmpty() )
		{
			addActionError(String.format(getText("errors.custom.required"), getText("security.users.confirmPassword")));
		}
		else if (!users.getPassword().equals(users.getConfirmPassword()))
		{
			addActionError(String.format(getText("errors.custom.not.same"), getText("security.users.password"), getText("security.users.confirmPassword")));
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
			e.printStackTrace();
			
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
		
		if ( userForm != null && userForm.getGroups() != null && userForm.getGroups().getId() != null ) {

			Groups groups = getSystemService().get(Groups.class, userForm.getGroups().getId());
			users.getGroups().add(groups);
		}

		//MD5
		users.setPassword( MD5.getMD5( users.getPassword() ) );
		//MD5

		service.save(Users.class, users);
	}

	public void validateUpdate()
	{
		
		
		if (!users.getPassword().trim().isEmpty() &&
			 users.getConfirmPassword().trim().isEmpty() )
		{
			addActionError(String.format(getText("errors.custom.required"), getText("security.users.confirmPassword")));
		}
		else if (users.getPassword().trim().isEmpty() && !users.getConfirmPassword().trim().isEmpty())
		{
			addActionError(String.format(getText("errors.custom.required"), getText("security.users.newPassword")));
		}
		else if ( !users.getPassword().isEmpty() && 
					!users.getConfirmPassword().isEmpty() && 
					!users.getPassword().equals(users.getConfirmPassword()))
		{
			addActionError(String.format(getText("errors.custom.not.same"), getText("security.users.newPassword"), getText("security.users.confirmPassword")));
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
		Users usersToBeUpdate = (Users) curSession.get(SESSION_AWARE_USERS_FOR_UPDATE);

//		usersToBeUpdate.setCode( users.getCode() );
		if ( users.getPassword() == null || users.getPassword().trim().length() <= 0 ) {
		}
		else {
			usersToBeUpdate.setPassword( MD5.getMD5( users.getPassword() ) );
		}
		
		usersToBeUpdate.setLang( users.getLang() );
		usersToBeUpdate.setEnabled( users.getEnabled() );

		SystemService service = this.getSystemService();
		service.updateUsers(usersToBeUpdate, userForm);

		// Important! Delete the session var for edit (avoid error)
		curSession.put(SESSION_AWARE_USERS_FOR_UPDATE, null);
	}



	////////////////////////////////
	// (*) Common validation (*)
	// All need-to-validate-pages will need to pass this final validation
	////////////////////////////////
    public void validate()
    {
		if (users == null)
		{
			addActionError(getText(ErrorMessage_OperationError));
		}

		if (userForm == null)
		{
			addActionError(getText(ErrorMessage_OperationError));
		}
    	
//		if( getFieldErrors().size() > 0 ) {
//			addActionError( getText( ErrorMessage_SaveFail, new String[] { getText( ErrorMessage_DataError ) } ) );
//		}
    }



	// The validator must be placed before the get and set method. Get and set method must be placed together
    // Using validator in target class
    // if there is a import error, delete (message="") first, then import, and then add it back
    @VisitorFieldValidator(message="")					//Common Error Message
	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public List<Users> getUsersList() {
		return usersList;
	}

	public void setUsersList(List<Users> usersList) {
		this.usersList = usersList;
	}

	public List<Groups> getUserGroupList() {
		return userGroupList;
	}

	public void setUserGroupList(List<Groups> userGroupList) {
		this.userGroupList = userGroupList;
	}

	public List<EnableDisableStatus> getEnableDisableStatusList() {
		return enableDisableStatusList;
	}

	public void setEnableDisableStatusList(
			List<EnableDisableStatus> enableDisableStatusList) {
		this.enableDisableStatusList = enableDisableStatusList;
	}

	// The validator must be placed before the get and set method. Get and set method must be placed together
    // Using validator in target class
    // if there is a import error, delete (message="") first, then import, and then add it back
    @VisitorFieldValidator(message="")					//Common Error Message
	public UserForm getUserForm() {
		return userForm;
	}
	public void setUserForm(UserForm userForm) {
		this.userForm = userForm;
	}

	/////////////////
	//private function
	/////////////////
	private void initFormInputField() {
		
		enableDisableStatusList = Arrays.asList( BaseAction.EnableDisableStatus.values() );
		
		userGroupList = findUserGroupList();
	}
	
	private void initListInputField() {
		
		enableDisableStatusList = Arrays.asList( BaseAction.EnableDisableStatus.values() );
		
		userGroupList = findUserGroupList();
	}

	private List<Groups> findUserGroupList() {
		
		Groups searchGroups = new Groups();
		searchGroups.setEnabled(true);



		CustomCriteriaHandler<Groups> customerCriteriaHandler = new CustomCriteriaHandler<Groups>() {

			@Override
			public void makeCustomCriteria(Criteria baseCriteria) {

				filterAdminAndSuperAdminAndKioskGroupAndItsUsers(baseCriteria);
			}
		};	



		return getSystemService().findByExample(Groups.class, searchGroups, null, null, customerCriteriaHandler, null, null);
	}
}
