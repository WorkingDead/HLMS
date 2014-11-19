package security.interceptor;

import module.security.setting.SecuritySetting;
import security.KeyManager;
import utils.spring.SpringUtils;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

public class LicenseInterceptor extends MethodFilterInterceptor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8878672110330254406L;

	private static final String LICENSE_NOT_VALID_RESULT = "license.not.valid.result";
	
	boolean success = false;

	@Override
	public void destroy() {
		success = false;
	}

	@Override
	public void init() {
		success = false;
	}

	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		KeyManager keyman = new KeyManager(((SecuritySetting)SpringUtils.getBean("securitySetting")).getKeyPath());
		success = keyman.isValid() && !keyman.isExpiry();
		if(success)
		{
			return invocation.invoke();
		}
		return LICENSE_NOT_VALID_RESULT;
	}

}
