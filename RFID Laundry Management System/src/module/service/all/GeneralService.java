package module.service.all;

import java.util.List;
import module.dao.general.GenerateNoRecord.GenerateNoRecordType;
import module.dao.general.Receipt;
import module.dao.general.SpecialEvent;
import module.service.iface.BaseCRUDDaosService;

public interface GeneralService extends BaseCRUDDaosService
{
	// This method cannot be called directly from GeneralService but called by another service, e.g. genIroningDeliveryReceiptCode
	public abstract String generateNo(GenerateNoRecordType type);
	
	// System
	public abstract String genIroningDeliveryReceiptCode();
	public abstract String genClothRackingReceiptCode();
	public abstract void saveReceiptAndTransaction(Receipt receipt);
	public abstract void saveIroningExpirationSpecialEvent(Receipt receipt, List<SpecialEvent> eventList);
	
	// Kiosk
	public abstract String genKioskClothCollectionReceiptCode();
	public abstract String genKioskClothDistributeReceiptCode();
	
}
