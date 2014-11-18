package web.actions.handheld.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import module.dao.BeansFactoryApplication;

import utils.spring.SpringUtils;
/**
 * Currently, the cloth size is hardcoded due to cloth size has not recorded in database.
 * If the size need to dynamic change, modify this class to query from database.
 * @author cchin_kan
 *
 */
public class ClothSizeInfo implements Serializable {
	
	public static List<ClothSizeInfo> getClothSizeInfo() {
		List<ClothSizeInfo> clothSizes = new ArrayList<ClothSizeInfo>();
		
		String clothSizeString = ((BeansFactoryApplication)SpringUtils.getBean(BeansFactoryApplication.BEANNAME)).getAllClothSize();
		
		String[] cs = clothSizeString.split(",");
		for(String size : cs) {
			clothSizes.add(new ClothSizeInfo(size, size));
		}
		
		return clothSizes;
	}
	
	public ClothSizeInfo(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	private String id;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5582478593778665626L;

}
