package utils.taglib.displaytagpaging;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class DisplayTagPagingTag extends TagSupport {

	private int total;
	private int offset;
	private int interval;
	private int size = 8; //default value
	
	@Override
	public int doEndTag() throws JspException {

		try {
			JspWriter out = pageContext.getOut();

			int totalpage = (int) Math.ceil(((float) total) / interval);
			int current = offset / interval;
			
			StringBuffer sb = new StringBuffer();
			
			sb.append("<div class='displaytag_paging'>");

			if (totalpage>=1)
			{	 	
				if (totalpage>1)
				{
					sb.append("<span class=\"pagebanner\">"+ total + " items found, displaying "+ (offset+1) +" to " + (((offset + interval)>total)?total:(offset+interval)) + ".</span>");
					sb.append("<span class=\"pagelinks\">");

					if (current==0)
						sb.append("[First/Prev]");
					else
					{
						sb.append("[<a href=\"javascript:goTo(0)\";>First</a>/");
						sb.append("<a href=\"javascript:goTo("+(current-1)*interval+")\";>Prev</a>]");
					}
					if (totalpage>size)
						if (current<=size/2)
							for (int i=1;i<=size;i++)
							{
								if (i==current+1)
									sb.append("<strong>"+i+"</strong>");
								else
									sb.append("<a href=\"javascript:goTo("+(i-1)*interval+")\";>"+i+"</a>");
								if (i!=size) 
									sb.append(", ");
							}
						else
							if (current>totalpage-size/2)
								for (int i=totalpage-size+1;i<=totalpage;i++)
								{
									if (i==current+1)
										sb.append("<strong>"+i+"</strong>");
									else
										sb.append("<a href=\"javascript:goTo("+(i-1)*interval+")\";>"+i+"</a>");
									if (i!=totalpage) 
										sb.append(", ");
								}
							else	
								for (int i=current-size/2+1;i<=current+size/2;i++)
								{
									if (i==current+1)
										sb.append("<strong>"+i+"</strong>");
									else
										sb.append("<a href=\"javascript:goTo("+(i-1)*interval+")\";>"+i+"</a>");
									if (i!=current+size/2) 
										sb.append(", ");
								}
					else
						for (int i=1;i<=totalpage;i++)
						{
							if (i==current+1)
								sb.append("<strong>"+i+"</strong>");
							else
								sb.append("<a href=\"javascript:goTo("+(i-1)*interval+")\";>"+i+"</a>");
							if (i!=totalpage) 
								sb.append(", ");
						}

					if (current==totalpage-1)
						sb.append(" [Next/Last]");
					else
					{
						sb.append(" [<a href=\"javascript:goTo("+(current+1)*interval+")\";>Next</a>/");
						sb.append("<a href=\"javascript:goTo("+(totalpage-1)*interval+")\";>Last</a>]");
					}
					sb.append("</span>");
				}
				else
				{
					if (total==1)
						sb.append("<span class=\"pagebanner\">One item found.</span>");
					else
						sb.append("<span class=\"pagebanner\">"+ total + " items found, displaying all items.</span>");
					sb.append("<span class=\"pagelinks\"><strong>1</strong></span>");
				}
			}
			
			sb.append("</div>");
			
			out.println(sb.toString());

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new JspException(ex);
		}

		return SKIP_BODY;
	}

	//nothing do when body is empty
	@Override
	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}

	/**
	 * getting setting
	 * 
	 */

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * for Serializable
	 */
	private static final long serialVersionUID = 5999365806765987808L;

}
