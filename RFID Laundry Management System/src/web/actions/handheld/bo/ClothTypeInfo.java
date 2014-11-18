package web.actions.handheld.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import module.dao.master.ClothType;

public class ClothTypeInfo implements Serializable {
	
	public static List<ClothTypeInfo> parseClothType(List<ClothType> clothTypes) {
		List<ClothTypeInfo> list = new ArrayList<ClothTypeInfo>();
		for(ClothType type : clothTypes) {
			list.add(parseClothType(type));
		}
		return list;
	}
	
	public static ClothTypeInfo parseClothType(ClothType clothType) {
		ClothTypeInfo info = new ClothTypeInfo();
		
		info.id = clothType.getId();
		info.name = clothType.getName();
		
		return info;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private long id;
	private String name;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6015749364494663392L;

}
