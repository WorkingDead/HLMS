package web.actions.system;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;

import web.actions.BaseActionSystem;

@InterceptorRefs({
	@InterceptorRef("prefixStack"),

	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class KioskHomeAction extends BaseActionSystem
{
	private static final long serialVersionUID = -7094647563291243452L;
	
	private static final Logger log4j = Logger.getLogger(KioskHomeAction.class);

	public String execute()
	{
		this.setTilesKey("kioskHome");
		return TILES;	
	}
}