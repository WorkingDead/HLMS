package web.actions.handheld.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.TextProvider;

import module.dao.master.Cloth;

public class ClothInfo implements Serializable {
	
	public static List<ClothInfo> parseClothInfo(List<Cloth> cloths, TextProvider provider) {
		List<ClothInfo> infos = new ArrayList<ClothInfo>();
		for (Cloth c : cloths) {
			infos.add(ClothInfo.parseClothInfo(c, provider));
		}
		return infos;
	}
	
	public static ClothInfo parseClothInfo(Cloth cloth, TextProvider provider) {
		ClothInfo info = new ClothInfo();
		
		info.id = cloth.getId();
		info.code = cloth.getCode();
		info.rfid = cloth.getRfid();
		info.type = cloth.getClothType().getName(); //no need to check, nullable = false (kan)
		info.status = provider.getText(cloth.getClothStatus().toString()); //TODO: get resource bundle
		info.staffCode = cloth.getStaff()==null?"":cloth.getStaff().getCode();
		info.staffName = cloth.getStaff()==null?"":cloth.getStaff().getNameCht();
		
		return info;
	}
	
	private Long id;
	
	private String code;
	private String rfid;
	private String type;
	
	private String status;
	
	private String staffCode;
	private String staffName;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStaffCode() {
		return staffCode;
	}

	public void setStaffCode(String staffCode) {
		this.staffCode = staffCode;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7169268269502338601L;
}
