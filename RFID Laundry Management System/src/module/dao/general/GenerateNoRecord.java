package module.dao.general;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import module.dao.BaseBoGeneral;
import module.dao.BeansFactoryApplication;
import module.dao.DaoFactory;

@Entity
@Table(name=DaoFactory.GenerateNoRecord)
public class GenerateNoRecord extends BaseBoGeneral
{
	private static final long serialVersionUID = 5288520570111001522L;
	
	public enum GenerateNoRecordType
	{
		TYPE_IRONING_DELIVERY,
		TYPE_CLOTH_RACKING,
		TYPE_KIOSK_COLLECTION, 
		TYPE_KIOSK_DISTRIBUTE
	}
	
	@Id
	@Enumerated(EnumType.STRING)
	private GenerateNoRecordType id;

	@Column(name="last_generate_date")
	private Date lastGenerateDate;
	
	@Column(name="last_generate_no")
	private Integer lastGenerateNo;


	public GenerateNoRecordType getId()
	{
		return id;
	}
	public void setId(GenerateNoRecordType id)
	{
		this.id = id;
	}
	public Date getLastGenerateDate()
	{
		return lastGenerateDate;
	}
	public void setLastGenerateDate(Date lastGenerateDate)
	{
		this.lastGenerateDate = lastGenerateDate;
	}
	public Integer getLastGenerateNo()
	{
		return lastGenerateNo;
	}
	public void setLastGenerateNo(Integer lastGenerateNo)
	{
		this.lastGenerateNo = lastGenerateNo;
	}


	
	public static String getGenerateNoRecordPrefix(GenerateNoRecordType type)
	{
		switch (type)
		{
			case TYPE_IRONING_DELIVERY:
				return BeansFactoryApplication.getInstance().getIroningDeliveryPrefix();
			case TYPE_CLOTH_RACKING:
				return BeansFactoryApplication.getInstance().getClothRackingPrefix();
			case TYPE_KIOSK_COLLECTION:
				return BeansFactoryApplication.getInstance().getKioskCollectionPrefix();
			case TYPE_KIOSK_DISTRIBUTE:
				return BeansFactoryApplication.getInstance().getKioskDistributePrefix();
			default:
				throw new RuntimeException("getGenerateNoRecordPrefix(): Type Not Found");
		}
	}
	
	public static String getGenerateNoRecordPattern(GenerateNoRecordType type)
	{
		switch (type)
		{
			case TYPE_IRONING_DELIVERY:
				return BeansFactoryApplication.getInstance().getIroningDeliveryPattern();
			case TYPE_CLOTH_RACKING:
				return BeansFactoryApplication.getInstance().getClothRackingPattern();
			case TYPE_KIOSK_COLLECTION:
				return BeansFactoryApplication.getInstance().getKioskCollectionPattern();
			case TYPE_KIOSK_DISTRIBUTE:
				return BeansFactoryApplication.getInstance().getKioskDistributePattern();
			default:
				throw new RuntimeException("getGenerateNoRecordPattern(): Type Not Found");
		}
	}
}