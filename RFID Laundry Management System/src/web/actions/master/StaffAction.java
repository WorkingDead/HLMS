package web.actions.master;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.Cloth.ClothStatus;
import module.dao.master.ClothType;
import module.dao.master.Department;
import module.dao.master.Staff;
import module.dao.master.Staff.StaffStatus;

import module.service.all.MasterService;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import web.actions.BaseActionMaster;

@Results({
	@Result(name="input", location="staff.new", type="tiles"),
	@Result(name="getNewPage", location="staff.new", type="tiles"),
	@Result(name="getListPage", location="staff.list", type="tiles"),
	@Result(name="getSearchResultPage", location="staff.search", type="tiles"),
	@Result(name="getEditPage", location="staff.edit", type="tiles"),
	@Result(name="getEditClothPage", location="staff.cloth.edit", type="tiles"),
	
	@Result(name="clothListJson", type="json", params={
//			"root",					"clothListAdded", 
//			"wrapPrefix",			"{ \"d\" : ", "wrapSuffix", "}",
			
			"includeProperties",	"^clothListInDb\\[\\d+\\]\\.id, " +
									"^clothListInDb\\[\\d+\\]\\.clothType\\.name, " +
									"^clothListInDb\\[\\d+\\]\\.size, " +
									"^clothListInDb\\[\\d+\\]\\.code, " +
									"^clothListInDb\\[\\d+\\]\\.rfid, " +
									"^clothListInDb\\[\\d+\\]\\.clothStatus, " +
									"^clothListInDb\\[\\d+\\]\\.displayField, " +
									"^clothListInDb\\[\\d+\\]\\.remark, " +
									"^clothListAdded\\[\\d+\\]\\.id, " +
									"^clothListAdded\\[\\d+\\]\\.clothType\\.name, " +
									"^clothListAdded\\[\\d+\\]\\.size, " +
									"^clothListAdded\\[\\d+\\]\\.code, " +
									"^clothListAdded\\[\\d+\\]\\.rfid, " +
									"^clothListAdded\\[\\d+\\]\\.clothStatus, " +
									"^clothListAdded\\[\\d+\\]\\.displayField, " +
									"^clothListAdded\\[\\d+\\]\\.remark"
	}),
	
	//Auto Complete
	@Result(name="staffCodeAutoCompleteJson", type="json", params={
			"root", 				"suggestStaffList",
			"includeProperties",
									"\\[\\d+\\]\\.id, " +
									"\\[\\d+\\]\\.code, " +
									"\\[\\d+\\]\\.nameEng, " +
									"\\[\\d+\\]\\.nameCht, " +
									"\\[\\d+\\]\\.dept\\.nameEng, " +
									"\\[\\d+\\]\\.dept\\.nameCht"
	}),
	
	@Result(name="uniqueStaffJson", type="json", params={
			"includeProperties",
			"^dummyStaffForAC\\.id, ^dummyStaffForAC\\.code, ^dummyStaffForAC\\.nameEng, ^dummyStaffForAC\\.nameCht, ^dummyStaffForAC\\.dept\\.nameEng, ^dummyStaffForAC\\.dept\\.nameCht"
		}),
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", "create, update, updateClothOfStaff"}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class StaffAction extends BaseActionMaster
{
	private static final long serialVersionUID = 635973663964230927L;
	private static final Logger log4j = Logger.getLogger(StaffAction.class);
	
	private final String SESSION_KEY_DB_PATTERN = "SESSION_KEY_DB_PATTERN";		// for clothes already in DB
	private final String SESSION_KEY_NEW_PATTERN = "SESSION_KEY_NEW_PATTERN";	// for newly added clothes (not in DB yet)
	private final String SESSION_KEY_EDIT_STAFF = "SESSION_KEY_EDIT_STAFF";		// staff to be edited save here
	private final String SESSION_KEY_EDIT_STAFF_CLOTH = "SESSION_KEY_EDIT_STAFF_CLOTH";		// cloth of staff to be edited save here
	
	private final String CODE_TYPE_DELIMETER = " @ ";
	
	/////////////////////////////////////////////////////////
	// field name for isFieldValueAlreadyInDb() method
	private static final String COLUMN_CODE = "code";
	private static final String COLUMN_CARD_NUMBER = "cardNumber";
	private static final String COLUMN_NAME_CHT = "nameCht";
	private static final String COLUMN_NAME_ENG = "nameEng";
	private static final String COLUMN_RFID = "rfid";	// for Cloth only
	// field name for isFieldValueAlreadyInDb() method
	/////////////////////////////////////////////////////////
	
	private Staff staff;
	private List<Staff> staffList;
	private List<Staff> selectedStaffList;
	
	//Staff Utility
	private List<Staff> suggestStaffList;
	private Staff dummyStaffForAC;
	
	private List<Department> deptList;
	private List<String> positionList;
	private List<StaffStatus> staffStatusList;
	private List<ClothType> clothTypeList;
	private List<String> clothSizeList;
	private List<ClothStatus> clothStatusList;
	private List<TrueFalseBoolean> booleanList;		// for enable and disable (for list page only)
	private List<YesNoInt> enableStatusList;
	private Integer enableStatus;
	
	private List<Cloth> clothList;			// tmp newly added clothes, next is to add them to clothListAdded 
	private List<Cloth> clothListAdded;		// just added but not saved to DB yet (store in session only)
	private List<Cloth> clothListInDb;
	private List<Long> voidClothIdList;		// clothes that will change status to VOID
	private Cloth cloth;
	
	private StaffStatus defaultStatus = StaffStatus.Normal;
	public StaffStatus getDefaultStatus() {
		return defaultStatus;
	}

	public void setDefaultStatus(StaffStatus defaultStatus) {
		this.defaultStatus = defaultStatus;
	}
	// Services
	private MasterService masterService;
	
	public StaffAction()
	{
		this.masterService = this.getMasterService();
		this.deptList = this.masterService.findAll(Department.class, null, null, null, Order.asc("nameCht"));

		this.positionList = new ArrayList<String>( this.getBeansFactoryApplication().getgetAllStaffPositionInMap().values() );
		
//		this.staffStatusList = Arrays.asList( StaffStatus.values() );
		
		String allClothSize = this.getBeansFactoryApplication().getAllClothSize();
		String clothSizeArray[] = allClothSize.split(",");
		this.clothSizeList = Arrays.asList(clothSizeArray);
		
//		this.clothStatusList = Arrays.asList( ClothStatus.values() );
		clothListAdded = new ArrayList<Cloth>();
	}
	
	@Override
	public String execute()
	{
		return null;
	}
	
	public String getNewPage()
	{
		this.resetSessionVariables();
		this.staffStatusList = Arrays.asList( StaffStatus.Normal );
		this.clothStatusList = Arrays.asList( ClothStatus.Using );
		this.clothTypeList = getEnableClothType();
		
		return "getNewPage";
	}
	
	public String getListPage()
	{
		this.resetSessionVariables();
		this.staffStatusList = Arrays.asList( StaffStatus.values() );
//		this.clothStatusList = Arrays.asList( ClothStatus.values() );
		this.booleanList = Arrays.asList( TrueFalseBoolean.values() );
		this.enableStatusList = Arrays.asList(YesNoInt.values());
		
		return "getListPage";
	}
	
	public String getEditPage()
	{
		this.resetSessionVariables();
		this.staffStatusList = Arrays.asList( StaffStatus.values() );
		this.clothStatusList = Arrays.asList( ClothStatus.Using );
		this.clothTypeList = getEnableClothType();
		
		// Long staffId = selectedStaffList.get(0).getId();	// js control exactly ONE staff selected before submit form in JSP
		Long staffId = this.staff.getId();
		this.staff = this.getStaffById(staffId);
		
		if (this.staff.getClothSet() != null && this.staff.getClothSet().size() > 0)
		{
			this.clothListInDb = new ArrayList<Cloth>(this.staff.getClothSet());
			Cloth tmpCloth = null;
			String clothStatusStr = null;
			for (int i = 0; i < this.clothListInDb.size(); i++)
			{
				tmpCloth = this.clothListInDb.get(i);
				clothStatusStr = this.getText(tmpCloth.getClothStatus().getValue());
				tmpCloth.setDisplayField(clothStatusStr);
				
				if (tmpCloth.getRemark() == null)
					tmpCloth.setRemark("");
			}
			
			Collections.sort(this.clothListInDb, new EditPageDbPatternComparator());
			getSession().put(SESSION_KEY_DB_PATTERN, this.clothListInDb);
		}
		
		this.getSession().put(SESSION_KEY_EDIT_STAFF, this.staff);	// later take out this staff and update it to DB
		return "getEditPage";
	}
	
	public String getEditClothPage()
	{
		this.staff = (Staff) this.getSession().get(SESSION_KEY_EDIT_STAFF);
		
		// In cloth-edit-page, all clothType (include disable type) must be display to prevent error
		this.clothTypeList = getAllClothType();
		
		// If this staff doesn't hv clothes in DB, here won't be entered, then "clothListInDb" list won't be NULL
		this.clothListInDb = (List<Cloth>) getSession().get(SESSION_KEY_DB_PATTERN);
		this.cloth = this.searchClothById(this.clothListInDb, this.cloth.getId());
		
		this.getSession().put(SESSION_KEY_EDIT_STAFF_CLOTH, this.cloth);	// later take out this staff's this cloth and update it to DB
		return "getEditClothPage";
	}
	
	
	public void validateCreate()
	{
		if (staff.getCode() == null || staff.getCode().isEmpty())
		{
			addActionError(String.format(getText("errors.custom.required"), getText("staff.code")));
		}
		else if (this.isStaffFieldValueAlreadyInDb(COLUMN_CODE, staff.getCode()))
		{
			addActionError(String.format(getText("errors.custom.already.existed"), getText("staff.code"), staff.getCode()));
		}
		
		if (staff.getCardNumber() == null || staff.getCardNumber().isEmpty())
		{
			addActionError(String.format(getText("errors.custom.required"), getText("staff.card")));
		}
		else if (this.isStaffFieldValueAlreadyInDb(COLUMN_CARD_NUMBER, staff.getCardNumber()))
		{
			addActionError(String.format(getText("errors.custom.already.existed"), getText("staff.card"), staff.getCardNumber()));
		}
		
//		if (staff.getNameCht() == null || staff.getNameCht().isEmpty())
//		{
//			addActionError(String.format(getText("errors.custom.required"), getText("staff.name.cht")));
//		}
//		else if (this.isStaffFieldValueAlreadyInDb(COLUMN_NAME_CHT, staff.getNameCht()))
//		{
//			addActionError(String.format(getText("errors.custom.already.existed"), getText("staff.name.cht"), staff.getNameCht()));
//		}
		
//		if (staff.getNameEng() == null || staff.getNameEng().isEmpty())
//		{
//			addActionError(String.format(getText("errors.custom.required"), getText("staff.name.eng")));
//		}
//		else if (this.isStaffFieldValueAlreadyInDb(COLUMN_NAME_ENG, staff.getNameEng()))
//		{
//			addActionError(String.format(getText("errors.custom.already.existed"), getText("staff.name.eng"), staff.getNameEng()));
//		}
	}
	
	public String create()
	{
		try
		{
			createImpl();
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
	
	public void createImpl() throws Exception
	{
		// This is because Department extends BaseBoMaster which has hibernate version control
		// Therefore, get the Department object again and set it to the Staff object
		// If no hiberante version control, no need to get the entired Department Object
		// Actually, get the Department object again is only for getting the latest updated value of the fields, "id" and "version".
		// This is because hiberante with version control in "version" field only checks the fields, "id" and "version" for determining create or update
		Department department = this.masterService.get(Department.class, staff.getDept().getId());
		staff.setDept( department );
		
		this.clothListAdded = (ArrayList<Cloth>) getSession().get(SESSION_KEY_NEW_PATTERN);
		if (this.clothListAdded != null)
		{
			for (int i = 0; i < this.clothListAdded.size(); i++)
			{
				Cloth c = this.clothListAdded.get(i);
				c.setWashingCount(0);
				c.setEnable(true);
			}
			staff.setClothSet(new HashSet<Cloth>(this.clothListAdded));
		}
		
		this.masterService.save(Staff.class, staff);
		this.resetSessionVariables();
	}
	
	
	public void validateUpdate()
	{
		Staff staffInDb = (Staff) this.getSession().get(SESSION_KEY_EDIT_STAFF);
		
		if (staff.getCode() == null || staff.getCode().isEmpty())
		{
			addActionError(String.format(getText("errors.custom.required"), getText("staff.code")));
		}
		else if (staff.getCode().equals(staffInDb.getCode()))
		{
			// do nothing and not search the value in DB
			// used to skip the following checking
		}
		else if (this.isStaffFieldValueAlreadyInDb(COLUMN_CODE, staff.getCode()))
		{
			addActionError(String.format(getText("errors.custom.already.existed"), getText("staff.code"), staff.getCode()));
		}
		
		if (staff.getCardNumber() == null || staff.getCardNumber().isEmpty())
		{
			addActionError(String.format(getText("errors.custom.required"), getText("staff.card")));
		}
		else if (staff.getCardNumber().equals(staffInDb.getCardNumber()))
		{
			// do nothing and not search the value in DB
			// used to skip the following checking
		}
		else if (this.isStaffFieldValueAlreadyInDb(COLUMN_CARD_NUMBER, staff.getCardNumber()))
		{
			addActionError(String.format(getText("errors.custom.already.existed"), getText("staff.card"), staff.getCardNumber()));
		}
		
//		if (staff.getNameCht() == null || staff.getNameCht().isEmpty())
//		{
//			addActionError(String.format(getText("errors.custom.required"), getText("staff.name.cht")));
//		}
//		else if (staff.getNameCht().equals(staffInDb.getNameCht()))
//		{
//			// do nothing and not search the value in DB
//			// used to skip the following checking
//		}
//		else if (this.isStaffFieldValueAlreadyInDb(COLUMN_NAME_CHT, staff.getNameCht()))
//		{
//			addActionError(String.format(getText("errors.custom.already.existed"), getText("staff.name.cht"), staff.getNameCht()));
//		}
		
//		if (staff.getNameEng() == null || staff.getNameEng().isEmpty())
//		{
//			addActionError(String.format(getText("errors.custom.required"), getText("staff.name.eng")));
//		}
//		else if (staff.getNameEng().equals(staffInDb.getNameEng()))
//		{
//			// do nothing and not search the value in DB
//			// used to skip the following checking
//		}
//		else if (this.isStaffFieldValueAlreadyInDb(COLUMN_NAME_ENG, staff.getNameEng()))
//		{
//			addActionError(String.format(getText("errors.custom.already.existed"), getText("staff.name.eng"), staff.getNameEng()));
//		}
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
		//System.out.println("Update!");
		
		Staff staffToBeUpdated = (Staff) this.getSession().get(SESSION_KEY_EDIT_STAFF);
		staffToBeUpdated.setNameCht(staff.getNameCht());
		staffToBeUpdated.setNameEng(staff.getNameEng());
		staffToBeUpdated.setPosition(staff.getPosition());
		staffToBeUpdated.setCardNumber(staff.getCardNumber());
		
		staffToBeUpdated.setEnable(staff.getEnable());
		Department department = this.masterService.get(Department.class, staff.getDept().getId());
		staffToBeUpdated.setDept( department );
		
		this.clothListAdded = (ArrayList<Cloth>) getSession().get(SESSION_KEY_NEW_PATTERN);
		if (this.clothListAdded != null)	// In fact, at least call checkPattern() once will set SESSION_KEY_NEW_PATTERN (by Horace)
		{
			int size = this.clothListAdded.size();
			for (int i = 0; i < size; i++)
			{
				Cloth c = this.clothListAdded.get(i);
				c.setWashingCount(0);
				c.setEnable(true);
			}
			
			staffToBeUpdated.getClothSet().addAll(this.clothListAdded);
		}
		
		
		if (staffToBeUpdated.getStaffStatus().equals(StaffStatus.Leave))
		{
			// If this staff was already leave, don't gen special event again
			this.masterService.save(Staff.class, staffToBeUpdated);
		}
		else
		{
			staffToBeUpdated.setStaffStatus(staff.getStaffStatus());
			this.masterService.updateAndSaveStaff(staffToBeUpdated);
		}
		
		
		
//		this.masterService.save(Staff.class, staffToBeUpdated);
		this.resetSessionVariables();
	}
	
	public String getSearchResultPage()
	{
		CustomCriteriaHandler<Staff> customCriteriaHandler = new CustomCriteriaHandler<Staff>() 
		{
			@Override
			public void makeCustomCriteria(Criteria criteria)
			{
				if (staff != null)
				{
					if (staff.getCode() != null && !staff.getCode().isEmpty())
					{
						criteria.add(Restrictions.like("code", staff.getCode(), MatchMode.START));
					}
					
					if (staff.getCardNumber() != null && !staff.getCardNumber().isEmpty())
					{
						criteria.add(Restrictions.like("cardNumber", staff.getCardNumber(), MatchMode.START));
					}
					
					if (staff.getDept() != null && staff.getDept().getId() != null)
					{
						criteria.add(Restrictions.eq("dept.id", staff.getDept().getId()));
					}
					
					if (staff.getPosition() != null && !staff.getPosition().isEmpty())
					{
						criteria.add(Restrictions.eq("position", staff.getPosition()));
					}
					
					if (staff.getNameCht() != null && !staff.getNameCht().isEmpty())
					{
						criteria.add(Restrictions.like("nameCht", staff.getNameCht(), MatchMode.START));
					}
					
					if (staff.getNameEng() != null && !staff.getNameEng().isEmpty())
					{
						criteria.add(Restrictions.like("nameEng", staff.getNameEng(), MatchMode.START));
					}
					
					if (staff.getStaffStatus() != null)
					{
						criteria.add(Restrictions.eq("staffStatus", staff.getStaffStatus()));
					}
					
					if (enableStatus != null)
					{
						if (enableStatus.equals(YesNoInt.Yes.getValue()))
						{
							criteria.add(Restrictions.eq("enable", true));
						}
						else if (enableStatus.equals(YesNoInt.No.getValue()))
						{
							criteria.add(Restrictions.eq("enable", false));
						}
					}
				}
			}
		};
		
		CustomLazyHandler<Staff> customLazyHandler = new CustomLazyHandler<Staff>()
		{
			@Override
			public void LazyList(List<Staff> list)
			{
				for (int i = 0; i < list.size(); i++)
				{
					Staff tmpStaff = list.get(i);
					tmpStaff.getDept().getId();
				}
			}
		};
		
		this.loadPagination(this.masterService.totalByExample(Staff.class, null, customCriteriaHandler));
		this.staffList = this.masterService.findByExample(
				Staff.class, 
				null,
				getPage().getOffset(), 
				getPage().getInterval(),
				customCriteriaHandler, 
				customLazyHandler, 
				Order.asc("code"));

		return "getSearchResultPage";
	}
	

	
	
	public void validateUpdateClothOfStaff()
	{
		Cloth clothInDb = (Cloth) this.getSession().get(SESSION_KEY_EDIT_STAFF_CLOTH);
		
		final String tmpCode = this.cloth.getCode();
		final String tmpRfid = this.cloth.getRfid();
		final Long staffId = this.staff.getId();
		final Long clothTypeId = this.cloth.getClothType().getId();
		String inputErrorMsg = null;
		
		////////////////////////////////////////////////////
		// Check Code
		////////////////////////////////////////////////////
		if (tmpCode == null || tmpCode.isEmpty())
		{
			inputErrorMsg = String.format(getText("errors.custom.required"), getText("cloth.code"));
			addActionError( inputErrorMsg );
		}
		else if (tmpCode.equals(clothInDb.getCode()) && clothTypeId.equals(clothInDb.getClothType().getId()))
		{
			// Nothing to do, used to skip following
		}
		else 
		{
			Cloth tmpCloth = null;
			String tmpStr = null;
			
			// Check clothes already in DB (return json need this)
			this.clothListInDb = (ArrayList<Cloth>) getSession().get(SESSION_KEY_DB_PATTERN);
			if (this.clothListInDb != null)
			{
				// The code of clothes of this staff in DB
				for (int i = 0; i < this.clothListInDb.size(); i++)
				{
					// cloth code need not to be unique in entire DB but must be unique under same staff, same clothType 
					tmpCloth = this.clothListInDb.get(i);
					if (tmpCloth.getCode().equals(tmpCode) && tmpCloth.getClothType().getId().equals(clothTypeId))
					{
						inputErrorMsg = String.format(getText("errors.custom.already.existed"), getText("cloth.code"), tmpCode);
						addActionError( inputErrorMsg );
						break;
					}
				}
			}
			
			// Check newly added clothes (not in DB yet)
			this.clothListAdded = (ArrayList<Cloth>) getSession().get(SESSION_KEY_NEW_PATTERN);
			if (this.clothListAdded != null)
			{
				// The code of clothes of this staff already added but not in DB
				for (int i = 0; i < this.clothListAdded.size(); i++)
				{
					// cloth code need not to be unique in entire DB but must be unique under same staff, same clothType 
					tmpCloth = this.clothListAdded.get(i);
					if (tmpCloth.getCode().equals(tmpCode) && tmpCloth.getClothType().getId().equals(clothTypeId))
					{
						inputErrorMsg = String.format(getText("errors.custom.already.existed"), getText("cloth.code"), tmpCode);
						addActionError( inputErrorMsg );
						break;
					}
				}
			}
		}
		
		
		////////////////////////////////////////////////////
		// Check RFID
		////////////////////////////////////////////////////
		if (tmpRfid == null || tmpRfid.isEmpty())
		{
			inputErrorMsg = String.format(getText("errors.custom.required"), getText("label.rfid"));
			addActionError( inputErrorMsg );
		}
		else if (clothInDb.getRfid().equals(tmpRfid))
		{
			// DO NOT remove this line!!! (by Horace)
			// Nothing to do (use to skip next else-if)
			// if here is true, means rfid of this cloth is same as before
		}
//		else if (this.isClothFieldValueAlreadyInDb(COLUMN_RFID, tmpRfid))
//		{
//			// check RFID in entire DB
//			inputErrorMsg = String.format(getText("errors.custom.already.existed"), getText("label.rfid"), tmpRfid);
//			addActionError( inputErrorMsg );
//		}
		else
		{
			boolean rfidDupl = false;
			
			// check RFID in newly-added cloth of this staff (not in DB yet)
			this.clothListAdded = (ArrayList<Cloth>) getSession().get(SESSION_KEY_NEW_PATTERN);
			if (this.clothListAdded != null)	// In fact, at least call checkPattern() once will set SESSION_KEY_NEW_PATTERN (by Horace)
			{
				int size = this.clothListAdded.size();
				Cloth c = null;
				for (int i = 0; i < size; i++)
				{
					c = this.clothListAdded.get(i);
					if (c.getRfid().equals(tmpRfid))
					{
						rfidDupl = true;
						break;
					}
				}
			}
			
			// check RFID in already-existed cloth of this staff (already in DB)
			this.clothListInDb = (List<Cloth>) getSession().get(SESSION_KEY_DB_PATTERN);
			if (!rfidDupl && this.clothListInDb != null)	// In fact, at least call checkPattern() once will set SESSION_KEY_NEW_PATTERN (by Horace)
			{
				int size = this.clothListInDb.size();
				Cloth c = null;
				for (int i = 0; i < size; i++)
				{
					c = this.clothListInDb.get(i);
					if (c.getRfid().equals(tmpRfid))
					{
						rfidDupl = true;
						break;
					}
				}
			}
			
			if (!rfidDupl)
			{
				// compare RFID with other staff's cloth
				Integer n = this.masterService.totalByExample(Cloth.class, null, 
						new CustomCriteriaHandler<Cloth>()
						{
							@Override
							public void makeCustomCriteria(Criteria criteria)
							{
								criteria.add(Restrictions.eq(COLUMN_RFID, tmpRfid) );
								Criteria staffCriteria = criteria.createCriteria("staff");
								staffCriteria.add(Restrictions.ne("id", staffId));
							}
						}
				);
				if (n > 0)
				{
					rfidDupl = true;
				}
			}
			
			
			if (rfidDupl)
			{
				inputErrorMsg = String.format(getText("errors.custom.already.existed"), getText("label.rfid"), tmpRfid);
				addActionError( inputErrorMsg );
			}
		}
	}
	
	public String updateClothOfStaff()
	{
		//System.out.println("Update a Cloth of a Staff");
		Long staffId = this.staff.getId();
		Long clothId = this.cloth.getId();
		
		Long tmpClothTypeId = this.cloth.getClothType().getId();
		ClothType tmpClothType = getMasterService().get(ClothType.class, tmpClothTypeId);
		String tmpClothSize = this.cloth.getSize();
		String tmpRfid = this.cloth.getRfid();
		String tmpRemark= this.cloth.getRemark();
		String tmpCode = this.cloth.getCode();
		
		//System.out.println("staffId: " + staffId);
		//System.out.println("clothId: " + clothId);
		//System.out.println("tmpRfid: " + tmpRfid);
		//System.out.println("tmpRemark: " + tmpRemark);
		
		Cloth clothToBeUpdated = (Cloth) this.getSession().get(SESSION_KEY_EDIT_STAFF_CLOTH);
		clothToBeUpdated.setCode(tmpCode);
		clothToBeUpdated.setClothType(tmpClothType);
		clothToBeUpdated.setSize(tmpClothSize);
		
		//Record the original "rfid" for detecting the change in "onBeforeSave()" and "OnBeforeUpdate()"
		clothToBeUpdated.setOldRfid( clothToBeUpdated.getRfid() );
		clothToBeUpdated.setRfid(tmpRfid);
		//Record the original "rfid" for detecting the change in "onBeforeSave()" and "OnBeforeUpdate()"
		
		clothToBeUpdated.setRemark(tmpRemark);
		
		// For clothes already in DB
		this.clothListInDb = (ArrayList<Cloth>) getSession().get(SESSION_KEY_DB_PATTERN);
		if (this.clothListInDb == null)
		{
			this.clothListInDb = new ArrayList<Cloth>();
		}
		
		// For Newly added clothes (not in DB yet)
		this.clothListAdded = (ArrayList<Cloth>) getSession().get(SESSION_KEY_NEW_PATTERN);
		if (this.clothListAdded == null)
		{
			this.clothListAdded = new ArrayList<Cloth>();
		}
		
		return "clothListJson";
	}
	
	public String voidCloth()
	{
		//System.out.println("Void Clothes: " + this.voidClothIdList.size());
		
		// For clothes already in DB
		this.clothListInDb = (ArrayList<Cloth>) getSession().get(SESSION_KEY_DB_PATTERN);
		if (this.clothListInDb == null)
		{
			this.clothListInDb = new ArrayList<Cloth>();
		}
		else
		{
			for (int i = 0; i < voidClothIdList.size(); i++)
			{
				Long clothId = voidClothIdList.get(i);
				Cloth tmpCloth = this.searchClothById(this.clothListInDb, clothId);
				tmpCloth.setClothStatus(ClothStatus.Void);
				tmpCloth.setDisplayField(this.getText(ClothStatus.Void.getValue()));
			}
		}
		
		// For Newly added clothes (not in DB yet)
		this.clothListAdded = (ArrayList<Cloth>) getSession().get(SESSION_KEY_NEW_PATTERN);
		if (this.clothListAdded == null)
		{
			this.clothListAdded = new ArrayList<Cloth>();
		}
		
		return "clothListJson";
	}
	
	
	public String checkPattern()
	{
		//System.out.println("checkPattern: Add " + this.clothList.size() + " clothes to Staff");
		
		// used to check cloth code and RFID duplication
		List<String> clothCodeAdded = new ArrayList<String>();	// cloth code in fieldset "CLOTH LIST" (unique in a staff)
		List<String> clothRfidAdded = new ArrayList<String>();	// cloth RFID in fieldset "CLOTH LIST" (unique in entire DB)
		Cloth tmpCloth = null;
		String tmpStr = null;
		
		// For clothes already in DB (return json need this)
		this.clothListInDb = (ArrayList<Cloth>) getSession().get(SESSION_KEY_DB_PATTERN);
		if (this.clothListInDb == null)
		{
			this.clothListInDb = new ArrayList<Cloth>();
		}
		else
		{
			// The code of clothes of this staff in DB
			for (int i = 0; i < this.clothListInDb.size(); i++)
			{
				// cloth code need not to be unique in entire DB but must be unique under same staff, same clothType 
				tmpCloth = this.clothListInDb.get(i);
				tmpStr = tmpCloth.getCode() + CODE_TYPE_DELIMETER + tmpCloth.getClothType().getName();
				clothCodeAdded.add(tmpStr);
				
				
				// RFID must be unique in entire DB, so no need to add here but later give a DB query check (by Horace)
//				clothRfidAdded.add(this.clothListInDb.get(i).getRfid());
			}
		}
		
		// For Newly added clothes (not in DB yet, return json need this)
		this.clothListAdded = (ArrayList<Cloth>) getSession().get(SESSION_KEY_NEW_PATTERN);
		if (this.clothListAdded == null)
		{
			this.clothListAdded = new ArrayList<Cloth>();
		}
		else
		{
			// The code and RFID of clothes of this staff already added but not in DB
			for (int i = 0; i < this.clothListAdded.size(); i++)
			{
				tmpCloth = this.clothListAdded.get(i);
				tmpStr =  tmpCloth.getCode() + CODE_TYPE_DELIMETER + tmpCloth.getClothType().getName();
				clothCodeAdded.add(tmpStr);
				
				// need this to check between user-input-RFID and already-added-RFID 
				clothRfidAdded.add(this.clothListAdded.get(i).getRfid());
			}
		}
		
		
		HashMap<String, String> codeCounter = new HashMap<String, String>();	// map<code, lineNumberStr>: count num of each cloth code in user input
		HashMap<String, String> rfidCounter = new HashMap<String, String>();	// map<rfid, lineNumberStr>: count num of each cloth rfid in user input
		String clothStatusStr = null;
		for (int i = 0; i < this.clothList.size(); i++)
		{
			tmpCloth = this.clothList.get(i);
			clothStatusStr = this.getText(tmpCloth.getClothStatus().getValue());
			tmpCloth.setDisplayField(clothStatusStr);
			
			ClothType tmpClothType = this.masterService.get(ClothType.class, tmpCloth.getClothType().getId());
			tmpCloth.setClothType(tmpClothType);
			
//			String tmpSize = tmpCloth.getSize();
			String tmpCode = tmpCloth.getCode();
			tmpStr = tmpCloth.getCode() + CODE_TYPE_DELIMETER + tmpCloth.getClothType().getName();
			String tmpRfid = tmpCloth.getRfid();
//			ClothStatus tmpClothStatus = tmpCloth.getClothStatus();
//			String tmpRemark = tmpCloth.getRemark();
			
			
			String lineNumber = String.format( getText("error.at.line.colon"),  ""+(i+1) );
			String inputErrorMsg = null;
			if (tmpCode == null || tmpCode.isEmpty())
			{
				inputErrorMsg = String.format(getText("errors.custom.required"), getText("cloth.code"));
				addActionError( lineNumber + inputErrorMsg );
			}
			else if (clothCodeAdded.contains(tmpStr))
			{
				inputErrorMsg = String.format(getText("errors.custom.already.existed"), getText("cloth.code"), tmpStr);
				addActionError( lineNumber + inputErrorMsg );
			}
//			else if (this.isClothFieldValueAlreadyInDb(COLUMN_CODE, tmpCode))
//			{
//				inputErrorMsg = String.format(getText("errors.custom.already.existed"), getText("cloth.code"), tmpCode);
//				addActionError( lineNumber + inputErrorMsg );
//			}
			
			
			if (tmpRfid == null || tmpRfid.isEmpty())
			{
				inputErrorMsg = String.format(getText("errors.custom.required"), getText("label.rfid"));
				addActionError( lineNumber + inputErrorMsg );
			}
			else if (clothRfidAdded.contains(tmpRfid))
			{
				inputErrorMsg = String.format(getText("errors.custom.already.existed"), getText("label.rfid"), tmpRfid);
				addActionError( lineNumber + inputErrorMsg );
			}
			else if (this.isClothFieldValueAlreadyInDb(COLUMN_RFID, tmpRfid))
			{
				// check RFID in entire DB
				inputErrorMsg = String.format(getText("errors.custom.already.existed"), getText("label.rfid"), tmpRfid);
				addActionError( lineNumber + inputErrorMsg );
			}
			
			/////////////////////////////////////////////////////
			// User Input statistic (by Horace)
			/////////////////////////////////////////////////////
			lineNumber = getText("label.line") + " " + (i+1);
			// Cloth Code statistic
			if (tmpCode != null && !tmpCode.isEmpty())
			{
				if (codeCounter.containsKey(tmpStr))
				{
					codeCounter.put(tmpStr, codeCounter.get(tmpStr) + ", " + (i+1));
				}
				else
				{
					codeCounter.put(tmpStr, lineNumber);
				}
			}
			// Cloth RFID statistic
			if (tmpRfid != null && !tmpRfid.isEmpty())
			{
				if (rfidCounter.containsKey(tmpRfid))
				{
					rfidCounter.put(tmpRfid, rfidCounter.get(tmpRfid) + ", " + (i+1));
				}
				else
				{
					rfidCounter.put(tmpRfid, lineNumber);
				}
			}
		}
		
		///////////////////////////////////////////////////////////////
		// check user input: any duplicate cloth code (by Horace)
		///////////////////////////////////////////////////////////////
		Iterator<String> it1 = codeCounter.keySet().iterator();
		String code = null;
		String lineStr = null;
		while (it1.hasNext())
		{
			code = it1.next();
			lineStr = codeCounter.get(code);
			
			// if contains "," means duplicate
			if ( lineStr.contains(",") )
			{
				addActionError( lineStr + " " +  getText("errors.has.duplicate") + " " + getText("cloth.code") + " '" + code + "'");
			}
		}
		///////////////////////////////////////////////////////////////
		// check user input: any duplicate cloth RFID (by Horace)
		///////////////////////////////////////////////////////////////
		it1 = rfidCounter.keySet().iterator();
		String rfid = null;
		while (it1.hasNext())
		{
			rfid = it1.next();
			lineStr = rfidCounter.get(rfid);
			
			// if contains "," means duplicate
			if ( lineStr.contains(",") )
			{
				addActionError( lineStr + " " +  getText("errors.has.duplicate") + " " + getText("label.rfid") + " '" + rfid + "'");
			}
		}
		
		
		if (hasActionErrors())
		{
			return "jsonValidateResult";
		}
		else
		{
			this.clothListAdded.addAll(clothList);
			getSession().put(SESSION_KEY_NEW_PATTERN, this.clothListAdded);
			return "clothListJson";
		}
	}
	
	
	
	
	
	////////////////////////////////////////////////
	// Utility Methods
	////////////////////////////////////////////////	
	
	// Manually clear Session Var
	private void resetSessionVariables()
	{
		getSession().put(SESSION_KEY_EDIT_STAFF, null);
		getSession().put(SESSION_KEY_DB_PATTERN, null);
		getSession().put(SESSION_KEY_NEW_PATTERN, null);
		getSession().put(SESSION_KEY_EDIT_STAFF_CLOTH, null);
	}
	
	private List<ClothType> getEnableClothType()
	{
		List<ClothType> clothTypeList = masterService.findByExample(ClothType.class, null, null, null, 
				
				new CustomCriteriaHandler<ClothType>()
				{
					@Override
					public void makeCustomCriteria(Criteria criteria)
					{
						criteria.add(Restrictions.eq("enable", true));
					}
				},
				
				null, Order.asc("id"));
		
		return clothTypeList;
	}
	
	private List<ClothType> getAllClothType()
	{
		return masterService.findAll(ClothType.class, null, null, null, Order.asc("id"));
	}
	
	
	// get Staff from DB by ID
	private Staff getStaffById(final Long id)
	{
		Staff tmpStaff = this.masterService.get(Staff.class, id, 
				
				new CustomLazyHandler<Staff>()
				{
					@Override
					public void LazyObject(Staff staff)
					{
						staff.getDept().getId();
						
						if (staff.getClothSet() != null && staff.getClothSet().size() > 0)
						{
							ArrayList<Cloth> tmpClothList = new ArrayList<Cloth>(staff.getClothSet()); 
							for (int i = 0; i < tmpClothList.size(); i++)
							{
								tmpClothList.get(i).getClothType().getId();
							}
						}
					}
				}
		);
		
		return tmpStaff;
	}

	// Search cloth by id in an cloth list 
	private Cloth searchClothById(List<Cloth> list, Long clothId)
	{
		Cloth tmpCloth = null;
		for (int i = 0; i < list.size(); i++)
		{
			tmpCloth = list.get(i);
			if (tmpCloth.getId().compareTo(clothId) == 0)
			{
				return tmpCloth;
			}
		}
		
		return null;
	}
	
	/* 
	 * Check whether a column value already existed in DB (by Horace)
	 * - FIELD_NAME: name of the field, plz use the const already defined in this class 
	 * - FIELD_VALUE: value if the field
	 * e.g. if find is there a staff has a code "aaa" in DB, 
	 * 		boolean isExist = isFieldValueAlreadyInDb(StaffAction.CODE, "aaa");
	 */
	// check Staff field
	private boolean isStaffFieldValueAlreadyInDb(final String FIELD_NAME, final String FIELD_VALUE)
	{
		Integer n = this.masterService.totalByExample(Staff.class, new Staff(), 
				new CustomCriteriaHandler<Staff>()
				{
					@Override
					public void makeCustomCriteria(Criteria criteria)
					{
						criteria.add(Restrictions.eq(FIELD_NAME, FIELD_VALUE) );
					}
				}
		);
		
//		System.out.println("StaffField(" + FIELD_NAME + " = " + FIELD_VALUE + "): " +  + n + " found in DB!");
		
		if (n > 0)
			return true;
		else
			return false;
	}
	// check Cloth field
	private boolean isClothFieldValueAlreadyInDb(final String FIELD_NAME, final String FIELD_VALUE)
	{
		Integer n = this.masterService.totalByExample(Cloth.class, new Cloth(), 
				new CustomCriteriaHandler<Cloth>()
				{
					@Override
					public void makeCustomCriteria(Criteria criteria)
					{
						criteria.add(Restrictions.eq(FIELD_NAME, FIELD_VALUE) );
					}
				}
		);
		
		//System.out.println("ClothField(" + FIELD_NAME + " = " + FIELD_VALUE + "): "  + n + " found in DB!");
		
		if (n > 0)
			return true;
		else
			return false;
	}



    //////////////////////////////////////////
    // Staff Utility
    //////////////////////////////////////////

	//New Requirement
	public String getKioskStaffByCardNumber()
	{
		this.setTilesKey("uniqueStaffJson");
		
		return getStaffByCardNumber();
	}
	
	public String getStaffByCardNumber() {
		
		if ( dummyStaffForAC != null ) {
			dummyStaffForAC = getStaffByCardNumberImpl(getStaffListDefaultCriteriaHandler(true, dummyStaffForAC), getStaffListDefaultLazyHandler());
		}

		if ( this.getTilesKey().trim().isEmpty() ) 
			this.setTilesKey("uniqueStaffJson");			//if it is empty, use back its original setting

		return this.getTilesKey();
	}

	public Staff getStaffByCardNumberImpl(CustomCriteriaHandler<Staff> customCriteriaHandler, CustomLazyHandler<Staff> customLazyHandler) {

		this.suggestStaffList = this.getMasterService().findByExample(Staff.class, null, null, null, customCriteriaHandler, customLazyHandler, null);

		if ( this.suggestStaffList.size() != 1 )
			return null;
		else
			return this.suggestStaffList.get(0);
	}
	//New Requirement
	
	public CustomCriteriaHandler<Staff> getStaffListDefaultCriteriaHandler(final boolean enable, final Staff staff) {
		
		return 
				new CustomCriteriaHandler<Staff>()
				{
					@Override
					public void makeCustomCriteria(Criteria baseCriteria) {
						
						if ( staff != null ) {
							
							if ( staff.getCode() != null && staff.getCode().trim().isEmpty() == false ) {
								baseCriteria.add( Restrictions.eq("code", staff.getCode().trim()) );
							}
							
							if ( staff.getCardNumber() != null && staff.getCardNumber().trim().isEmpty() == false )
							{
								final String cardId = computeCardId(staff.getCardNumber());
								baseCriteria.add( Restrictions.eq("cardNumber", cardId) );
							}
						}
						
						baseCriteria.add( Restrictions.eq("enable", enable) );
					}
				};
	}

	// Auto Complete (Ajax JSON)
	public String getKioskStaffCodeAutoCompleteList()
	{
		this.setTilesKey("staffCodeAutoCompleteJson");
		
		return getStaffCodeAutoCompleteList();
	}
	
	// Auto Complete (Ajax JSON)
	public String getStaffCodeAutoCompleteList()
	{
		if (dummyStaffForAC == null)
			dummyStaffForAC = new Staff();
		dummyStaffForAC.setEnable(true);
		//dummyStaffForAC.setStaffStatus( StaffStatus.Normal );		//Only Non-leaved Staff??

		return getStaffCodeAutoCompleteListImpl(dummyStaffForAC, null, null, null, getStaffListDefaultLazyHandler(), Order.asc("code"));
	}

	public CustomLazyHandler<Staff> getStaffListDefaultLazyHandler() {
		
		return
				new CustomLazyHandler<Staff>()
				{
					@Override
					public void LazyList(List<Staff> list) {
						
						Iterator<Staff> it = list.iterator();
						while( it.hasNext() )
						{
							Staff obj = it.next();
							
							obj.getDept().getNameEng();
							obj.getClothSet().size();

							for (Iterator<Cloth> iterator = obj.getClothSet().iterator(); iterator.hasNext();)
							{
								Cloth cloth = iterator.next();
								cloth.getClothType().getName();
							}
						}
					}
				};
	}

	public String getStaffCodeAutoCompleteListImpl(Staff dummyStaffForAC, Integer offset, Integer interval, CustomCriteriaHandler<Staff> customCriteriaHandler, CustomLazyHandler<Staff> customLazyHandler, Order orderBy) {
		
		this.suggestStaffList = this.getMasterService().findByExample(Staff.class, dummyStaffForAC, offset, interval, customCriteriaHandler, customLazyHandler, orderBy);
		
		if ( this.getTilesKey().trim().isEmpty() ) 
			this.setTilesKey("staffCodeAutoCompleteJson");			//if it is empty, use back its original setting

		return this.getTilesKey();
	}



	public Staff getStaff()
	{
		return staff;
	}
	public void setStaff(Staff staff)
	{
		this.staff = staff;
	}
	public List<Department> getDeptList()
	{
		return deptList;
	}
	public void setDeptList(List<Department> deptList)
	{
		this.deptList = deptList;
	}
	public List<String> getPositionList()
	{
		return positionList;
	}
	public void setPositionList(List<String> positionList)
	{
		this.positionList = positionList;
	}
	public List<StaffStatus> getStaffStatusList()
	{
		return staffStatusList;
	}
	public void setStaffStatusList(List<StaffStatus> staffStatusList)
	{
		this.staffStatusList = staffStatusList;
	}
	public List<Cloth> getClothList()
	{
		return clothList;
	}
	public void setClothList(List<Cloth> clothList)
	{
		this.clothList = clothList;
	}
	public List<ClothType> getClothTypeList()
	{
		return clothTypeList;
	}
	public void setClothTypeList(List<ClothType> clothTypeList)
	{
		this.clothTypeList = clothTypeList;
	}
	public List<String> getClothSizeList()
	{
		return clothSizeList;
	}
	public void setClothSizeList(List<String> clothSizeList)
	{
		this.clothSizeList = clothSizeList;
	}
	public List<ClothStatus> getClothStatusList()
	{
		return clothStatusList;
	}
	public void setClothStatusList(List<ClothStatus> clothStatusList)
	{
		this.clothStatusList = clothStatusList;
	}
	public List<Cloth> getClothListAdded()
	{
		return clothListAdded;
	}
	public void setClothListAdded(List<Cloth> clothListAdded)
	{
		this.clothListAdded = clothListAdded;
	}
	public List<Staff> getStaffList()
	{
		return staffList;
	}
	public void setStaffList(List<Staff> staffList)
	{
		this.staffList = staffList;
	}
	public List<Staff> getSelectedStaffList()
	{
		return selectedStaffList;
	}
	public void setSelectedStaffList(List<Staff> selectedStaffList)
	{
		this.selectedStaffList = selectedStaffList;
	}
	public Cloth getCloth()
	{
		return cloth;
	}
	public void setCloth(Cloth cloth)
	{
		this.cloth = cloth;
	}
	public List<Cloth> getClothListInDb()
	{
		return clothListInDb;
	}
	public void setClothListInDb(List<Cloth> clothListInDb)
	{
		this.clothListInDb = clothListInDb;
	}
	public List<Long> getVoidClothIdList()
	{
		return voidClothIdList;
	}
	public void setVoidClothIdList(List<Long> voidClothIdList)
	{
		this.voidClothIdList = voidClothIdList;
	}
	public List<TrueFalseBoolean> getBooleanList()
	{
		return booleanList;
	}
	public void setBooleanList(List<TrueFalseBoolean> booleanList)
	{
		this.booleanList = booleanList;
	}
	public List<Staff> getSuggestStaffList()
	{
		return suggestStaffList;
	}
	public void setSuggestStaffList(List<Staff> suggestStaffList)
	{
		this.suggestStaffList = suggestStaffList;
	}
	public Staff getDummyStaffForAC()
	{
		return dummyStaffForAC;
	}
	public void setDummyStaffForAC(Staff dummyStaffForAC)
	{
		this.dummyStaffForAC = dummyStaffForAC;
	}
	public List<YesNoInt> getEnableStatusList()
	{
		return enableStatusList;
	}
	public void setEnableStatusList(List<YesNoInt> enableStatusList)
	{
		this.enableStatusList = enableStatusList;
	}
	public Integer getEnableStatus()
	{
		return enableStatus;
	}
	public void setEnableStatus(Integer enableStatus)
	{
		this.enableStatus = enableStatus;
	}
}

class ClothComparator implements Comparator<Cloth>
{
	@Override
	public int compare(Cloth o1, Cloth o2)
	{
		return o1.getCode().compareTo(o2.getCode());
	}
}

class EditPageDbPatternComparator implements Comparator<Cloth>
{
	@Override
	public int compare(Cloth o1, Cloth o2)
	{
		String typeName1 = o1.getClothType().getName();
		String typeName2 = o2.getClothType().getName();
		int r = typeName1.compareTo(typeName2);
		if (r != 0)
		{
			return r;
		}
		else
		{
			String clothCode1 = o1.getCode();
			String clothCode2 = o2.getCode();
			r = clothCode1.compareTo(clothCode2);
			return r;
		}
	}
}