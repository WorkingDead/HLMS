package module.service.all;

import java.util.Collection;
import java.util.List;

import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.Staff;
import module.service.iface.BaseCRUDDaosService;

public interface MasterService extends BaseCRUDDaosService
{
	//Also Handling Cloth Lost
	public abstract void updateAndSaveClothLost(Cloth cloth, Staff staff, ClothType clothType);
	//Also Handling Cloth Found
	public abstract void updateAndSaveClothFound(Cloth cloth, ClothType clothType, Staff staff);
	
	//Here is selection image save method
	public abstract void saveClothType(ClothType clothType, Collection<Long> addAttachmentList, Collection<Long> delAttachmentList);

	//Here is selection image save method
	//This method for saving the attachment list map with the ClothType
	public abstract void saveClothTypeAttachment(ClothType clothType, Collection<Long> addAttachmentList, Collection<Long> delAttachmentList);

	public abstract void updateAndSaveStaff(Staff staff);

	//For testing service roll back only
	public abstract void serviceSaveListRollbackTestForStaff01(List<Staff> list);		//Normal Saving

	public abstract void serviceSaveListRollbackTestForStaff02(List<Staff> list);		//Normal Saving
	
	public abstract void serviceSaveListRollbackTestForStaff03(List<Staff> list) throws Exception;
	
	public abstract void serviceSaveListRollbackTestForStaff04(List<Staff> list) throws Exception;
	//For testing service roll back only
}
