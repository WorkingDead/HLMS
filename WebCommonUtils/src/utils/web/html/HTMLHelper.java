package utils.web.html;

import org.apache.commons.lang.StringUtils;

public class HTMLHelper {

	private static final String htmlAttributePattern = "%1$s=\"%2$s\" ";

	public static String getHtmlElement(String elementName, String content, String id, String[] cssClass, String[]...attributes)
	{
		StringBuffer sb = new StringBuffer();

		sb.append("<" + elementName + " ");

		if(id!=null){
			//apply id
			sb.append(String.format(htmlAttributePattern, "id", id));
		}

		if(cssClass!=null && cssClass.length>0)
		{
			//apply css
			String css = StringUtils.join(cssClass, " ");
			sb.append(String.format(htmlAttributePattern, "class", css));
		}

		if(attributes!=null && attributes.length>0)
		{
			//apply other attribute
			for(int i=0;i<attributes.length;i++)
			{
				String[] attr = attributes[i];
				sb.append(String.format(htmlAttributePattern, attr[0], attr[1]));
			}
		}

		//apply content
		sb.append(">" + content);

		//apply end
		sb.append("</" + elementName + ">");

		return sb.toString();
	}

	////////////////////////////////////////
	//a href
	////////////////////////////////////////
	public static String getAHref(String name, String href){
		return getAHref(name, href, null, null);
	}

	public static String getAHref(String name, String href, String id){
		return getAHref(name, href, id, null);
	}

	public static String getAHref(String name, String href, String[] cssClass){
		return getAHref(name, href, null, cssClass);
	}

	public static String getAHref(String name, String href, String id, String[] cssClass)
	{
		return getHtmlElement("a", name, id, cssClass, new String[]{"href", href});
	}

	////////////////////////////////////////
	//div
	////////////////////////////////////////
	public static String getDiv(String content)
	{
		return getDiv(content, null, null);
	}

	public static String getDiv(String content, String id)
	{
		return getDiv(content, id, null);
	}

	public static String getDiv(String content, String[] cssClass)
	{
		return getDiv(content, null, cssClass);
	}

	public static String getDiv(String content, String id, String[] cssClass)
	{
		return getHtmlElement("div", content, id, cssClass);
	}

	////////////////////////////////////////
	//Span
	////////////////////////////////////////
	public static String getSpan(String content)
	{
		return getSpan(content, null, null);
	}

	public static String getSpan(String content, String id)
	{
		return getSpan(content, id, null);
	}

	public static String getSpan(String content, String[] cssClass)
	{
		return getSpan(content, null, cssClass);
	}

	public static String getSpan(String content, String id, String[] cssClass)
	{
		return getHtmlElement("span", content, id, cssClass);
	}
}
