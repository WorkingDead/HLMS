package web.actions.kiosk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.Staff;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;
import utils.convertor.DateConverter;
import web.actions.BaseActionKiosk;

@Results({
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", "lost, found"}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class ClothLostAndFoundAction extends BaseActionKiosk
{
	private static final long serialVersionUID = 358182647886772783L;
	private static final Logger log4j = Logger.getLogger(ClothLostAndFoundAction.class);
	
	private ClothType clothType;
	private Cloth cloth;
	private Staff staff;
	
	private String kioskName;	// need this because of the menu links
	
	private static final String JASPER_RECEIPT_LOST = "jasper_report/receiptLost.jasper";
	private List<Object> mainReportList;
	
	public static enum LostFoundWay
	{
		lost,
		found;
	}

	public String getMainPage()
	{	
		this.kioskName = this.getServletRequest().getParameter(KIOSK_NAME);	// get Kiosk Name from URL
		//System.out.println( "get " + kioskName + "-Cloth-Lost-or-Found Page!" );
		
		this.setTilesKey("cloth-lost-and-found.main");
		return TILES;
	}

	///////////////////////////////////////////
	// Validation Area
	///////////////////////////////////////////
	public void validateLost()
	{
    	if ( staff == null || staff.getId() == null )
    	{
    		addActionError(String.format(getText("errors.custom.required.or"), getText("staff.code"), getText("staff.card")));
    	}
    	
		if (clothType == null || clothType.getId() == null)
		{
			addActionError(String.format(getText("errors.custom.required"), getText("clothType.clothType")));
		}
	}
	
	public String lost()
	{
		try
		{
			lostImpl();
			
			// 2. Print the receipt
			this.printReceipt();
			
			addActionMessage( getText( SuccessMessage_SaveSuccess ) );
			log4j.info( getText( SuccessMessage_SaveSuccess ) );
		}
		catch (Exception e)
		{
			log4j.error( e );
			
			while ( true )
			{
				Exception cause = (Exception)e.getCause();
				if ( cause == null )
				{
					addActionError( getText (ErrorMessage_SaveFail, new String[]{e.getMessage()} ) );
					log4j.error( getText( ErrorMessage_SaveFail, new String[]{e.getMessage()} ) );
					break;
				}
				else
				{
					e = cause;
				}
			}
		}
		
		return "jsonValidateResult";
	}
	
	public void lostImpl() throws Exception
	{
		if (cloth == null || cloth.getId() == null)
		{
			cloth = null;
		}
		else
		{
			cloth = this.getMasterService().get(Cloth.class, cloth.getId(), null);
		}

		staff = this.getMasterService().get(Staff.class, staff.getId(), 
				
				new CustomLazyHandler<Staff>()
				{
					@Override
					public void LazyObject(Staff staff)
					{
						staff.getDept().getId();
					}
				}
		);
		clothType = this.getMasterService().get(ClothType.class, clothType.getId());
		
//		cloth.setClothStatus( ClothStatus.Lost );
//		cloth.setModifiedBy( (Users)this.getSystemService().loadUserByUsername( BaseActionKiosk.KioskUserName ) );	//Wrong

		this.getMasterService().updateAndSaveClothLost(cloth, staff, clothType);
	}
	
	public void validateFound()
	{
    	if ( staff == null || staff.getId() == null )
    	{
    		addActionError(String.format(getText("errors.custom.required.or"), getText("staff.code"), getText("staff.card")));
    	}
		
		if (cloth == null || cloth.getId() == null)
		{
			addActionError(String.format(getText("errors.custom.required"), getText("cloth.code")));
		}
	}
	
	public String found()
	{
		try
		{
			foundImpl();
			addActionMessage( getText( SuccessMessage_SaveSuccess ) );
			log4j.info( getText( SuccessMessage_SaveSuccess ) );
		}
		catch (Exception e)
		{
			log4j.error( e );
			
			while ( true )
			{
				Exception cause = (Exception)e.getCause();
				if ( cause == null )
				{
					addActionError( getText (ErrorMessage_SaveFail, new String[]{e.getMessage()} ) );
					log4j.error( getText( ErrorMessage_SaveFail, new String[]{e.getMessage()} ) );
					break;
				}
				else
				{
					e = cause;
				}
			}
		}
		
		return "jsonValidateResult";
	}
	
	public void foundImpl() throws Exception
	{
		cloth = this.getMasterService().get(Cloth.class, cloth.getId());
		clothType = this.getMasterService().get(ClothType.class, clothType.getId());
		staff = this.getMasterService().get(Staff.class, staff.getId());
		
//		cloth.setClothStatus( ClothStatus.Using );
//		cloth.setModifiedBy( (Users)this.getSystemService().loadUserByUsername( BaseActionKiosk.KioskUserName ) );	//Wrong
		
		this.getMasterService().updateAndSaveClothFound(cloth, clothType, staff);
	}

	////////////////////////////////
	// (*) Common validation (*)
	// All need-to-validate-pages will need to pass this final validation
	////////////////////////////////
    public void validate()
    {
    	// The error msg here cannot be added before individual validation
    }
	///////////////////////////////////////////
	// Validation Area
	///////////////////////////////////////////



	private void printReceipt() throws Exception
	{
		/////////////////////////////////////////////
		// 1. Preparing receipt data for printing
		/////////////////////////////////////////////
		
		// iReport setting
		String reportFilePath = this.getRealPath() + JASPER_RECEIPT_LOST;
		Map parameters = null;
		
		String printerName = null;
		if (kioskName.equals(KioskName.kiosk1.toString()))
		{
			printerName = this.getBeansFactoryApplication().getKiosk1Printer();
		}
		else if (kioskName.equals(KioskName.kiosk2.toString()))
		{
			printerName = this.getBeansFactoryApplication().getKiosk2Printer();
		}
		else
		{
			printerName = "Invalid Kiosk Name so no printer selected!";
			System.out.println("printerName: " + printerName);
		}
		
		Calendar printDateTime = Calendar.getInstance();
		
		String receiptDate = DateConverter.format(printDateTime, DateConverter.DATE_FORMAT);
		String receiptTime = DateConverter.format(printDateTime, DateConverter.HOUR_MINUTE_FORMAT);
		String staffCode = staff.getCode();
		String staffNameCht = staff.getNameCht();
		String staffNameEng = staff.getNameEng();
		String dept = staff.getDept().getNameCht() + "\n" + staff.getDept().getNameEng();
		String clothTypeStr = this.clothType.getName();
		String clothCode = null;
		if (cloth != null && !cloth.getCode().isEmpty())
			clothCode = cloth.getCode();
		
		
		
		
		/////////////////////////////////////////////
		// 2. Send to Printer
		/////////////////////////////////////////////
		parameters = new HashMap();
		parameters.put("receiptDate", receiptDate);
		parameters.put("receiptTime", receiptTime);
		parameters.put("staffCode", staffCode);
		parameters.put("staffNameCht", staffNameCht);
		parameters.put("staffNameEng", staffNameEng);
		parameters.put("dept", dept);
		parameters.put("clothType", clothTypeStr);
		parameters.put("clothCode", clothCode);
		
		// add a dummy report-main-obj to defeat JasperReport bug
		this.mainReportList = new ArrayList<Object>();
		this.mainReportList.add(new Object());
		
		try
		{
			printUnderWindowDriver(reportFilePath, parameters, this.mainReportList, 1, printerName);
		}
		catch (Exception e)
		{
			log4j.error( e );
			e.printStackTrace();
			System.out.println("[Error] Print failed!");
		}
	}
	
    
    
	///////////////////////////////////////////
	// Getter and Setter
	///////////////////////////////////////////
	public ClothType getClothType()
	{
		return clothType;
	}
	public void setClothType(ClothType clothType)
	{
		this.clothType = clothType;
	}
	public Cloth getCloth()
	{
		return cloth;
	}
	public void setCloth(Cloth cloth)
	{
		this.cloth = cloth;
	}
	public Staff getStaff()
	{
		return staff;
	}
	public void setStaff(Staff staff)
	{
		this.staff = staff;
	}

	public String getKioskName()
	{
		return kioskName;
	}
	public void setKioskName(String kioskName)
	{
		this.kioskName = kioskName;
	}
}
