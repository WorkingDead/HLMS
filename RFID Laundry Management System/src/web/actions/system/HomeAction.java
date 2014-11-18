package web.actions.system;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.ParentPackage;

import web.actions.BaseActionSystem;

@ParentPackage("struts-action-default")
public class HomeAction extends BaseActionSystem
{
	private static final long serialVersionUID = 2165912332660268905L;

	private static final Logger log4j = Logger.getLogger(HomeAction.class);

	public String execute()
	{
		//this.setMethodTitleKey("Home");

		this.setTilesKey("home");
		return TILES;	
	}
}
