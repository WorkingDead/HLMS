package z_test_junit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.Staff;
import module.dao.master.Cloth.ClothStatus;
import module.dao.master.Stf.StfOpe;
import module.dao.system.Users;

import module.service.ServiceFactory;
import module.service.all.GeneralService;
import module.service.all.MasterService;
import module.service.all.SystemService;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import web.actions.BaseActionSecurity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:databaseContext.xml", "classpath:applicationContext.xml"})
public class ImportClothToEachStaff {

	public MasterService masterService;
	
	@Resource(name=ServiceFactory.GeneralService)
	public GeneralService generalService;
	
	@Resource(name=ServiceFactory.SystemService)
	private SystemService systemService;



	private final int DATA_STARTING_ROW_ARRAY_INDEX = 1; // index of 1st row is 0
	private final int DATA_STARTING_ROW_OFFSET = 2;
	private final String FILE_NAME = "excel_import/customer.xls";



	public Staff getStaffRecordByStf(final String stf) throws Exception {
		
		List<Staff> staffList = masterService.findByExample(Staff.class, null, null, null, 

				new CustomCriteriaHandler<Staff>() {
					@Override
					public void makeCustomCriteria(Criteria criteria) {
						criteria.add( Restrictions.eq("code", stf) );
					}
				},
				
				new CustomLazyHandler<Staff>()
				{
					@Override
					public void LazyList(List<Staff> list)
					{
						Iterator<Staff> it = list.iterator();
						while (it.hasNext())
						{
							Staff staff = it.next();
							staff.getDept().getNameEng();
						}
					}
				}, 
				
				null);
		
		if ( staffList.size() == 1 )
			return staffList.get(0);
		else if ( staffList.size() > 1 ) {
			
			String staffId = "";
			Iterator<Staff> iterator = staffList.iterator();
			while ( iterator.hasNext() ) {
				
				if ( staffId.isEmpty() ) {
				}
				else {
					staffId = staffId + ", ";
				}
				
				Staff staff = iterator.next();
				staffId = staffId + staff.getId();
			}

			StfOpe stfOpe = StfOpe.FAILL;
			Exception exception = new Exception( stfOpe.toString() );
			throw exception;
		}
		else {

			StfOpe stfOpe = StfOpe.FAILN;
			Exception exception = new Exception( stfOpe.toString() );
			throw exception;
		}
	}
	
	private void saveListToDB(ArrayList<Staff> finalSaveList)
	{
		masterService.saveList(Staff.class, finalSaveList);
	}

	@Test
	public void test() throws Exception {

		Users user = systemService.findByExample(Users.class, null, null, null, 
				new CustomCriteriaHandler<Users>()
				{
					@Override
					public void makeCustomCriteria(Criteria baseCriteria)
					{
						baseCriteria.add(Restrictions.eq("username", BaseActionSecurity.AdminUserName));
					}
				}, 
				null, null).get(0);



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
			throw e;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw e;
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
			excel[j][11] = sheet.getCell(11, i).getContents();
			excel[j][12] = sheet.getCell(12, i).getContents();
			excel[j][13] = sheet.getCell(13, i).getContents();
			
			System.out.println("Line " + (j+DATA_STARTING_ROW_OFFSET) + ": " + excel[j][0] + "\t" + excel[j][1] + "\t" + excel[j][2] + "\t" +excel[j][3] + "\t" +excel[j][4]);
		}
		wb.close();



		/////////////////////////////////////////////////////////
		// Save to DB
		/////////////////////////////////////////////////////////
		ArrayList<Staff> saveList = new ArrayList<Staff>();

		int numOfRowExcel = numOfRow - 1;
		for (int i = 0; i < numOfRowExcel; i++)
		{
			String code = excel[i][0];
			String customerCode = excel[i][1];
			String oldRefCode = excel[i][2];
			String nameEng = excel[i][3];
			String nameChi = excel[i][4];
			String contactPerson = excel[i][5];
			String address = excel[i][6];
			String district = excel[i][7];
			String country = excel[i][8];
			String phone = excel[i][9];
			String fax = excel[i][10];
			String routeCode = excel[i][11];
			String forwarderCode = excel[i][12];
			String depts = excel[i][13];
			
			
			
			
			
			Staff staff = getStaffRecordByStf( code );

			
			
			
			
			
			
			List<Cloth> clothList = null;
			if ( depts != null && !depts.isEmpty() ) {
				
				clothList = new ArrayList<Cloth>();
				
				String deptSet[] = depts.split(";");
				for (int x = 0; x < deptSet.length; x++) {
					
					
					Cloth cloth = new Cloth();
					
					
					
					
					cloth.setClothStatus(ClothStatus.Using);

					cloth.setEnable(true);
					cloth.setCreatedBy(user);
					clothList.add(cloth);
				}
			}
			else {
				throw new Exception("No Cloth");
			}



			
			
			
			
			staff.setClothSet(new HashSet<Cloth>(clothList));
			staff.setModifiedBy(user);
			System.out.println("Staff " + i + ": " + staff.getCode());
			saveList.add(staff);
		}



		// Save to DB
		if (saveList.size() > 0)
		{
			this.saveListToDB(saveList);
		}

		System.out.println("Num of Saved: " + saveList.size() );
		System.out.println("Finished! \n\n");
	}
	
}
