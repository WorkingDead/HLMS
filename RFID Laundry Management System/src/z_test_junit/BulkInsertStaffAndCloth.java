package z_test_junit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import module.dao.BeansFactoryApplication;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.Department;
import module.dao.master.Staff;
import module.dao.master.Cloth.ClothStatus;
import module.dao.master.Staff.StaffStatus;
import module.dao.system.Users;
import module.service.ServiceFactory;
import module.service.all.MasterService;
import module.service.all.SystemService;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import web.actions.BaseActionSecurity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:databaseContext.xml", "classpath:applicationContext.xml"})
public class BulkInsertStaffAndCloth {

	@Resource(name=ServiceFactory.MasterService)
	public MasterService masterService;
	
	@Resource(name=ServiceFactory.SystemService)
	private SystemService systemService;
	
	@Resource(name=BeansFactoryApplication.BEANNAME)
	private BeansFactoryApplication beansFactoryApplication;
	
	@Test
	public void test()
	{
		System.out.println("###############################################");
		System.out.println("Bulk Insert Staff and Their Clothes");
		System.out.println("###############################################");
		
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
		
		String allCharacters[] = {
									"a",
									"b",
									"c",
									"d",
									"e",
									"f",
									"g",
									"h",
									"i",
									"j",
									"k",
									"l",
									"m",
									"n",
									"o",
									"p",
									"q",
									"r",
									"s",
									"t",
									"u",
									"v",
									"w",
									"x",
									"y",
									"z"
									};
		String exampleNameArray[] = new String[10000];		//Fail To Insert 100000 In This Way
		Random rn = new Random();
		int minimum = 0;
		int maximum = 25;
		Map<String, String> map = new HashMap<String, String>();
		
		for ( int i = 0; i < exampleNameArray.length; i++ ) {
			
//			int randomNum = minimum + rn.nextInt(maximum - minimum + 1);
//			System.out.println("randomNum = " + randomNum);
			
			while ( true ) {
				
				String string = allCharacters[minimum + rn.nextInt(maximum - minimum + 1)]
						+ allCharacters[minimum + rn.nextInt(maximum - minimum + 1)]
						+ allCharacters[minimum + rn.nextInt(maximum - minimum + 1)]
						+ allCharacters[minimum + rn.nextInt(maximum - minimum + 1)]
						+ allCharacters[minimum + rn.nextInt(maximum - minimum + 1)];
				
				
				if ( map.containsKey( string ) == false ) {
					map.put(string, string);
					break;
				}
				else {
				}
			}
		}
		
		exampleNameArray = map.values().toArray(new String[0]);
		
//		String exampleNameArray[] = {"aaaaa", "bbbbb", "ccccc", 
//				"ddd", "eee", "fff", "ggg", 
//				"hhh", "iii", "jjj", "kkk", "lll", "mmm", "nnn",
//				"ooo", "ppp", "qqq", "rrr", "sss", "ttt", "uuu",
//				"vvv", "www", 
//				"xxx", "yyy", "zzz"
//				};
		
		Integer numOfDept = 3;
		List<Department> deptList = masterService.findAll(Department.class, null, numOfDept, null, Order.asc("id"));
		int deptPtr = 0;

		Collection<String> c = beansFactoryApplication.getgetAllStaffPositionInMap().values();
		String staffPositionArray[] = c.toArray( new String[ c.size() ] );
		int postPtr = 0;
		
		String allClothSize = beansFactoryApplication.getAllClothSize();
		String exampleSizeArray[] = allClothSize.split(",");
		int sizePtr = 0;
		
		Integer numOfType = 6;
		List<ClothType> typeList = masterService.findAll(ClothType.class, null, numOfType, null, Order.asc("name"));
		int typePtr = 0;
		int numOfClothPerStaff = typeList.size();
		
		final String RFID_PREFIX = "30045678901234567890A";	// eg: 30045678901234567890A001
		int rfidBody = 0;
		
		
		ArrayList<Staff> saveList = new ArrayList<Staff>();
		for (int i = 0; i < exampleNameArray.length; i++)
		{
			String exampleName = exampleNameArray[i];
			Staff staff = new Staff();
			staff.setCode(exampleName);
			staff.setCardNumber(exampleName);
			staff.setNameCht(exampleName);
			staff.setNameEng(exampleName);
			staff.setDept(deptList.get(deptPtr));
			if (deptPtr < (deptList.size() - 1))
				deptPtr++;
			else
				deptPtr = 0;
			
			staff.setPosition(staffPositionArray[postPtr]);
			if (postPtr < (staffPositionArray.length - 1))
				postPtr++;
			else
				postPtr = 0;
			
			staff.setStaffStatus(StaffStatus.Normal);
			staff.setEnable(true);
			
			List<Cloth> list = new ArrayList<Cloth>();
			for (int j = 0; j < numOfClothPerStaff; j++)
			{
				Cloth cloth = new Cloth();
				
				cloth.setClothType(typeList.get(typePtr));
				if (typePtr < (typeList.size() - 1))
					typePtr++;
				else
					typePtr = 0;
				
				cloth.setSize(exampleSizeArray[sizePtr]);
				if (sizePtr < (exampleSizeArray.length - 1))
					sizePtr++;
				else
					sizePtr = 0;
				
				cloth.setCode(exampleName + (j+1));
				cloth.setRfid(RFID_PREFIX + genRfidBody(rfidBody));
				rfidBody++;
				cloth.setClothStatus(ClothStatus.Using);
				cloth.setRemark(exampleName + (j+1));
				cloth.setEnable(true);
				cloth.setCreatedBy(user);
				list.add(cloth);
			}
			
			typePtr = sizePtr = 0;
			
			staff.setClothSet(new HashSet<Cloth>(list));
			staff.setCreatedBy(user);
			System.out.println("Staff " + i + ": " + staff.getCode());
			saveList.add(staff);
		}
		
		masterService.saveList(Staff.class, saveList);
		
		System.out.println("Num of Saved: " + saveList.size() );
		System.out.println("Finish! \n\n");
	}
	
	// zero-padding for 3-digit number only
	private String genRfidBody(Integer n)
	{
		String s = "" + Integer.toHexString(n).toUpperCase();	// convert decimal to hex
		
		if (s.length() == 1)
			s = "00" + s;
		else if (s.length() == 2)
			s = "0" + s;
			
		return s;
	}
}
