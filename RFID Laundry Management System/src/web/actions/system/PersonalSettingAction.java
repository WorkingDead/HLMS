package web.actions.system;

import java.util.Map;
import module.dao.system.Users;
import module.service.all.SystemService;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;
import com.opensymphony.xwork2.validator.annotations.VisitorFieldValidator;
import utils.security.MD5;
import web.actions.BaseActionSystem;

@Results({
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", "update"}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class PersonalSettingAction extends BaseActionSystem
{
	private static final long serialVersionUID = 4161557411091776894L;
	private static final Logger log4j = Logger.getLogger(PersonalSettingAction.class);

	private String SESSION_AWARE_USERS_FOR_UPDATE = "SESSION_AWARE_USERS_FOR_UPDATE";

	private Users users;
	private String oldPassword;

	public String getMainPage()
	{	
		if ( users == null || users.getUsername() == null || users.getUsername().trim().length() <= 0 ) {
			
			addActionError( "usersUpdate: users is null or users.getUsername() is null or empty") ;
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

		Map curSession = this.getSession();
		curSession.put(SESSION_AWARE_USERS_FOR_UPDATE, users);

		this.setTilesKey("system.personal.setting");
		return TILES;
	}
	
	
	
	
	///////////////////////////////////////////
	// Validation Area
	///////////////////////////////////////////
	public void validateUpdate()
	{
		if ( users == null ) {
			addActionError( getText( ErrorMessage_OperationError ) );
			return;
		}
			
		if ( ( oldPassword == null || oldPassword.trim().isEmpty() ) && ( users.getPassword() == null || users.getPassword().trim().isEmpty() ) )
		{
			
		}
		else
		{
			Map curSession = this.getSession();
			Users usersToBeUpdate = (Users) curSession.get(SESSION_AWARE_USERS_FOR_UPDATE);
			
//			if ( usersToBeUpdate == null ) {
//				addActionError( getText( ErrorMessage_OperationError ) );
//				return;
//			}
			
			if (oldPassword.isEmpty())
			{
				// if no old pw, don't change the pw and do not check 
			}
			if (MD5.getMD5(oldPassword).equals(usersToBeUpdate.getPassword()) == false)
			{
				addActionError(String.format(getText("errors.custom.invalid"), getText("security.users.oldPassword")));
			}
			else
			{
				if (users.getPassword().trim().isEmpty() && users.getConfirmPassword().trim().isEmpty())
				{
					addActionError(String.format(getText("errors.custom.required"), getText("security.users.newPassword")));
					addActionError(String.format(getText("errors.custom.required"), getText("security.users.confirmPassword")));
				}
				else if (!users.getPassword().trim().isEmpty() && users.getConfirmPassword().trim().isEmpty() )
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

		if ( users.getPassword() == null || users.getPassword().trim().isEmpty() ) {
		}
		else {
			usersToBeUpdate.setPassword( MD5.getMD5( users.getPassword() ) );
		}

		if ( users.getLang() != null )
			usersToBeUpdate.setLang( users.getLang() );

		this.getSystemService().save(Users.class, usersToBeUpdate);

		// Important! Delete the session var for edit (avoid error)
		curSession.put(SESSION_AWARE_USERS_FOR_UPDATE, null);
	}



	////////////////////////////////
	// (*) Common validation (*)
	// All need-to-validate-pages will need to pass this final validation
	////////////////////////////////
    public void validate()
    {
//		if (getFieldErrors().size() > 0)
//		{
//			addActionError(getText(ErrorMessage_SaveFail, new String[] { getText(ErrorMessage_DataError) }));
//		}
    }
	///////////////////////////////////////////
	// Validation Area
	///////////////////////////////////////////



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

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
}
