package web.actions.handheld.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import module.dao.master.RollContainer;

public class ContainerInfo implements Serializable {
	
	public static List<ContainerInfo> parseRollContainer(List<RollContainer> rs) {
		List<ContainerInfo> containerInfos = new ArrayList<ContainerInfo>();
		for(RollContainer r : rs) {
			containerInfos.add(parseRollContainer(r));
		}
		return containerInfos;
	}
	
	public static ContainerInfo parseRollContainer(RollContainer r) {
		return new ContainerInfo(r.getId(), r.getCode());
	}
	
	public ContainerInfo() {
	}

	public ContainerInfo(long id, String code) {
		this.id = id;
		this.code = code;
	}

	private long id;
	private String code;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3572415853101317952L;

}
