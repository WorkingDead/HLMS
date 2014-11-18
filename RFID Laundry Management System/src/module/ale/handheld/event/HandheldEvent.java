package module.ale.handheld.event;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class HandheldEvent implements Serializable {
	
	private String kioskName;
	private String eventName;
	private Date eventTime;
	
	private Set<EventParameter> parameters;
	private Set<EventItemList> itemLists;

	public String getKioskName() {
		return kioskName;
	}

	public void setKioskName(String kioskName) {
		this.kioskName = kioskName;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public Set<EventParameter> getParameters() {
		return parameters;
	}

	public void setParameters(Set<EventParameter> parameters) {
		this.parameters = parameters;
	}

	public Set<EventItemList> getItemLists() {
		return itemLists;
	}

	public void setItemLists(Set<EventItemList> itemLists) {
		this.itemLists = itemLists;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -627306473937733185L;
}
