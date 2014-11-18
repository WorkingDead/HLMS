package web.actions.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletResponse;
import module.dao.general.HistoryCloth;
import module.dao.general.Receipt;
import module.dao.general.Receipt.ReceiptStatus;
import module.dao.general.Receipt.ReceiptType;
import module.dao.general.ReceiptPatternIron;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.Staff;
import module.dao.master.Cloth.ClothStatus;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import utils.convertor.DateConverter;
import web.actions.BaseActionGeneral;
import web.actions.form.ClothTypeCounter;

@Results({
	
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", "update, removeClothFromRollContainer"}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class ReceiptAction extends BaseActionGeneral implements ServletResponseAware
{
	private static final long serialVersionUID = -3009352733379813134L;
	private static final Logger log4j = Logger.getLogger(ReceiptAction.class);
	
	private List<ReceiptType> receiptTypeList;
	private ReceiptType receiptType;
	
	private List<ReceiptStatus> receiptStatusList;
	private ReceiptStatus receiptStatus;
	
	private List<Receipt> receiptList;
	private List<Receipt> selectedReceiptList;
	private Receipt receipt;
	private Long receiptId;
	private Staff staff;
	private Cloth cloth;
	private List<ClothType> clothTypeList;
	private ClothType clothType;
	
	private List<Cloth> clothList;
	private List<ReceiptPatternIron> receiptPatternIronList;
	private List<ReceiptPatternIron> selectedPatternList;
	private List<ClothTypeCounter> clothTypeCountList;
	private ReceiptPatternIron receiptPatternIron;
	
	private Calendar dateFrom;
	private Calendar dateTo;
	
	private String deliveryDateTimeStr;
	private String deliveryDateStr;
	private String deliveryTimeStr;
	
	private static final String JASPER_RECEIPT_COLLECT_HH = "jasper_report/handheldReceiptCollect.jasper";
	private static final String JASPER_RECEIPT_IRON = "jasper_report/receiptIron.jasper";
	private static final String JASPER_RECEIPT_RACK = "jasper_report/receiptRack.jasper";
	private static final String JASPER_A4_RECEIPT_DIST = "jasper_report/a4ReceiptDist.jasper";
	
	private HttpServletResponse response;
	
	public String getListPage()
	{
		receiptTypeList = Arrays.asList(ReceiptType.values());
		receiptStatusList = Arrays.asList(ReceiptStatus.values());
		
		this.setTilesKey("receipt.list");
		return TILES;
	}
	
	public String getSearchResultPage()
	{
		CustomCriteriaHandler<Receipt> customCriteriaHandler = new CustomCriteriaHandler<Receipt>()
		{
			@Override
			public void makeCustomCriteria(Criteria criteria)
			{
				if (receiptType != null)
				{
					criteria.add(Restrictions.eq("receiptType", receiptType));
				}
				
				if (receipt.getCode() != null && !receipt.getCode().isEmpty())
				{
					criteria.add(Restrictions.like("code", receipt.getCode(), MatchMode.START));
				}
				
				if (dateFrom != null)
				{
					dateFrom.set(Calendar.HOUR_OF_DAY, dateFrom.getActualMinimum(Calendar.HOUR_OF_DAY));
					dateFrom.set(Calendar.MINUTE, dateFrom.getActualMinimum(Calendar.MINUTE));
					dateFrom.set(Calendar.SECOND, dateFrom.getActualMinimum(Calendar.SECOND));
					dateFrom.getTime();
					criteria.add(  Restrictions.ge("creationDate", dateFrom) );
				}

				if (dateTo != null)
				{
					dateTo.set(Calendar.HOUR_OF_DAY, dateTo.getActualMaximum(Calendar.HOUR_OF_DAY));
					dateTo.set(Calendar.MINUTE, dateTo.getActualMaximum(Calendar.MINUTE));
					dateTo.set(Calendar.SECOND, dateTo.getActualMaximum(Calendar.SECOND));
					dateTo.getTime();
					criteria.add(  Restrictions.le("creationDate", dateTo) );
				}
				
				if (receiptStatus != null)
				{
					criteria.add(Restrictions.eq("receiptStatus", receiptStatus));
				}
				
				if (receipt.getCreatedBy() != null && receipt.getCreatedBy().getUsername() != null && !receipt.getCreatedBy().getUsername().isEmpty())
				{
					criteria.add(Restrictions.like("createdBy.username", receipt.getCreatedBy().getUsername(), MatchMode.START));
				}
				
				if (receipt.getStaffHandledBy() != null && receipt.getStaffHandledBy().getCode() != null && !receipt.getStaffHandledBy().getCode().isEmpty())
				{
					// If you want to add restrictions on an associated entity, you must create an alias, or a subcriteria
					criteria.createAlias("staffHandledBy", "shb");
//					criteria.add(Restrictions.like("shb.nameEng", receipt.getStaffHandledBy().getNameEng(), MatchMode.START));
					criteria.add(Restrictions.like("shb.code", receipt.getStaffHandledBy().getCode(), MatchMode.START));
				}
				
				if (receipt.getStaffPickedBy() != null && receipt.getStaffPickedBy().getCode() != null && !receipt.getStaffPickedBy().getCode().isEmpty())
				{
					// If you want to add restrictions on an associated entity, you must create an alias, or a subcriteria
					criteria.createAlias("staffPickedBy", "spb");
					criteria.add(Restrictions.like("spb.code", receipt.getStaffPickedBy().getCode(), MatchMode.START));
				}
				
			}
		};
		
		CustomLazyHandler<Receipt> customLazyHandler = new CustomLazyHandler<Receipt>()
		{
			@Override
			public void LazyList(List<Receipt> list)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Receipt tmpReceipt = list.get(i);
					
					if (tmpReceipt.getCreatedBy() != null)
					{
						tmpReceipt.getCreatedBy().getUsername();
					}
					
					if (tmpReceipt.getStaffHandledBy() != null)
					{
						tmpReceipt.getStaffHandledBy().getId();
					}
					
					if (tmpReceipt.getStaffPickedBy() != null)
					{
						tmpReceipt.getStaffPickedBy().getId();
					}
					
				}
			}
		};
		
		this.loadPagination(this.getGeneralService().totalByExample(Receipt.class, null, customCriteriaHandler));
		this.receiptList = this.getGeneralService().findByExample(
						Receipt.class, 
						null,
						getPage().getOffset(), 
						getPage().getInterval(),
						customCriteriaHandler, 
						customLazyHandler, 
						Order.desc("creationDate")
		);
		
		this.setTilesKey("receipt.search");
		return TILES;
	}
	
	public String getDetailPage()
	{
		Long receiptId = selectedReceiptList.get(0).getId();
		this.receipt = this.getReceiptFromDbById(receiptId);
//		System.out.println("receiptId: " + receiptId);
		
		// Ironing Delivery
		if (receipt.getReceiptType().equals(ReceiptType.Iron))
		{
			this.receiptPatternIronList = new ArrayList<ReceiptPatternIron>(receipt.getReceiptPatternIronSet());
			List<Cloth> allClothList = new ArrayList<Cloth>();
			for (int i = 0; i < this.receiptPatternIronList.size(); i++)
			{
				ReceiptPatternIron pattern = this.receiptPatternIronList.get(i);
				allClothList.addAll(pattern.getClothSet());
			}
			Collections.sort(this.receiptPatternIronList, new ReceiptPatternIronSorter());
			
			this.clothTypeCountList = this.updateclothTypeCountMap(allClothList);
			this.setTilesKey("receipt.detail.ironing.receipt");
		}
		else	// Cloth Collection / Distribute
		{
			this.clothList = new ArrayList<Cloth>(receipt.getClothSet());
			Collections.sort(this.clothList, new ClothDetailSorter());
			this.clothTypeCountList = this.updateclothTypeCountMap(this.clothList);
			this.setTilesKey("receipt.detail");
		}
		
		return TILES;
	}
	
	//////////////////////////////////////////////////////
	// For Iroing Delivery Receipt
	//////////////////////////////////////////////////////
	public String getRemoveClothPage()
	{
		//System.out.println("get Remove Cloth Page~!!");
		this.receiptId = selectedReceiptList.get(0).getId();
		this.clothTypeList = getMasterService().findAll(ClothType.class, null, null, null, Order.asc("id"));
		
		this.setTilesKey("receipt.detail.ironing.receipt.delete.cloth");
		return TILES;
	}
	
	
	public void validateRemoveClothFromRollContainer()
	{
		if (staff.getCode() == null || staff.getCode().isEmpty())
		{
			addActionError(String.format(getText("errors.custom.required"), getText("staff.code")));
		}
		
		if (clothType.getId() == null)
		{
			addActionError(String.format(getText("errors.custom.required"), getText("clothType.clothType")));
		}
		
		if (cloth.getCode() == null || cloth.getCode().isEmpty())
		{
			addActionError(String.format(getText("errors.custom.required"), getText("cloth.code")));
		}
		
		if ( !hasActionErrors() )
		{
			// get Receipt in validation which can be used later too 
			this.receipt = this.getReceiptFromDbById(this.receiptId);
			Iterator<ReceiptPatternIron> itPattern = this.receipt.getReceiptPatternIronSet().iterator();
			ReceiptPatternIron tmpReceiptPatternIron = null;
			Iterator<Cloth> itCloth = null;
			Cloth tmpCloth = null;
			Long clothIdToBeDel = null;
			boolean notFound = true;
			String targetRfid = null;
			while (itPattern.hasNext() && notFound)
			{
				tmpReceiptPatternIron = itPattern.next();
				itCloth = tmpReceiptPatternIron.getClothSet().iterator();
				while ( itCloth.hasNext() && notFound)
				{
					tmpCloth = itCloth.next();
					if (tmpCloth.getCode().equals(cloth.getCode()) &&
						tmpCloth.getClothType().getId().equals(clothType.getId()) &&
						tmpCloth.getStaff().getCode().equals(staff.getCode()))
					{
						//System.out.println("Staff '" + staff.getCode() + "' \t Cloth '" + cloth.getCode() + "' is deleted from Cart '" + tmpReceiptPatternIron.getRollContainer().getCode() + "'");
						notFound = false;
						
						// Delete cloth from container do here!!!
						clothIdToBeDel = tmpCloth.getId();
						tmpReceiptPatternIron.getClothSet().remove(tmpCloth);
						tmpReceiptPatternIron.setPatternClothTotal(tmpReceiptPatternIron.getClothSet().size());
						this.receipt.setReceiptClothTotal(this.receipt.getReceiptClothTotal() - 1);
						
						// This cloth is marked for historyCloth
						targetRfid = tmpCloth.getRfid();
					}
				}
			}
			if (notFound)
			{
				addActionError( getText("errors.no.cloth.found"));
			}
			else
			{
				this.getGeneralService().save(Receipt.class, this.receipt);
				
				//////////////////////////////////////////////////////
				// Change the cloth status back to "Washing"
				//////////////////////////////////////////////////////
				tmpCloth = this.getMasterService().get(Cloth.class, clothIdToBeDel, null);
				tmpCloth.setClothStatus(ClothStatus.Washing);	// this cloth will be assumed to be not-did-ironing 
				this.getMasterService().save(Cloth.class, tmpCloth);
				
				
				//////////////////////////////////////////////////////
				// Delete the historyCloth from this Receipt
				//////////////////////////////////////////////////////
				if (targetRfid != null)
				{
					final String clothRfid = targetRfid;
					final Long receiptId = this.receipt.getId();
					List<HistoryCloth> deleteList = this.getGeneralService().findByExample(HistoryCloth.class, null, null, null, 
							new CustomCriteriaHandler<HistoryCloth>()
							{
								@Override
								public void makeCustomCriteria(Criteria criteria)
								{
									criteria.add(Restrictions.eq("rfid", clothRfid));
									criteria.add(Restrictions.eq("receipt.id", receiptId));
								}
							}, 
							
							null, null);
					
					
					if (deleteList != null && deleteList.size() > 0)
					{
						if (deleteList.size() == 1)
						{
							HistoryCloth hc = deleteList.get(0);
							log4j.error( "HistoryCloth '" + hc.getRfid() + "' successfully deleted from receipt '" +  this.receipt.getCode() + "'!!!" );
							this.getGeneralService().delete(HistoryCloth.class, hc);
							
							// Special Case: all unracked clothes are removed from unfinished receipt, set this receipt to finished
							Receipt checkReceipt = this.getReceiptFromDbById(this.receiptId);
							if (checkReceipt.getHistoryClothSet() == null || checkReceipt.getHistoryClothSet().size() == 0)
							{
								checkReceipt.setReceiptStatus(ReceiptStatus.Finished);
								System.out.println("Iron-Receipt '" + receiptId + "' changed to FINISHED! (Due to last unracked cloth removal");
								this.getGeneralService().save(Receipt.class, checkReceipt);
							}
						}
						else
						{
							log4j.error( "Error! This RFID/HistoryCloth appears more than once in receipt '" +  this.receipt.getCode() + "'!!!" );
						}
					}
					else
					{
						log4j.error( "Error! This RFID/HistoryCloth not found in receipt '" +  this.receipt.getCode() + "'!!!" );
					}
				}

			}
		}
	}
	
	public String removeClothFromRollContainer()
	{
		try
		{
			removeClothFromRollContainerImpl();
			addActionMessage( getText( "msg.cloth.delete.success" ) );
			log4j.info( getText( "msg.cloth.delete.success" ) );
		}
		catch (Exception e)
		{
			log4j.error( e );
			
			while ( true )
			{
				Exception cause = (Exception) e.getCause();
				if ( cause == null )
				{
					addActionError( getText("errors.delete.fail") );
					e.printStackTrace();
					log4j.error( getText("errors.delete.fail") );
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
	
	public void removeClothFromRollContainerImpl()
	{
		// For faster performance, deletion done in validation method "validateRemoveClothFromRollContainer()"
	}
	
	
	// Only for Ironing Delivery (update roll container ironing delivery datetime)
	public String getEditPatternPage()
	{
		Long patternId = selectedPatternList.get(0).getId();
		//System.out.println("Pattern ID: " + patternId);
		
		CustomLazyHandler<ReceiptPatternIron> customLazyHandler = new CustomLazyHandler<ReceiptPatternIron>()
		{
			@Override
			public void LazyObject(ReceiptPatternIron obj)
			{
				obj.getReceipt().getId();
				
				if (obj.getRollContainer() != null)
					obj.getRollContainer().getId();
				
				// Ironing
				if (obj.getClothSet() != null && obj.getClothSet().size() > 0)
				{
					Iterator<Cloth> itCloth = obj.getClothSet().iterator();
					Cloth cloth = null;
					while (itCloth.hasNext())
					{
						cloth = itCloth.next();
						cloth.getStaff().getId();
						cloth.getStaff().getDept().getId();
						cloth.getClothType().getId();
					}
				}
			}
		};
		
		this.receiptPatternIron = this.getGeneralService().get(ReceiptPatternIron.class, patternId, customLazyHandler);
		
		this.receipt = receiptPatternIron.getReceipt();
		this.clothList = new ArrayList<Cloth>(receiptPatternIron.getClothSet());
		Collections.sort(this.clothList, new ClothDetailSorter());
		this.clothTypeCountList = this.updateclothTypeCountMap(this.clothList);
		
		this.setTilesKey("receipt.ironing.receipt.pattern.edit");
		return TILES;
	}
	
	
	public void validateUpdate()
	{
		try
		{
			DateConverter.toCalendar(deliveryDateStr, DateConverter.DATE_FORMAT_REVERSE);
		}
		catch (Exception e)
		{
			String errorMsg = String.format(getText("errors.custom.invalid"), getText("label.ironing.delivery.date"));
			addActionError(errorMsg);
		}

		try
		{
			DateConverter.toCalendar(deliveryTimeStr, DateConverter.HOUR_MINUTE_FORMAT);
		}
		catch (Exception e)
		{
			String errorMsg = String.format(getText("errors.custom.invalid"), getText("label.ironing.delivery.time"));
			addActionError(errorMsg);
		}
	}
	
	public String update() throws Exception
	{
		try
		{
			updateImpl();
			addActionMessage( getText( SuccessMessage_SaveSuccess ) );
			log4j.info( getText( SuccessMessage_SaveSuccess ) );
		}
		catch (Exception e)
		{
			log4j.error( e );
			
			while ( true )
			{
				Exception cause = (Exception) e.getCause();
				if ( cause == null )
				{
					addActionError( getText (ErrorMessage_SaveFail, new String[]{e.getMessage()} ) );
					e.printStackTrace();
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
	
	public void updateImpl() throws Exception
	{
		this.receiptPatternIron = this.getGeneralService().get(ReceiptPatternIron.class, receiptPatternIron.getId(), null);
		String deliveryDateTime = deliveryDateStr + deliveryTimeStr;
		String dateTimePattern = DateConverter.DATE_FORMAT_REVERSE + DateConverter.HOUR_MINUTE_FORMAT;
		try
		{
			receiptPatternIron.setIroningDeliveryTime( DateConverter.toCalendar(deliveryDateTime, dateTimePattern) );
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		this.getGeneralService().save(ReceiptPatternIron.class, receiptPatternIron);
	}
	
	
	public String printReceipt()
	{
		Long receiptId = selectedReceiptList.get(0).getId();
		this.receipt = this.getReceiptFromDbById(receiptId);
		//System.out.println("Printing receiptId: " + receiptId);
		
		String subreportPath = this.getRealPath() + JASPER_FOLDER;
		AioReceiptPrinter printer = new AioReceiptPrinter();
		try
		{
			if (receipt.getReceiptType().equals(ReceiptType.Iron))
			{
				String reportFilePath = this.getRealPath() + JASPER_RECEIPT_IRON;
				printer.printIroningReceipt(this.receipt, reportFilePath, subreportPath);
			}
			else if (receipt.getReceiptType().equals(ReceiptType.Collect))
			{
				String reportFilePath = this.getRealPath() + JASPER_RECEIPT_COLLECT_HH;
				printer.printCollectReceipt(this.receipt, reportFilePath, subreportPath);
			}
			else if (receipt.getReceiptType().equals(ReceiptType.Rack))
			{
				String reportFilePath = this.getRealPath() + JASPER_RECEIPT_RACK;
				printer.printRackingReceipt(this.receipt, reportFilePath, subreportPath);
			}
			else if (receipt.getReceiptType().equals(ReceiptType.Distribute))
			{
				String reportFilePath = this.getRealPath() + JASPER_A4_RECEIPT_DIST;
				printer.printDistReceipt(this.receipt, reportFilePath, subreportPath);
			}
			
			
			addActionMessage( getText( "msg.print.success" ) );
			log4j.info( getText( "msg.print.success" ) );
		}
		catch (Exception e)
		{
			log4j.error( e );
			
			while ( true )
			{
				Exception cause = (Exception)e.getCause();
				if ( cause == null )
				{
					addActionError( getText( "errors.print.fail" ) );
					e.printStackTrace();
					log4j.error( getText( "errors.print.fail" ) );
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
	
	public String exportReceiptToPdf()
	{
		this.receipt = this.getReceiptFromDbById(receipt.getId());
		String subreportPath = this.getRealPath() + JASPER_FOLDER;
		AioReceiptExporter exporter = new AioReceiptExporter(this.getResponse());
		try
		{
			if (receipt.getReceiptType().equals(ReceiptType.Iron))
			{
				String reportFilePath = this.getRealPath() + JASPER_RECEIPT_IRON;
				exporter.exportIroningReceipt(this.receipt, reportFilePath, subreportPath);
			}
			else if (receipt.getReceiptType().equals(ReceiptType.Collect))
			{
				String reportFilePath = this.getRealPath() + JASPER_RECEIPT_COLLECT_HH;
				exporter.exportCollectReceipt(this.receipt, reportFilePath, subreportPath);
			}
			else if (receipt.getReceiptType().equals(ReceiptType.Rack))
			{
				String reportFilePath = this.getRealPath() + JASPER_RECEIPT_RACK;
				exporter.exportRackingReceipt(this.receipt, reportFilePath, subreportPath);
			}
			else if (receipt.getReceiptType().equals(ReceiptType.Distribute))
			{
				String reportFilePath = this.getRealPath() + JASPER_A4_RECEIPT_DIST;
				exporter.exportDistReceipt(this.receipt, reportFilePath, subreportPath);
			}
		}
		catch (Exception e)
		{
			log4j.error( e );
			
			while ( true )
			{
				Exception cause = (Exception)e.getCause();
				if ( cause == null )
				{
					addActionError( getText( "errors.print.fail" ) );
					e.printStackTrace();
					log4j.error( getText( "errors.print.fail" ) );
					break;
				}
				else
				{
					e = cause;
				}
			}
		}
		
		return null;
	}
	
	
	
	
	///////////////////////////////////////////
	// Utility Method
	///////////////////////////////////////////
	private Receipt getReceiptFromDbById(Long receiptId)
	{
		CustomLazyHandler<Receipt> customLazyHandler = new CustomLazyHandler<Receipt>()
		{
			@Override
			public void LazyObject(Receipt obj)
			{
				if (obj.getCreatedBy() != null)
					obj.getCreatedBy().getUsername();
				
				if (obj.getStaffHandledBy() != null)
					obj.getStaffHandledBy().getId();
				
				if (obj.getStaffPickedBy() != null)
					obj.getStaffPickedBy().getId();
				
				if (obj.getHistoryClothSet() != null)
				{
					obj.getHistoryClothSet().size();
				}
				
				// Collect / Rack / Distribute
				if (obj.getClothSet() != null && obj.getClothSet().size() > 0)
				{
					Iterator<Cloth> it = obj.getClothSet().iterator();
					Cloth cloth = null;
					while (it.hasNext())
					{
						cloth = it.next();
						cloth.getStaff().getId();
						cloth.getStaff().getDept().getId();
						cloth.getClothType().getId();
					}
				}
				
				// Ironing
				if (obj.getReceiptPatternIronSet() != null && obj.getReceiptPatternIronSet().size() > 0)
				{
					Iterator<ReceiptPatternIron> itPattern = obj.getReceiptPatternIronSet().iterator();
					ReceiptPatternIron pattern = null;
					Iterator<Cloth> itCloth = null;
					Cloth cloth = null;
					while (itPattern.hasNext())
					{
						pattern = itPattern.next();
						pattern.getRollContainer().getId();
						itCloth = pattern.getClothSet().iterator();
						while (itCloth.hasNext())
						{
							cloth = itCloth.next();
							cloth.getStaff().getId();
							cloth.getStaff().getDept().getId();
							cloth.getClothType().getId();
						}
					}
				}
			}
		};
		
		return this.getGeneralService().get(Receipt.class, receiptId, customLazyHandler);
	}
	
	private List<ClothTypeCounter> updateclothTypeCountMap(List<Cloth> clothList)
	{
		// 1. count each cloth type
		TreeMap<String, Integer> clothTypeCountMap = new TreeMap<String, Integer>();
		for (int i = 0; i < clothList.size(); i++)
		{
			String type = clothList.get(i).getClothType().getName();
			Integer counter = null;
			if (clothTypeCountMap.containsKey(type))
			{
				counter = clothTypeCountMap.get(type);
				counter++;
				clothTypeCountMap.put(type, counter);
			}
			else
			{
				clothTypeCountMap.put(type, 1);
			}
		}
		
		// 2. convert the cloth-type-count-map into a list so it can be sent back to HTML by json
		List<ClothTypeCounter> typeCountList = this.convertClothTypeCountMapToList(clothTypeCountMap);
		return typeCountList;
	}
	
	private List<ClothTypeCounter> convertClothTypeCountMapToList(TreeMap<String, Integer> clothTypeCountMap)
	{
		ArrayList<Entry<String, Integer>> ctcList = new ArrayList<Entry<String,Integer>>(clothTypeCountMap.entrySet());
		ArrayList<ClothTypeCounter> resultList = new ArrayList<ClothTypeCounter>();
		
		for (int i = 0; i < ctcList.size(); i++)
		{
			Entry<String, Integer> e = ctcList.get(i);
			
			String type = e.getKey();
			Integer num = e.getValue();
			
			ClothTypeCounter typeCounter = new ClothTypeCounter();
			typeCounter.setType(type);
			typeCounter.setNum(num);
			
			resultList.add(typeCounter);
		}
		
		return resultList;
	}
	
	/////////////////////////////////////////////////
	// Shared Method
	/////////////////////////////////////////////////
	public HttpServletResponse getResponse()
	{
		return response;
	}
	@Override
	public void setServletResponse(HttpServletResponse response)
	{
		this.response = response;
	}
	
	public List<ReceiptType> getReceiptTypeList()
	{
		return receiptTypeList;
	}
	public void setReceiptTypeList(List<ReceiptType> receiptTypeList)
	{
		this.receiptTypeList = receiptTypeList;
	}
	public ReceiptType getReceiptType()
	{
		return receiptType;
	}
	public void setReceiptType(ReceiptType receiptType)
	{
		this.receiptType = receiptType;
	}
	public List<ReceiptStatus> getReceiptStatusList()
	{
		return receiptStatusList;
	}
	public void setReceiptStatusList(List<ReceiptStatus> receiptStatusList)
	{
		this.receiptStatusList = receiptStatusList;
	}
	public ReceiptStatus getReceiptStatus()
	{
		return receiptStatus;
	}
	public void setReceiptStatus(ReceiptStatus receiptStatus)
	{
		this.receiptStatus = receiptStatus;
	}
	public List<Receipt> getReceiptList()
	{
		return receiptList;
	}
	public void setReceiptList(List<Receipt> receiptList)
	{
		this.receiptList = receiptList;
	}
	public Receipt getReceipt()
	{
		return receipt;
	}
	public void setReceipt(Receipt receipt)
	{
		this.receipt = receipt;
	}
	public Calendar getDateFrom()
	{
		return dateFrom;
	}
	@TypeConversion(converter="utils.convertor.struts2.SimpleDateTimeToCalendarTypeConvertor")
	public void setDateFrom(Calendar dateFrom)
	{
		this.dateFrom = dateFrom;
	}
	public Calendar getDateTo()
	{
		return dateTo;
	}
	@TypeConversion(converter="utils.convertor.struts2.SimpleDateTimeToCalendarTypeConvertor")
	public void setDateTo(Calendar dateTo)
	{
		this.dateTo = dateTo;
	}
	public List<Receipt> getSelectedReceiptList()
	{
		return selectedReceiptList;
	}
	public void setSelectedReceiptList(List<Receipt> selectedReceiptList)
	{
		this.selectedReceiptList = selectedReceiptList;
	}
	public List<ClothTypeCounter> getClothTypeCountList()
	{
		return clothTypeCountList;
	}
	public void setClothTypeCountList(List<ClothTypeCounter> clothTypeCountList)
	{
		this.clothTypeCountList = clothTypeCountList;
	}
	public List<Cloth> getClothList()
	{
		return clothList;
	}
	public void setClothList(List<Cloth> clothList)
	{
		this.clothList = clothList;
	}
	public List<ReceiptPatternIron> getReceiptPatternIronList()
	{
		return receiptPatternIronList;
	}
	public void setReceiptPatternIronList(List<ReceiptPatternIron> receiptPatternIronList)
	{
		this.receiptPatternIronList = receiptPatternIronList;
	}
	public List<ReceiptPatternIron> getSelectedPatternList()
	{
		return selectedPatternList;
	}
	public void setSelectedPatternList(List<ReceiptPatternIron> selectedPatternList)
	{
		this.selectedPatternList = selectedPatternList;
	}
	public ReceiptPatternIron getReceiptPatternIron()
	{
		return receiptPatternIron;
	}
	public void setReceiptPatternIron(ReceiptPatternIron receiptPatternIron)
	{
		this.receiptPatternIron = receiptPatternIron;
	}
	public String getDeliveryDateTimeStr()
	{
		return deliveryDateTimeStr;
	}
	public void setDeliveryDateTimeStr(String deliveryDateTimeStr)
	{
		this.deliveryDateTimeStr = deliveryDateTimeStr;
	}
	public String getDeliveryDateStr()
	{
		return deliveryDateStr;
	}
	public void setDeliveryDateStr(String deliveryDateStr)
	{
		this.deliveryDateStr = deliveryDateStr;
	}
	public String getDeliveryTimeStr()
	{
		return deliveryTimeStr;
	}
	public void setDeliveryTimeStr(String deliveryTimeStr)
	{
		this.deliveryTimeStr = deliveryTimeStr;
	}
	public Staff getStaff()
	{
		return staff;
	}
	public void setStaff(Staff staff)
	{
		this.staff = staff;
	}
	public Cloth getCloth()
	{
		return cloth;
	}
	public void setCloth(Cloth cloth)
	{
		this.cloth = cloth;
	}
	public Long getReceiptId()
	{
		return receiptId;
	}
	public void setReceiptId(Long receiptId)
	{
		this.receiptId = receiptId;
	}
	public ClothType getClothType()
	{
		return clothType;
	}
	public void setClothType(ClothType clothType)
	{
		this.clothType = clothType;
	}
	public List<ClothType> getClothTypeList()
	{
		return clothTypeList;
	}
	public void setClothTypeList(List<ClothType> clothTypeList)
	{
		this.clothTypeList = clothTypeList;
	}
}



class ReceiptPatternIronSorter implements Comparator<ReceiptPatternIron>
{
	@Override
	public int compare(ReceiptPatternIron o1, ReceiptPatternIron o2)
	{
		String cart1 = o1.getRollContainer().getCode();
		String cart2 = o2.getRollContainer().getCode();
		return cart1.compareTo(cart2);
	}
}

class ClothDetailSorter implements Comparator<Cloth>
{
	@Override
	public int compare(Cloth o1, Cloth o2)
	{
		String dept1 = o1.getStaff().getDept().getNameCht();
		String dept2 = o2.getStaff().getDept().getNameCht();
		int r1 = dept1.compareTo(dept2);
		if (r1 != 0)
		{
			return r1;
		}
		else
		{
			String staff1 = o1.getStaff().getCode();
			String staff2 = o2.getStaff().getCode();
			int r2 = staff1.compareTo(staff2);
			if (r2 != 0)
			{
				return r2;
			}
			else
			{
				String clothType1 = o1.getClothType().getName();
				String clothType2 = o2.getClothType().getName();
				int r3 = clothType1.compareTo(clothType2);
				if (r3 != 0)
				{
					return r3;
				}
				else
				{
					return o1.getCode().compareTo(o2.getCode());
				}
			}
		}
	}
}