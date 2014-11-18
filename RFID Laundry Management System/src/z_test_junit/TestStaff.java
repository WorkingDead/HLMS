package z_test_junit;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import module.dao.master.Department;
import module.dao.master.Staff;
import module.dao.master.Staff.StaffStatus;

import module.service.ServiceFactory;
import module.service.all.MasterService;

import org.hibernate.HibernateException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:databaseContext.xml", "classpath:applicationContext.xml"})
public class TestStaff {

	@Resource(name=ServiceFactory.MasterService)
	public MasterService masterService;



//	@Ignore("Not Complete And Not Run")
	@Test(timeout = 1000)
//	@Rollback(true)
	public void createTest01() {
		
		Department department = new Department();
		department.setNameCht("Department 000A1 CHT");
		department.setNameEng("Department 000A1 ENG");
		
		masterService.save(Department.class, department);
	}

//	@Ignore("Not Complete And Not Run")
	@Test(timeout = 1000)
//	@Rollback(true)
	public void createTest02() {
		
		Staff staff = new Staff();
		staff.setCode("00000A1");
		staff.setNameEng("00000A1 Eng Name");
		staff.setNameCht("00000A1 Cht Name");
		staff.setEnable(true);
		staff.setStaffStatus( StaffStatus.Normal );

		Department department = masterService.get(Department.class, 1L);
		staff.setDept(department);
		
		masterService.save(Staff.class, staff);
		
		//Hibernate would set the id for you after saving the entity object
		System.out.println( "staff id = " + staff.getId() );
	}
	
	@Ignore("Not Complete And Not Run")
	//Don't know why the exception is not HibernateOptimisticLockingFailureException
	//@Test(timeout = 2000, expected = HibernateOptimisticLockingFailureException.class)
	@Test(timeout = 2000, expected = HibernateException.class)
	@Rollback(true)		//Don't know this statement is valid or not
	public void updateLockTest() {
		
		Staff staff_A = masterService.get(Staff.class, 1L);
		Staff staff_B = masterService.get(Staff.class, 1L);
		
		
		
		staff_A.setCode("00000A1 111");

		try {
			Thread.sleep(1000);					//Wait at least one second
		}
		catch (Exception e) {
			e.printStackTrace();
		}



		masterService.save(Staff.class, staff_A);
		masterService.save(Staff.class, staff_B);
	}

	@Ignore("Not Complete And Not Run")
	@Test(timeout = 2000)				//Test Normal Saving
	public void rollBackTest00() {
		
		List<Staff> staffList = new LinkedList<Staff>();

		try {
			Staff staff = new Staff();
			staff.setCode("Test00_01");
			staff.setNameEng("Test00_01 Eng Name");
			staff.setNameCht("Test00_01 Cht Name");
			staff.setEnable(true);
			staff.setStaffStatus( StaffStatus.Normal );

			Department department = masterService.get(Department.class, 1L);
			staff.setDept(department);
			
			staffList.add(staff);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Staff staff = new Staff();
			staff.setCode("Test00_02");
			staff.setNameEng("Test00_02 Eng Name");
			staff.setNameCht("Test00_02 Cht Name");
			staff.setEnable(true);
			staff.setStaffStatus( StaffStatus.Normal );

			Department department = masterService.get(Department.class, 1L);
			staff.setDept(department);
			
			staffList.add(staff);
		}
		catch (Exception e) {
			e.printStackTrace();
		}



		masterService.saveList(Staff.class, staffList);			//Different Point 
	}
	
