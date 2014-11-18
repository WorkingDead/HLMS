package z_test_junit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Resource;
import module.dao.BeansFactoryApplication;
import module.dao.iface.CustomCriteriaHandler;
import module.dao.master.Cloth;
import module.dao.master.Cloth.ClothStatus;
import module.dao.master.ClothType;
import module.dao.master.Department;
import module.dao.master.Staff;
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
public class ImportStaff
{
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
		System.out.println("Importing Staff and their clothes");
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
		
//		String exampleNameArray[] = {"aaaaa", "bbbbb", "ccccc", 
//				"ddddd", "eeeee", "fffff", "ggggg", 
//				"hhhhh", "iiiii", "jjjjj", "kkkkk", "lllll", "mmmmm", "nnnnn",
//				"ooooo", "ppppp", "qqqqq", "rrrrr", "sssss", "ttttt", "uuuuu",
//				"vvvvv", "wwwww", 
//				"xxxxx", "yyyyy", "zzzzz"
//		};
		
		String exampleNameArray[] = new String[10];
		for ( int i = 0; i < exampleNameArray.length; i++)
		{
			exampleNameArray[i] = "a" + genNum(i);
		}
		
		
		Integer numOfDept = 5;
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
		int numOfClothPerStaff = 20;
		
		final String RFID_PREFIX = "3004567890123456789";	// eg: 30045678901234567890A001
		int rfidBody = 0;
		
		int numOfStaff = 0;
		int numOfCloth = 0;
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
			
			numOfStaff++;
			System.out.println("Creating staff [" + (i+1) + "]: '" + staff.getCode() + "'");
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
				cloth.setWashingCount(0);
				cloth.setWashingCountResetDate(Calendar.getInstance());
				list.add(cloth);
				
				numOfCloth++;
				System.out.println("\t Creating cloth " + numOfCloth + ": " + cloth.getRfid());
			}
			
			typePtr = sizePtr = 0;
			
			staff.setClothSet(new HashSet<Cloth>(list));
			staff.setCreatedBy(user);
//			saveList.add(staff);
			masterService.save(Staff.class, staff);
		}
		
//		masterService.saveList(Staff.class, saveList);
		
		System.out.println("Cloth Total: " + numOfCloth);
		System.out.println("Staff Total: " + numOfStaff);
//		System.out.println("Num of Saved: " + saveList.size() );
		System.out.println("Finish! \n\n");
	}
	
	// zero-padding for 4-digit number only
	private String genRfidBody(Integer n)
	{
		String s = "" + Integer.toHexString(n).toUpperCase();	// convert decimal to hex
		
		if (s.length() == 1)
			s = "0000" + s;
		else if (s.length() == 2)
			s = "000" + s;
		else if (s.length() == 3)
			s = "00" + s;
		else if (s.length() == 4)
			s = "0" + s;
			
		return s;
	}
	
	private String genNum(Integer n)
	{
		String s = "" + n;
		if (s.length() == 1)
			s = "000000" + s;
		else if (s.length() == 2)
			s = "00000" + s;
		else if (s.length() == 3)
			s = "0000" + s;
		else if (s.length() == 4)
			s = "000" + s;
		else if (s.length() == 5)
			s = "00" + s;
		else if (s.length() == 6)
			s = "0" + s;
			
		return s;	
	}
}
