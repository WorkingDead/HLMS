package web.actions.handheld;

import java.io.BufferedReader;

import java.lang.reflect.Type;

import java.util.Iterator;
import java.util.List;

import module.ale.handheld.HandheldHandlerFactory;
import module.ale.handheld.event.HandheldEvent;

import module.dao.iface.CustomCriteriaHandler;
import module.dao.iface.CustomLazyHandler;
import module.dao.master.Cloth;
import module.dao.master.ClothType;
import module.dao.master.RollContainer;

import org.apache.log4j.Logger;

import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import utils.spring.SpringUtils;

import web.actions.BaseActionHandheld;
import web.actions.handheld.bo.ClothInfo;
import web.actions.handheld.bo.ClothSizeInfo;
import web.actions.handheld.bo.ClothTypeInfo;
import web.actions.handheld.bo.ContainerInfo;

@Results({
	@Result(name="ClothInfo", type="json", params={
			"root" , 	"clothInfos"}),
	@Result(name="ContainerInfo", type="json", params={
			"root" , 	"containerInfos"}),
	@Result(name="ClothTypeInfo", type="json", params={
			"root" , 	"clothTypeInfos"}),
	@Result(name="ClothSizeInfo", type="json", params={
			"root" , 	"clothSizeInfos"}),
	@Result(name="Sync", type="json", params={
			"root" , 	"success"})
})
@ParentPackage("struts-action-default")
public class SyncDataAction extends BaseActionHandheld {

	private static final Logger log4j = Logger.getLogger(SyncDataAction.class);
	
	// Handheld download from App (Server)
	public String clothInfo() {

		this.getMasterService().findByExample(Cloth.class, null, null, null, new CustomCriteriaHandler<Cloth>() {

			@Override
			public void makeCustomCriteria(Criteria arg0) {
				arg0.createCriteria("staff").add(Restrictions.eq("code", staffCode));
			}
		}, new CustomLazyHandler<Cloth>() {

			@Override
			public void LazyList(List<Cloth> list) {
				clothInfos = ClothInfo.parseClothInfo(list, SyncDataAction.this);
			}

		}, null);

		return "ClothInfo";
	}

	public String containerInfo() {

		this.getMasterService().findByExample(RollContainer.class, null, null, null, null, new CustomLazyHandler<RollContainer>() {

			@Override
			public void LazyList(List<RollContainer> list) {
				containerInfos = ContainerInfo.parseRollContainer(list);
			}

		}, null);

		return "ContainerInfo";
	}

	public String clothTypeInfo() {

		this.getMasterService().findByExample(ClothType.class, null, null, null, null, new CustomLazyHandler<ClothType>() {

			@Override
			public void LazyList(List<ClothType> list) {
				clothTypeInfos = ClothTypeInfo.parseClothType(list);
			}

		}, null);

		return "ClothTypeInfo";
	}

	public String clothSizeInfo() {
		clothSizeInfos = ClothSizeInfo.getClothSizeInfo();
		return "ClothSizeInfo";
	}

	// Handheld send to App
	public String sync() {

		try {
			BufferedReader reader = this.getServletRequest().getReader();
			String read = "";
			String line = "";
			while((line = reader.readLine()) != null)
			{
				read += line;
			}

			log4j.info("read: " + read);
			
			Gson gson = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss").create();
			Type listOfTestObject = new TypeToken<List<HandheldEvent>>(){}.getType();
			List<HandheldEvent> handheldEvents = gson.fromJson(read, listOfTestObject);
			
			for (Iterator<HandheldEvent> it = handheldEvents.iterator();it.hasNext();) {
				HandheldEvent item = it.next();
				log4j.info("kiosk " + item.getKioskName());
				log4j.info("getEventName " + item.getEventName());
				log4j.info("gettime " + item.getEventTime());
				log4j.info("item count  " + item.getItemLists().size());
				HandheldHandlerFactory factory = SpringUtils.getBean(HandheldHandlerFactory.BEANNAME);
				factory.addWaitingList(item);
			}
			
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}

		return "Sync";
	}

	//parameters
	private String staffCode;

	//json return
	private List<ClothInfo> clothInfos;
	private List<ClothTypeInfo> clothTypeInfos;
	private List<ClothSizeInfo> clothSizeInfos;
	private List<ContainerInfo> containerInfos;
	private boolean success = false;
	
	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public List<ClothInfo> getClothInfos() {
		return clothInfos;
	}

	public void setClothInfos(List<ClothInfo> clothInfos) {
		this.clothInfos = clothInfos;
	}

	public List<ClothTypeInfo> getClothTypeInfos() {
		return clothTypeInfos;
	}

	public void setClothTypeInfos(List<ClothTypeInfo> clothTypeInfos) {
		this.clothTypeInfos = clothTypeInfos;
	}

	public List<ClothSizeInfo> getClothSizeInfos() {
		return clothSizeInfos;
	}

	public void setClothSizeInfos(List<ClothSizeInfo> clothSizeInfos) {
		this.clothSizeInfos = clothSizeInfos;
	}

	public List<ContainerInfo> getContainerInfos() {
		return containerInfos;
	}

	public void setContainerInfos(List<ContainerInfo> containerInfos) {
		this.containerInfos = containerInfos;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2280380007539487458L;
}
