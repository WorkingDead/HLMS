package web.actions.report;

/* For:
 * 1. Monthly Laundry Recv Report
 * 2. Monthly Laundry Dist Report */
public class ReportObject1
{
	private String receiptCode;
	private String receiptDate;
	private String receiptStatus;
	private String clothTypeCounter;
	private Integer receiptClothTotal;
	
	public String getReceiptCode()
	{
		return receiptCode;
	}
	public void setReceiptCode(String receiptCode)
	{
		this.receiptCode = receiptCode;
	}
	public String getReceiptDate()
	{
		return receiptDate;
	}
	public void setReceiptDate(String receiptDate)
	{
		this.receiptDate = receiptDate;
	}
	public String getReceiptStatus()
	{
		return receiptStatus;
	}
	public void setReceiptStatus(String receiptStatus)
	{
		this.receiptStatus = receiptStatus;
	}
	public String getClothTypeCounter()
	{
		return clothTypeCounter;
	}
	public void setClothTypeCounter(String clothTypeCounter)
	{
		this.clothTypeCounter = clothTypeCounter;
	}
	public Integer getReceiptClothTotal()
	{
		return receiptClothTotal;
	}
	public void setReceiptClothTotal(Integer receiptClothTotal)
	{
		this.receiptClothTotal = receiptClothTotal;
	}
}
