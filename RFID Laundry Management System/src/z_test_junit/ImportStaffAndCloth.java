package z_test_junit;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.Assert;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import module.dao.BeansFactoryApplication;

import module.dao.iface.CustomCriteriaHandler;

import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.Department;
import module.dao.master.Staff;
import module.dao.master.Cloth.ClothStatus;
import module.dao.master.Staff.StaffStatus;
import module.dao.master.Stf.StfOpe;

import module.dao.system.Users;
import module.service.ServiceFactory;
import module.service.all.MasterService;
import module.service.all.SystemService;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import utils.convertor.StringConvertor;
import web.actions.BaseActionSecurity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:databaseContext.xml", "classpath:applicationContext.xml"})
public class ImportStaffAndCloth {

	@Resource(name=ServiceFactory.MasterService)
	public MasterService masterService;
	
	@Resource(name=ServiceFactory.SystemService)
	private SystemService systemService;
	
	@Resource(name=BeansFactoryApplication.BEANNAME)
	private BeansFactoryApplication beansFactoryApplication;



	private final static int stfLength = 7;
	private final static int stfCrdLength  = 5;
	
	private final static int clothCodeLength = 1;
	private final static int rfidLength = 24;
	
	private final static String defaultPosition = "Å@¤h";
	private final static StaffStatus defaultStaffStatus = StaffStatus.Normal;
	private final static boolean defaultStaffBoolean = true;
	
	private final static ClothStatus defaultClothStatus = ClothStatus.Using;
	private final static boolean defaultClothBoolean = true;

	private final static Map<String, String> stfMap = new HashMap<String, String>();
	private final static Map<String, String> stfCrdMap = new HashMap<String, String>();
	private final static Map<String, Map<String, Map<String, String>>> clothCodeMap = new HashMap<String, Map<String, Map<String, String>>>();
	private final static Map<String, String> rfidMap = new HashMap<String, String>();



	private final int DATA_STARTING_ROW_ARRAY_INDEX = 1; // index of 1st row is 0
	private final int DATA_STARTING_ROW_OFFSET = 2;
	private final String FILE_NAME = "excel_import/ImportAllNurse(Uniform).xls";