	@Ignore("Not Complete And Not Run")
	@Test(timeout = 2000)				//Test Normal Saving
	public void rollBackTest01() {
		
		List<Staff> staffList = new LinkedList<Staff>();

		try {
			Staff staff = new Staff();
			staff.setCode("Test01_01");
			staff.setNameEng("Test01_01 Eng Name");
			staff.setNameCht("Test01_01 Cht Name");
			staff.setEnable(true);
			staff.setStaffStatus( StaffStatus.Normal );

			Department department = masterService.get(Department.class, 1L);
			staff.setDept(department);
			
			staffList.add(staff);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Staff staff = new Staff();
			staff.setCode("Test01_02");
			staff.setNameEng("Test01_02 Eng Name");
			staff.setNameCht("Test01_02 Cht Name");
			staff.setEnable(true);
			staff.setStaffStatus( StaffStatus.Normal );

			Department department = masterService.get(Department.class, 1L);
			staff.setDept(department);
			
			staffList.add(staff);
		}
		catch (Exception e) {
			e.printStackTrace();
		}



		masterService.serviceSaveListRollbackTestForStaff01(staffList);		//Different Point 
	}
	
	@Ignore("Not Complete And Not Run")
	@Test(timeout = 2000)				//Test Normal Saving
	public void rollBackTest02() {
		
		List<Staff> staffList = new LinkedList<Staff>();

		try {
			Staff staff = new Staff();
			staff.setCode("Test02_01");
			staff.setNameEng("Test02_01 Eng Name");
			staff.setNameCht("Test02_01 Cht Name");
			staff.setEnable(true);
			staff.setStaffStatus( StaffStatus.Normal );

			Department department = masterService.get(Department.class, 1L);
			staff.setDept(department);
			
			staffList.add(staff);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Staff staff = new Staff();
			staff.setCode("Test02_02");
			staff.setNameEng("Test02_02 Eng Name");
			staff.setNameCht("Test02_02 Cht Name");
			staff.setEnable(true);
			staff.setStaffStatus( StaffStatus.Normal );

			Department department = masterService.get(Department.class, 1L);
			staff.setDept(department);
			
			staffList.add(staff);
		}
		catch (Exception e) {
			e.printStackTrace();
		}



		masterService.serviceSaveListRollbackTestForStaff02(staffList);			//Different Point 
	}

	@Ignore("Not Complete And Not Run")
	@Test(timeout = 2000)
	public void rollBackTest03() {
		
		List<Staff> staffList = new LinkedList<Staff>();

		try {
			Staff staff = new Staff();
			staff.setCode("Test03_01");
			staff.setNameEng("Test03_01 Eng Name");
			staff.setNameCht("Test03_01 Cht Name");
			staff.setEnable(true);
			staff.setStaffStatus( StaffStatus.Normal );

			Department department = masterService.get(Department.class, 1L);
			staff.setDept(department);
			
			staffList.add(staff);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Staff staff = new Staff();
			staff.setCode("Test03_02");
			staff.setNameEng("Test03_02 Eng Name");
			staff.setNameCht("Test03_02 Cht Name");
			staff.setEnable(true);
			staff.setStaffStatus( StaffStatus.Normal );

			Department department = masterService.get(Department.class, 1L);
			staff.setDept(department);
			
			staffList.add(staff);
		}
		catch (Exception e) {
			e.printStackTrace();
		}



		try {
			masterService.serviceSaveListRollbackTestForStaff03( staffList );		//Different Point 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Ignore("Not Complete And Not Run")
	@Test(timeout = 2000)
	public void rollBackTest04() {
		
		List<Staff> staffList = new LinkedList<Staff>();

		try {
			Staff staff = new Staff();
			staff.setCode("Test04_01");
			staff.setNameEng("Test04_01 Eng Name");
			staff.setNameCht("Test04_01 Cht Name");
			staff.setEnable(true);
			staff.setStaffStatus( StaffStatus.Normal );

			Department department = masterService.get(Department.class, 1L);
			staff.setDept(department);
			
			staffList.add(staff);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Staff staff = new Staff();
			staff.setCode("Test04_02");
			staff.setNameEng("Test04_02 Eng Name");
			staff.setNameCht("Test04_02 Cht Name");
			staff.setEnable(true);
			staff.setStaffStatus( StaffStatus.Normal );

			Department department = masterService.get(Department.class, 1L);
			staff.setDept(department);
			
			staffList.add(staff);
		}
		catch (Exception e) {
			e.printStackTrace();
		}



		try {
			masterService.serviceSaveListRollbackTestForStaff04( staffList );		//Different Point 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
