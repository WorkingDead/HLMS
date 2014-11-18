package module.interceptor;

import org.apache.log4j.Logger;

import web.actions.BaseAction;
import web.actions.BaseActionSecurity;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

public class SecurityAuthorityInterceptor extends MethodFilterInterceptor {

	private static final Logger log4j = Logger.getLogger(SecurityAuthorityInterceptor.class);
	
	private static final long serialVersionUID = -3033029736907871436L;

	//Security
	private Boolean BaseActionSecurity_SecurityResourceAuthoritySuccessPermission = true;
	private Boolean BaseActionSecurity_SecurityResourceAuthorityFailurePermission = false;

	private Boolean BaseAction_SecurityResourceAuthorityPermission = true;

	private Boolean NoExtend_SecurityResourceAuthorityPermission = false;
	//Security

	@Override																			//Deny Base
	protected String doIntercept(ActionInvocation invocation) throws Exception {		//Deny > Allow

		Object action = invocation.getAction();
		if ( action instanceof BaseActionSecurity ) {

			log4j.debug(action.getClass().getName() + " has extended BaseActionSecurity");
			
			if ( ((BaseActionSecurity)invocation.getAction()).hasUserGroupResourceAuthority() == true ) {
				
				if ( BaseActionSecurity_SecurityResourceAuthoritySuccessPermission != null && BaseActionSecurity_SecurityResourceAuthoritySuccessPermission ) {
					return invocation.invoke();
				}
				else {
					return denyAccess(invocation);
				}
			}
			else {
				
				if ( BaseActionSecurity_SecurityResourceAuthorityFailurePermission != null && BaseActionSecurity_SecurityResourceAuthorityFailurePermission ) {
					return invocation.invoke();
				}
				else {
					return denyAccess(invocation);
				}
				
			}
		}
		else if ( action instanceof BaseAction ) {

			log4j.debug(action.getClass().getName() + " has extended BaseAction");

			if ( BaseAction_SecurityResourceAuthorityPermission != null && BaseAction_SecurityResourceAuthorityPermission ) {
				return invocation.invoke();
			}
			else {
				return denyAccess(invocation);
			}
		}
		else {
			
			log4j.debug(action.getClass().getName() + " has not extended");

			if ( NoExtend_SecurityResourceAuthorityPermission != null && NoExtend_SecurityResourceAuthorityPermission ) {
				return invocation.invoke();
			}
			else {
				return denyAccess(invocation);
			}
		}
	}

	private String denyAccess(ActionInvocation invocation) {
		
		Object action = invocation.getAction();
		
		if( action instanceof BaseAction ) {
			addActionError(invocation, ((BaseAction)action).getText( BaseAction.ErrorMessage_NoAuthority ));
		}

		return "login.form";
	}
	
	private void addActionError(ActionInvocation invocation, String message) {
		Object action = invocation.getAction();
		if(action instanceof ValidationAware) {
			((ValidationAware) action).addActionError(message);
		}
	}
}
