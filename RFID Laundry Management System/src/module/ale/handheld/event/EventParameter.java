package module.ale.handheld.event;

import java.io.Serializable;

public class EventParameter implements Serializable {
	
	private String name;
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2873920656282758035L;

}
