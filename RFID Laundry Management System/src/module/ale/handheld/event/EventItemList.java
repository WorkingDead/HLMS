package module.ale.handheld.event;

import java.io.Serializable;

public class EventItemList implements Serializable {
	
	private String hex;
	private String epc;//this is optional, currently no need to use

	public String getHex() {
		return hex;
	}

	public void setHex(String hex) {
		this.hex = hex;
	}

	public String getEpc() {
		return epc;
	}

	public void setEpc(String epc) {
		this.epc = epc;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1890529662161150183L;

}
