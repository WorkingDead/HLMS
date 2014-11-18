package utils.spring;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringUtils {
	
	/**
	 * New method
	 * @param name
	 * @return
	 */
	public static <T> T getBean(String name)
	{
		WebApplicationContext context =
				WebApplicationContextUtils.getRequiredWebApplicationContext(
	                                    ServletActionContext.getServletContext()
	                        );
		@SuppressWarnings("unchecked")
		T obj = (T) context.getBean(name);
		return obj;
	}
	
	
	@Deprecated
	/**
	 * Use old method to get the spring bean
	 * Use this in scheduler
	 * @param name
	 * @return Object
	 */
	public static <T> T getAlternativeBean(String name)
	{
		BeanFactoryLocator bfl = SingletonBeanFactoryLocator.getInstance();
		BeanFactoryReference bf = bfl.useBeanFactory("application.setting");
		return (T) bf.getFactory().getBean(name);
	}
	
	@Deprecated
	/**
	 * Use old method to get the spring bean
	 * Use this in scheduler
	 * @param name
	 * @return Object
	 */
	public static <T> T getAlternativeSecurityBean(String name)
	{
		BeanFactoryLocator bfl = SingletonBeanFactoryLocator.getInstance();
		BeanFactoryReference bf = bfl.useBeanFactory("security.setting");
		return (T) bf.getFactory().getBean(name);
	}

}
