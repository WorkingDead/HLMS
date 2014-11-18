package module.interceptor;

import org.apache.log4j.Logger;

import web.actions.BaseAction;
import web.actions.BaseActionKiosk;
import web.actions.BaseActionKiosk.KioskName;
import web.actions.system.KioskHomeAction;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

public class KioskNameDetectionInterceptor extends MethodFilterInterceptor {

	private static final Logger log4j = Logger.getLogger(KioskNameDetectionInterceptor.class);
	
	private static final long serialVersionUID = 6159845309672188830L;

	@Override	
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		
		Object action = invocation.getAction();
		if ( action instanceof BaseActionKiosk || action instanceof KioskHomeAction ) {
			
			log4j.debug(action.getClass().getName() + " has extended BaseActionKiosk");
			
			BaseAction baseAction = (BaseAction)action;
			String kioskName = baseAction.getServletRequest().getParameter( BaseActionKiosk.KIOSK_NAME );



			//Change Requirement To Record The Kiosk Name Only
//			if ( checkKioskName(kioskName) == true ) {
//				return invocation.invoke();
//			}
//			else {
//				baseAction.setTilesKey("wrongKioskName");
//				return BaseAction.TILES;
//			}
			//Change Requirement To Record The Kiosk Name Only



			if ( checkKioskName(kioskName) == true ) {
				//Do Nothing
			}
			else {
				log4j.info("WARNING: kioskName = " + kioskName);
			}



			return invocation.invoke();
		}
		else {
			
			log4j.debug(action.getClass().getName() + " has not extended");
			
			return invocation.invoke();
		}
	}
	
	private boolean matchKioskNameEnum(String string) {
		
		for ( int i = 0; i < KioskName.values().length; i++ ) {
			
			if ( string.trim().equals( KioskName.values()[i].toString() ) ) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean checkKioskName(String string) {
		
		if ( string == null || string.trim().length() <= 0 ) {
			return false;
		}
		else if ( matchKioskNameEnum(string) == false ) {
			return false;
		}
		else {
			return true;
		}
	}
}
