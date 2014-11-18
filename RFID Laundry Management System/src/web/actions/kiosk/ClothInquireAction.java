package web.actions.kiosk;

import java.util.List;

import module.dao.master.Cloth;
import module.dao.master.Cloth.ClothStatus;
import module.dao.master.Zone;

import org.apache.log4j.Logger;

import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.criterion.Order;

import web.actions.BaseActionKiosk;

@Results({
})
@InterceptorRefs({
	@InterceptorRef("prefixStack"),
	@InterceptorRef(value="validation",params={"includeMethods", ""}),
	@InterceptorRef("postStack")
})
@ParentPackage("struts-action-default")
public class ClothInquireAction extends BaseActionKiosk
{
	private static final long serialVersionUID = -7133286768088836153L;
	private static final Logger log4j = Logger.getLogger(ClothInquireAction.class);

	private List<ClothStatus> clothStatusList;
	private List<Zone> zoneList;

	public String getMainPage()
	{
		this.clothStatusList = Cloth.getClothStatusListWithoutLost();
		this.zoneList = this.getMasterService().findAll(Zone.class, null, null, null, Order.asc("code"));
		this.setTilesKey("cloth-inquire.main");
		return TILES;
	}

	public List<ClothStatus> getClothStatusList()
	{
		return clothStatusList;
	}
	public void setClothStatusList(List<ClothStatus> clothStatusList)
	{
		this.clothStatusList = clothStatusList;
	}
	public List<Zone> getZoneList()
	{
		return zoneList;
	}
	public void setZoneList(List<Zone> zoneList)
	{
		this.zoneList = zoneList;
	}
	
}