package utils.web.paging;

import utils.web.html.HTMLHelper;

public class Pagination {
	
	public static final int DEFAULT_INTERVAL = 20;
	
	private static final int pageNumInterval = 10; 
	
	private static final String DEFAULT_PAGE_HEADER = "Page %1$d of %2$d";
	private static final String DEFAULT_PREV_NAME = "&lt;Prev";
	private static final String DEFAULT_NEXT_NAME = "Next&gt;";
	private static final String DEFAULT_FIRST_NAME = "First";
	private static final String DEFAULT_LAST_NAME = "Last";
	
	private int total;
	private int totalPage;
	private int interval;
	private int offset;
	private int page;
	private int currentPage;
	private String pageHeader;
	private String prevName;
	private String nextName;
	private String firstName;
	private String lastName;
	
	public void loadPagination(int total)
	{
		this.loadPagination(total, DEFAULT_INTERVAL, DEFAULT_PAGE_HEADER, DEFAULT_PREV_NAME, DEFAULT_NEXT_NAME, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
	}
	
	public void loadPagination(int total, int interval)
	{
		this.loadPagination(total, interval, DEFAULT_PAGE_HEADER, DEFAULT_PREV_NAME, DEFAULT_NEXT_NAME, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
	}

	//Old Method For Compatibility
	public void loadPagination(int total, int interval, String pageHeader, String prevName, String nextName)
	{
		this.total = total;
		this.interval = interval;
		this.pageHeader = pageHeader;
		this.prevName = prevName;
		this.nextName = nextName;
		
		this.totalPage = (this.total / this.interval) + (this.total % this.interval==0?0:1);
		this.currentPage = this.offset==0?0:this.offset/this.interval;
	}
	
	public void loadPagination(int total, int interval, String pageHeader, String prevName, String nextName, String firstName, String lastName)
	{
		this.total = total;
		this.interval = interval;
		this.pageHeader = pageHeader;
		this.prevName = prevName;
		this.nextName = nextName;
		this.firstName = firstName;
		this.lastName = lastName;
		
		this.totalPage = (this.total / this.interval) + (this.total % this.interval==0?0:1);
		
		if ( this.offset == -1 ) {
			
			if ( this.page <= 0 )
				this.page = 0;
			else if ( this.page >= this.totalPage )
				this.page = this.totalPage - 1;
			else
				this.page = this.page - 1;
			
			this.offset = getPageOffset(this.page);
		}

		this.currentPage = this.offset==0?0:this.offset/this.interval;
	}
	
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
	
	public void setPage(String page) {

		try {
			setPage( Integer.parseInt( page ) );
		}
		catch (NumberFormatException e) {
			//Ignore
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public int getNextOffset() {
		return this.offset + this.interval;
	}
	
	public int getPrevOffset() {
		return this.offset - this.interval;
	}
	
	public int getNextPage() {
		return this.currentPage + 1;
	}
	
	public int getPrevPage() {
		return this.currentPage - 1;
	}
	
	public int getPageOffset(int page)
	{
		return this.interval * page;
	}
	
	public String getPageHeader() {
		return pageHeader;
	}

	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	public String getPrevName() {
		return prevName;
	}

	public void setPrevName(String prevName) {
		this.prevName = prevName;
	}

	public String getNextName() {
		return nextName;
	}

	public void setNextName(String nextName) {
		this.nextName = nextName;
	}

	public String getPagination()
	{
		return getPagination("ajaxSearchFormPage", "ajaxSearchJumpFormPage");
	}
	
	public String getPagination(String formPage, String jumpFormPage)
	{
		if(this.total==0)
		{
			return "";
		}
		
		StringBuffer sb = new StringBuffer();

		String header = "";
		if ( firstName != null && firstName.isEmpty() == false && lastName != null && lastName.isEmpty() == false ) {
			String currentpage = HTMLHelper.getHtmlElement("input", "", null, new String[]{jumpFormPage}, new String[]{"style", "width:30px"}, new String[]{"value", this.currentPage + 1 + ""});
			header = String.format(pageHeader, currentpage, this.totalPage);
		}
		else {
			header = String.format(pageHeader, this.currentPage + 1, this.totalPage);
		}

		sb.append(HTMLHelper.getDiv(header, new String[]{"alignCenterDiv"}));
		
		StringBuffer pageNumber = new StringBuffer();



		//First Link
		if ( firstName != null && firstName.isEmpty() == false ) {
			String first = HTMLHelper.getHtmlElement("a", firstName, null, new String[]{formPage}, new String[]{"href", "#"}, new String[]{"ref", "0"});
			pageNumber.append(first + " ");
		}

		//Previous Link
		String prev;
		if(this.getPrevOffset()<0)
		{
			prev = prevName;
		}
		else
		{
			prev = HTMLHelper.getHtmlElement("a", prevName, null, new String[]{formPage}, new String[]{"href", "#"}, new String[]{"ref", Integer.toString(this.getPrevOffset())});
		}
		
		pageNumber.append(prev + " ");
		
		//Page Link
		int startPageNum = 0;
		int endPage = pageNumInterval;
		
		if(this.totalPage<=pageNumInterval)
		{
			endPage = this.totalPage;
		}
		else
		{
			if(this.currentPage>this.totalPage - (pageNumInterval/2) - 1)
			{
				startPageNum = this.totalPage - pageNumInterval;
			}
			else if(this.currentPage>pageNumInterval/2)
			{
				startPageNum = this.currentPage - pageNumInterval/2;
			}
			else
			{
				startPageNum = 0;
			}
			endPage = startPageNum + pageNumInterval;
		}
		
		for(int i=startPageNum;i<endPage;i++)
		{
			String pageNum = Integer.toString(i+1);
			if(i==this.currentPage)
			{
				String span = HTMLHelper.getSpan(pageNum, new String[]{"currentPageNum"});
				pageNumber.append(HTMLHelper.getHtmlElement("a", span, null, new String[]{formPage}, new String[]{"href", "#"}, new String[]{"ref", Integer.toString(this.getPageOffset(i))}) + " ");
			}
			else
			{
				pageNumber.append(HTMLHelper.getHtmlElement("a", pageNum, null, new String[]{formPage}, new String[]{"href", "#"}, new String[]{"ref", Integer.toString(this.getPageOffset(i))}) + " ");
			}
		}

		//Next Link
		String next;
		
		if(this.getNextOffset()>=this.total)
		{
			next = nextName;
		}
		else
		{
			next = HTMLHelper.getHtmlElement("a", nextName, null, new String[]{formPage}, new String[]{"href", "#"}, new String[]{"ref", Integer.toString(this.getNextOffset())});
		}
		 
		pageNumber.append(next + " ");

		//Last Link
		if ( lastName != null && lastName.isEmpty() == false ) {
			String last = HTMLHelper.getHtmlElement("a", lastName, null, new String[]{formPage}, new String[]{"href", "#"}, new String[]{"ref", Integer.toString(this.getPageOffset(this.totalPage-1))});
			pageNumber.append(last + " ");
		}



		sb.append(HTMLHelper.getDiv(pageNumber.toString(), new String[]{"alignCenterDiv"}));
		
		return sb.toString();
	}
}