	@Test
	public void test()
	{
		System.out.println("###############################################");
		System.out.println("Importing Staff And Cloth");
		System.out.println("###############################################");



		/////////////////////////////////////////////////////////
		// Get data from Excel 
		/////////////////////////////////////////////////////////
		File f = new File(FILE_NAME);
		System.out.println("Path: " + f.getAbsolutePath());
		
		Workbook wb = null;
		try
		{
			wb = Workbook.getWorkbook(f);
		}
		catch (BiffException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		Sheet sheet = wb.getSheet(0);
		int numOfRow = sheet.getRows();
		int numOfCol = sheet.getColumns();
		
		String excel[][] = new String[numOfRow-1][numOfCol];
		for (int i = DATA_STARTING_ROW_ARRAY_INDEX; i < numOfRow; i++)
		{
			int j = i - 1;
			excel[j][0] = sheet.getCell(0, i).getContents();
			excel[j][1] = sheet.getCell(1, i).getContents();
			excel[j][2] = sheet.getCell(2, i).getContents();
			excel[j][3] = sheet.getCell(3, i).getContents();
			excel[j][4] = sheet.getCell(4, i).getContents();
			excel[j][5] = sheet.getCell(5, i).getContents();
			excel[j][6] = sheet.getCell(6, i).getContents();
			excel[j][7] = sheet.getCell(7, i).getContents();
			excel[j][8] = sheet.getCell(8, i).getContents();
			excel[j][9] = sheet.getCell(9, i).getContents();
			excel[j][10] = sheet.getCell(10, i).getContents();
			
			System.out.println("Line " + (j+DATA_STARTING_ROW_OFFSET) 
					+ ": " + excel[j][0]
					+ "\t" + excel[j][1]
					+ "\t" + excel[j][2]
					+ "\t" + excel[j][3]
					+ "\t" + excel[j][4]
					+ "\t" + excel[j][5]
					+ "\t" + excel[j][6]
					+ "\t" + excel[j][7]
					+ "\t" + excel[j][8]
					+ "\t" + excel[j][9]
					+ "\t" + excel[j][10]
							);
		}
		System.out.println();
		wb.close();



		List<Users> userList = systemService.findByExample(Users.class, null, null, null, 
				new CustomCriteriaHandler<Users>()
				{
					@Override
					public void makeCustomCriteria(Criteria baseCriteria)
					{
						baseCriteria.add(Restrictions.eq("username", BaseActionSecurity.AdminUserName));
					}
				}, 
				null, null);

		if ( userList.size() > 1 ) {
			Assert.fail("userList is larger than 1 and searchUsername = " + BaseActionSecurity.AdminUserName);
		}
		else if ( userList.size() < 1  ) {
			Assert.fail("userList is 0 and searchUsername = " + BaseActionSecurity.AdminUserName);
		}
		
		Users user = userList.get(0);



		/////////////////////////////////////////////////////////
		// Save to DB
		/////////////////////////////////////////////////////////
		ArrayList<Staff> saveList = new ArrayList<Staff>();
		ArrayList<String> errorList = new ArrayList<String>();

		int numOfRowExcel = numOfRow - 1;
		
		int numOfStaff = 0;
		String lastStaffId = "";
		Staff staff = null;

		for (int i = 0; i < numOfRowExcel; i++)
		{
			String departmentCht = excel[i][0].trim();
			String departmentEng = excel[i][1].trim();
			String staffId = excel[i][2].trim();
			String staffNameChi = excel[i][3].trim();
			String staffNameEng = excel[i][4].trim();
			String cardId = excel[i][5].trim();
			String clothType = excel[i][6].trim();
			String size = excel[i][7].trim();
			String clothCode = excel[i][8].trim();
			String rfid = excel[i][9].trim();
			String remark = excel[i][10].trim();

			try {

				if ( lastStaffId.equals( staffId ) ) {
					
					if ( staff == null ) {
						throw new Exception("staff = null or it fails to create this staff before.");
					}
					else {
						if ( staff.getClothSet() == null ) {
							throw new Exception("staff cloth set = null or it fails to create this staff before.");
						}
					}
				}
				else {
					
					numOfStaff = numOfStaff + 1;
					
					lastStaffId = staffId;
					
					staff = new Staff();
					
					staff.setCode( isStfUniqueInDatabase( validateSTF( staffId ) ) );
					staff.setCardNumber( isStfcrdUniqueInDatabase( StringConvertor.insertZero( validateSTFCRD( cardId ), stfCrdLength) ) );
					staff.setNameCht( validateSTFCNM( staffNameChi ) );
					staff.setNameEng( validateSTFNAM( staffNameEng ) );
					
					Department department = getDeptRecordByNameEngAndNameCht( validateDepartmentEng( departmentEng ), validateDepartmentCht( departmentCht ) );
					staff.setDept( department );
					
					//Default Record Value
					staff.setPosition( defaultPosition );
					staff.setStaffStatus( defaultStaffStatus );
					staff.setEnable( defaultStaffBoolean );
					//Default Record Value

					staff.setClothSet( new HashSet<Cloth>() );

					staff.setCreatedBy(user);
					System.out.println("Line " + (i+DATA_STARTING_ROW_OFFSET) + ": Staff code = " + staff.getCode());
					saveList.add(staff);
				}

				staff.getClothSet().add( createNewCloth( staff, clothType, size, clothCode, rfid, remark, user ) );
			}
			catch (Exception e) {

				//String errMsg = "Line " + (i+DATA_STARTING_ROW_OFFSET) + ": exception = " + convertStackTraceToString(e);
				String errMsg = "Line " + (i+DATA_STARTING_ROW_OFFSET) + ": exception = " + e.getMessage();
				errorList.add(errMsg);
				
				continue;
			}
		}
		System.out.println();



		if ( errorList != null && errorList.size() > 0 ) {
			// System.out.println("Num Of Failed: " + badList.size());
			System.out.println("Errors: ");
			for (int i = 0; i < errorList.size(); i++)
			{
				System.out.println(errorList.get(i));
			}
			System.out.println();

			Assert.fail( "errorList size = " +  errorList.size() );
		}
		else {
			
			if ( saveList.size() != numOfStaff ) {
				Assert.fail( "Before saving: saveList size = " +  saveList.size() + " NOT EQUAL to numofStaff = " + numOfStaff );
			}

			int saveListBeforeSize = saveList.size();

			try {
				masterService.saveList(Staff.class, saveList);
			}
			catch (Exception e) {
				Assert.fail( "e = " + e.getMessage() );
			}

			if ( saveList.size() > 0 ) {	//saveList size should be larger than 0

				if ( saveList.size() != numOfStaff ) {
					Assert.fail( "After saving: saveList size = " +  saveList.size() + " NOT EQUAL to numofStaff = " + numOfStaff );
				}
				else if ( saveListBeforeSize != saveList.size() ) {
					Assert.fail( "After saving: saveListBeforeSize = " +  saveListBeforeSize + " NOT EQUAL to saveListAfterSize = " + saveList.size() );
				}
				else {
					System.out.println("===============================================================");
					System.out.println("Num of Saved: " + saveList.size() );
					System.out.println("saveListBeforeSize: " + saveListBeforeSize);
					System.out.println("numOfStaff: " + numOfStaff);
					System.out.println();
					System.out.println("Error List Size: " + errorList.size() );
					System.out.println("===============================================================");
				}
			}
			else {
				Assert.fail( "After saving: saveList size = " +  saveList.size() );
			}
		}

		System.out.println("Finish! \n\n");
	}



	private Cloth createNewCloth(Staff staff, String clothType, String size, String clothCode, String rfid, String remark, Users user) throws Exception {

		Cloth cloth = new Cloth();

		cloth.setRfid( isRfidUnique( validateRfid( rfid ) ) );
		//cloth.setRemark( remark );		//Don't need to save it



		final String searchClothType = validateClothType( clothType );

		List<ClothType> clothTypeListInDB = masterService.findByExample(ClothType.class, null, null, null,

				new CustomCriteriaHandler<ClothType>()
				{
					@Override
					public void makeCustomCriteria(Criteria baseCriteria)
					{
						baseCriteria.add(Restrictions.eq("name", searchClothType));
					}
				}, 
				
				null, null);

		if ( clothTypeListInDB.size() > 1 ) {
			throw new Exception("clothTypeListInDB is larger than 1 and searchClothType = " + searchClothType);
		}
		else if ( clothTypeListInDB.size() < 1  ) {
			throw new Exception("clothTypeListInDB is 0 and searchClothType = " + searchClothType);
		}
		
		ClothType clothTypeInDB = clothTypeListInDB.get(0);
		if ( clothTypeInDB == null ) {
			throw new Exception("Cannot find cloth type and searchClothType = " + searchClothType);
		}
		else {
			cloth.setClothType( clothTypeInDB );
		}



		cloth.setCode( isClothCodeUniquePerPersonPerClothType( staff, clothTypeInDB, validateClothCode( clothCode ) ) );



		cloth.setSize( null );

		String allClothSize = beansFactoryApplication.getAllClothSize();
		String clothSizeArray[] = allClothSize.split(",");
		for ( int i = 0; i < clothSizeArray.length; i++ ) {
			if ( validateSize( size ).equals( clothSizeArray[i] ) ) {
				cloth.setSize( clothSizeArray[i] );
				break;
			}
		}
		
		if ( cloth.getSize() == null )
			throw new Exception("Cannot find cloth size and searchSize = " + size);



		//Default Record Value
		cloth.setClothStatus( defaultClothStatus );
		cloth.setEnable( defaultClothBoolean );
		
		cloth.setWashingCount( 0 );
		//Default Record Value
		
		cloth.setCreatedBy(user);
		
		return cloth;
	}

	private String isStfcrdUniqueInDatabase(String stfcrd) throws Exception {

		if ( stfCrdMap.containsKey( stfcrd ) ) {
			StfOpe stfOpe = StfOpe.FAILM;
			Exception exception = new Exception( stfOpe.toString() + " and stfcrd = " + stfcrd );
			throw exception;
		}
		else {
			stfCrdMap.put(stfcrd, stfcrd);
			return stfcrd;
		}
	}
	
	private String validateSTFCRD(String staffCardNumber) throws Exception {	//Check not null, not empty

		if ( staffCardNumber == null ) {
			Exception exception = new Exception( "staffCardNumber is null" );
			throw exception;
		}
		else if ( staffCardNumber.isEmpty() ) {
			Exception exception = new Exception( "staffCardNumber is empty" );
			throw exception;
		}
		else
			return staffCardNumber;
	}
	
	private Department getDeptRecordByNameEngAndNameCht(final String nameEng, final String nameCht) throws Exception {
		
		List<Department> deptList = masterService.findByExample(Department.class, null, null, null, 

				new CustomCriteriaHandler<Department>() {
					@Override
					public void makeCustomCriteria(Criteria criteria) {
						criteria.add( Restrictions.eq("nameEng", nameEng) );
						criteria.add( Restrictions.eq("nameCht", nameCht) );
					}
				}
				
				, null, null);

		if ( deptList.size() == 1 )
			return deptList.get(0);
		else if ( deptList.size() > 1 ) {
			
			String deptId = "";
			Iterator<Department> iterator = deptList.iterator();
			while ( iterator.hasNext() ) {
				
				if ( deptId.isEmpty() ) {
				}
				else {
					deptId = deptId + ", ";
				}
				
				Department dept = iterator.next();
				deptId = deptId + dept.getId();
			}

			StfOpe stfOpe = StfOpe.FAILH;
			Exception exception = new Exception( stfOpe.toString() + " and nameEng = " + nameEng + " and nameCht = " + nameCht );
//			log.error("getDeptRecordBySrvdsc: When SRVDSC = " + srvdsc +  " Record ID " + deptId);
//			log.error("getDeptRecordBySrvdsc: " + stfOpe.getDescription(), exception);
			throw exception;
		}
		else {

			StfOpe stfOpe = StfOpe.FAILI;
			Exception exception = new Exception( stfOpe.toString() + " and nameEng = " + nameEng + " and nameCht = " + nameCht );
//			log.error("getDeptRecordBySrvdsc: When SRVDSC = " + srvdsc);
//			log.error("getDeptRecordBySrvdsc: " + stfOpe.getDescription(), exception);
			throw exception;
		}
	}
	
	public String validateSTF(String stf) throws Exception {	//Check stfLength, not null, not empty
		
		if ( stf == null ) {
			throw new Exception( "stf is null" );
		}
		else if ( stf.isEmpty() ) {
			throw new Exception( "stf is empty" );
		}
		else if ( stf.length() != stfLength ) {
			throw new Exception( "stf length is not " + stfLength);
		}
		else
			return stf;
	}
	
	private String isStfUniqueInDatabase(String stf) throws Exception {

		if ( stfMap.containsKey( stf ) ) {
			StfOpe stfOpe = StfOpe.FAILL;
			Exception exception = new Exception( stfOpe.toString() + " and stf = " + stf );
			throw exception;
		}
		else {
			stfMap.put(stf, stf);
			return stf;
		}
	}
	
	private String validateSTFNAM(String STFNAM) throws Exception {	//Check not null, not empty
		
		if ( STFNAM == null ) {
			Exception exception = new Exception( "STFNAM is null" );
			throw exception;
		}
		else if ( STFNAM.isEmpty() ) {
			Exception exception = new Exception( "STFNAM is empty" );
			throw exception;
		}
		else
			return STFNAM;
	}
	
	private String validateSTFCNM(String STFCNM) throws Exception {		//Allow null, empty
		
		if ( STFCNM == null ) {
			return "";
		}
		else
			return STFCNM;
	}
	
	private String validateDepartmentEng(String nameEng) throws Exception {		//Check not null, not empty
		
		if ( nameEng == null ) {
			Exception exception = new Exception( "nameEng is null" );
			throw exception;
		}
		else if ( nameEng.isEmpty() ) {
			Exception exception = new Exception( "nameEng is empty" );
			throw exception;
		}
		else
			return nameEng;
	}
	
	private String validateDepartmentCht(String nameCht) throws Exception {		//Check not null, not empty
		
		if ( nameCht == null ) {
			Exception exception = new Exception( "nameCht is null" );
			throw exception;
		}
		else if ( nameCht.isEmpty() ) {
			Exception exception = new Exception( "nameCht is empty" );
			throw exception;
		}
		else
			return nameCht;
	}
	
	private String validateClothCode(String clothCode) throws Exception {	//Check clothCodeLength, not null, not empty

		if ( clothCode == null ) {
			Exception exception = new Exception( "clothCode is null" );
			throw exception;
		}
		else if ( clothCode.isEmpty() ) {
			Exception exception = new Exception( "clothCode is empty" );
			throw exception;
		}
		else if ( clothCode.length() != clothCodeLength ) {
			throw new Exception( "clothCode length is not " + clothCodeLength);
		}
		else
			return clothCode;
	}
	
	private String isClothCodeUniquePerPersonPerClothType(Staff staff, ClothType clothType, String clothCode) throws Exception {
		
		if ( clothCodeMap.containsKey( staff.getCode() ) ) {
			
			Map<String, Map<String, String>> staffMap = clothCodeMap.get( staff.getCode() );
			if ( staffMap.containsKey( clothType.getName() ) ) {
				
				Map<String, String> clothTypeMap = staffMap.get( clothType.getName() );
				if ( clothTypeMap.containsKey( clothCode ) ) {
					throw new Exception("clothCode found and clothType name = " + clothType.getName() + " and clothCode = " + clothCode);
				}
				else {
					clothTypeMap.put(clothCode, clothCode);
					return clothCode;
				}
			}
			else {
				
				Map<String, String> clothTypeMap = new HashMap<String, String>();
				clothTypeMap.put(clothCode, clothCode);
				
				staffMap.put(clothType.getName(), clothTypeMap);
				return clothCode;
			}
		}
		else {
			Map<String, String> clothTypeMap = new HashMap<String, String>();
			clothTypeMap.put(clothCode, clothCode);

			Map<String, Map<String, String>> staffMap = new HashMap<String, Map<String, String>>();
			staffMap.put(clothType.getName(), clothTypeMap);
			
			clothCodeMap.put(staff.getCode(), staffMap);
			return clothCode;
		}
	}
	
	private String validateRfid(String rfid) throws Exception {		//Check rfidLength, not null, not empty
		
		if ( rfid == null ) {
			Exception exception = new Exception( "rfid is null" );
			throw exception;
		}
		else if ( rfid.isEmpty() ) {
			Exception exception = new Exception( "rfid is empty" );
			throw exception;
		}
		else if ( rfid.length() != rfidLength ) {
			throw new Exception( "rfid length is not " + rfidLength);
		}
		else
			return rfid;
	}
	
	private String isRfidUnique(String rfid) throws Exception {		//Check unique
		
		if ( rfidMap.containsKey( rfid ) ) {
			throw new Exception("rfid found and rfid = " + rfid);
		}
		else {
			rfidMap.put(rfid, rfid);
			return rfid;
		}
	}
	
	private String convertStackTraceToString(Exception e) {
		
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	private String validateClothType(String clothType) throws Exception {	//Check not null, not empty
		
		if ( clothType == null ) {
			Exception exception = new Exception( "clothType is null" );
			throw exception;
		}
		else if ( clothType.isEmpty() ) {
			Exception exception = new Exception( "clothType is empty" );
			throw exception;
		}
		else
			return clothType;
	}
	
	private String validateSize(String size) throws Exception {	//Check not null, not empty
		
		if ( size == null ) {
			Exception exception = new Exception( "size is null" );
			throw exception;
		}
		else if ( size.isEmpty() ) {
			Exception exception = new Exception( "size is empty" );
			throw exception;
		}
		else
			return size;
	}
}
