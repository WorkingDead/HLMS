package web.actions;

import org.apache.log4j.Logger;

public class BaseActionKiosk extends BaseAction
{
	private static final long serialVersionUID = 4902588821672278009L;
	private static final Logger log4j = Logger.getLogger(BaseActionKiosk.class);
	public static final String KIOSK_NAME = "kioskName";
	
	
	public static enum KioskName
	{
		kiosk1,
		kiosk2;
	}
	
	

	public BaseActionKiosk()
	{
		super();
	}

	//////////////////////////////////////////
	// Security
	//////////////////////////////////////////
	public static final String KioskUserName = "kiosk";
	public static final String KioskUserGroupName = "Kiosk";

	

	
}
