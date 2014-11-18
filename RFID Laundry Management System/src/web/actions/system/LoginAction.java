package web.actions.system;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.ParentPackage;

import web.actions.BaseAction;
import web.actions.BaseActionSystem;

@ParentPackage("struts-action-default")
public class LoginAction extends BaseActionSystem
{
	private static final Logger log4j = Logger.getLogger(LoginAction.class);
	private static final long serialVersionUID = -2983753790925776112L;
	
	private String error;
	
	public String form()
	{
		if (error != null && error.equals("1"))
		{
			addActionError(getText(BaseAction.ErrorMessage_WrongUsernameOrPassword));
		}

		this.setTilesKey("login.form");
		return TILES;
	}

	public String getError()
	{
		return error;
	}
	public void setError(String error)
	{
		this.error = error;
	}

//	public String login()
//	{
//		return SUCCESS;
//	}
}
