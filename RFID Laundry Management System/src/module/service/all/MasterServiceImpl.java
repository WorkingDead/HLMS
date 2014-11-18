package module.service.all;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import web.actions.BaseActionKiosk;

import z_test_junit.CustomRollBackException;

import module.dao.DaoFactory;

import module.dao.general.SpecialEvent;
import module.dao.general.SpecialEvent.SpecialEventName;
import module.dao.general.SpecialEvent.SpecialEventStatus;
import module.dao.general.SpecialEventDao;

import module.dao.master.Cloth;
import module.dao.master.ClothDao;
import module.dao.master.ClothType;
import module.dao.master.ClothTypeDao;
import module.dao.master.RollContainerDao;
import module.dao.master.Staff;
import module.dao.master.StaffDao;
import module.dao.master.ZoneDao;
import module.dao.master.DepartmentDao;
import module.dao.master.Staff.StaffStatus;

import module.dao.system.Users;
import module.dao.system.UsersDao;

import module.service.ServiceFactory;
import module.service.impl.BaseCRUDDaosServiceImpl;

@Service(ServiceFactory.MasterService)
public class MasterServiceImpl extends BaseCRUDDaosServiceImpl implements MasterService
{
	@Resource(name=DaoFactory.CLOTH_DAO)
	public ClothDao clothDao;
	
	@Resource(name=DaoFactory.CLOTH_TYPE_DAO)
	public ClothTypeDao clothTypeDao;
	
	@Resource(name=DaoFactory.ZONE_DAO)
	public ZoneDao zoneDao;
	
	@Resource(name=DaoFactory.STAFF_DAO)
	public StaffDao staffDao;
	
	@Resource(name=DaoFactory.DEPARTMENT_DAO)
	public DepartmentDao DepartmentDao;
	
	@Resource(name=DaoFactory.ROLL_CONTAINER_DAO)
	public RollContainerDao rollContainerDao;
	
	@Resource(name=DaoFactory.SPECIAL_EVENT_DAO)
	public SpecialEventDao specialEventDao;
	
	@Resource(name=ServiceFactory.OthersService)
	public OthersService othersService;

	@Resource(name=DaoFactory.USERS_DAO)
	public UsersDao UsersDao;
	


	@Override		//Also Handling Cloth Lost
	public void updateAndSaveClothLost(Cloth cloth, Staff staff, ClothType clothType) {
		
		//Saving Cloth
//		this.clothDao.save(cloth);
		
		//Handling Special Event
		SpecialEvent se = new SpecialEvent();
		se.setSpecialEventName(SpecialEventName.ClothLost);
		se.setSpecialEventStatus(SpecialEventStatus.Followup);
		se.setStaff(staff);
		
		se.setClothType(clothType);
		
		if ( cloth != null )
			se.setCloth(cloth);
		
		se.setCreatedBy( (Users)UsersDao.loadUserByUsername( BaseActionKiosk.KioskUserName ) );

		specialEventDao.save(se);
	}
	
	@Override		//Also Handling Cloth Found
	public void updateAndSaveClothFound(Cloth cloth, ClothType clothType, Staff staff)
	{
		//Saving Cloth
//		this.clothDao.save(cloth);
		
		//Handling Special Event
		SpecialEvent se = new SpecialEvent();
		se.setSpecialEventName(SpecialEventName.ClothFound);
		se.setSpecialEventStatus(SpecialEventStatus.Finished);
		se.setStaff(staff);
		se.setCloth(cloth);
		se.setClothType(clothType);
		
		se.setCreatedBy( (Users)UsersDao.loadUserByUsername( BaseActionKiosk.KioskUserName ) );
		
		specialEventDao.save(se);
	}
	

	@Override	//Here is selection image save method
	public void saveClothType(ClothType clothType, Collection<Long> addAttachmentList, Collection<Long> delAttachmentList)
	{
		saveClothTypeAttachment(clothType, addAttachmentList, delAttachmentList);
		
		clothTypeDao.save(clothType);
	}

	@Override	//Here is selection image save method
	public void saveClothTypeAttachment(ClothType clothType, Collection<Long> addAttachmentList, Collection<Long> delAttachmentList)
	{
		othersService.saveAttachment(clothType, addAttachmentList, delAttachmentList);
	}

	
	@Override
	public void updateAndSaveStaff(Staff staff)
	{
		//////////////////////////////////////////////
		// 1. Save the receipt
		//////////////////////////////////////////////
		this.staffDao.save(staff);
		
		//////////////////////////////////////////////
		// 2. Create a special event if a staff left
		//////////////////////////////////////////////
		if (staff.getStaffStatus().equals(StaffStatus.Leave))
		{
			SpecialEvent se = new SpecialEvent();
			se.setSpecialEventName(SpecialEventName.StaffResigned);
			se.setSpecialEventStatus(SpecialEventStatus.Finished);
			se.setStaff(staff);
			se.setCloth(null);
			
			this.specialEventDao.save(se);
		}
	}
	


	//For testing service roll back only
	//Normal Saving
	public void serviceSaveListRollbackTestForStaff01(List<Staff> list) {
		
		for ( int i = 0; i < list.size(); i++ ) {

			Staff staff = list.get(i);

			this.save(Staff.class, staff);			//Different Point Between 01 and 02
		}
	}
	
	//Normal Saving
	public void serviceSaveListRollbackTestForStaff02(List<Staff> list) {
		
		for ( int i = 0; i < list.size(); i++ ) {

			Staff staff = list.get(i);

			staffDao.save(staff);					//Different Point Between 01 and 02
		}
	}
	
	public void serviceSaveListRollbackTestForStaff03(List<Staff> list) throws Exception {
		
		for ( int i = 0; i < list.size(); i++ ) {
			
			if ( i == 1 )
				throw new CustomRollBackException("Here is serviceSaveListRollbackTestForStaff03 roll back");
			
			Staff staff = list.get(i);

			this.save(Staff.class, staff);			//Different Point Between 03 and 04
		}
	}
	
	public void serviceSaveListRollbackTestForStaff04(List<Staff> list) throws Exception {
		
		for ( int i = 0; i < list.size(); i++ ) {
			
			if ( i == 1 )
				throw new CustomRollBackException("Here is serviceSaveListRollbackTestForStaff04 roll back");
			
			Staff staff = list.get(i);

			staffDao.save(staff);					//Different Point Between 03 and 04
		}
	}
	//For testing service roll back only
}
