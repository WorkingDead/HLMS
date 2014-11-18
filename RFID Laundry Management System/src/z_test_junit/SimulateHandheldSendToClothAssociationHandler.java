package z_test_junit;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import module.ale.handheld.event.EventItemList;
import module.ale.handheld.event.EventParameter;
import module.ale.handheld.event.HandheldEvent;
import module.ale.handheld.handler.ClothAssociationHandler;

import org.junit.Test;

import web.actions.BaseActionKiosk.KioskName;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class SimulateHandheldSendToClothAssociationHandler {

	
	
	@Test(timeout = 2000)
	public void Test01() {
		
		EventParameter eventParameter1 = new EventParameter();
		eventParameter1.setName( "staff_code" );
		eventParameter1.setValue( "0005665" );

		EventParameter eventParameter2 = new EventParameter();
		eventParameter2.setName( "cloth_type" );
		eventParameter2.setValue( "1" );

		EventParameter eventParameter3 = new EventParameter();
		eventParameter3.setName( "cloth_size" );
		eventParameter3.setValue( "M" );

		EventParameter eventParameter4 = new EventParameter();
		eventParameter4.setName( "cloth_code" );
		eventParameter4.setValue( "1" );



		EventItemList eventItemList1 = new EventItemList();
		eventItemList1.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.256" );
		eventItemList1.setHex( "30000000000000000000101" );



		HandheldEvent handheldEvent = new HandheldEvent();
		handheldEvent.setKioskName( KioskName.kiosk1.toString() );
		handheldEvent.setEventName( ClothAssociationHandler.EVENT_NAME );
		handheldEvent.setEventTime( Calendar.getInstance().getTime() );
		
		Set<EventParameter> parameters = new HashSet<EventParameter>();
		parameters.add( eventParameter1 );
		parameters.add( eventParameter2 );
		parameters.add( eventParameter3 );
		parameters.add( eventParameter4 );

		Set<EventItemList> itemLists = new HashSet<EventItemList>();
		itemLists.add( eventItemList1 );

		handheldEvent.setParameters( parameters );
		handheldEvent.setItemLists( itemLists );
		
		List<HandheldEvent> list = new LinkedList<HandheldEvent>();
		list.add(handheldEvent);
		
		Type listOfTestObject = new TypeToken<List<HandheldEvent>>(){}.getType();

		Gson gson = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss").create();
		SimulateHandheldAll.send ( gson.toJson( list, listOfTestObject ) );
	}
	
}
