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
import module.ale.handheld.handler.ClothDistributeHandler;

import org.junit.Test;

import web.actions.BaseActionKiosk.KioskName;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class SimulateHandheldSendToClothDistributeHandler {

	@Test(timeout = 2000)
	public void Test02() {
		
		EventParameter eventParameter1 = new EventParameter();
		eventParameter1.setName( "take_staff_code" );
		eventParameter1.setValue( "a0000000" );
		
		EventParameter eventParameter2 = new EventParameter();
		eventParameter2.setName( "process_staff_code" );
		eventParameter2.setValue( "a0000001" );



		EventItemList eventItemList1 = new EventItemList();
		eventItemList1.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.1" );
		eventItemList1.setHex( "300000000000040000000001" );
		
		EventItemList eventItemList2 = new EventItemList();
		eventItemList2.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList2.setHex( "300000000000040000000002" );
		
		EventItemList eventItemList3 = new EventItemList();
		eventItemList3.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList3.setHex( "300000000000040000000003" );
		
		EventItemList eventItemList4 = new EventItemList();
		eventItemList4.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList4.setHex( "300000000000040000000004" );
		
		EventItemList eventItemList5 = new EventItemList();
		eventItemList5.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList5.setHex( "300000000000040000000005" );
		
		EventItemList eventItemList6 = new EventItemList();
		eventItemList6.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.1" );
		eventItemList6.setHex( "300000000000040000000006" );
		
		EventItemList eventItemList7 = new EventItemList();
		eventItemList7.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList7.setHex( "300000000000040000000007" );
		
		EventItemList eventItemList8 = new EventItemList();
		eventItemList8.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList8.setHex( "300000000000040000000008" );
		
		EventItemList eventItemList9 = new EventItemList();
		eventItemList9.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList9.setHex( "300000000000040000000009" );
		
		EventItemList eventItemList10 = new EventItemList();
		eventItemList10.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList10.setHex( "30000000000004000000000A" );
		
		EventItemList eventItemList11 = new EventItemList();
		eventItemList11.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.1" );
		eventItemList11.setHex( "30000000000004000000000B" );
		
		EventItemList eventItemList12 = new EventItemList();
		eventItemList12.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList12.setHex( "30000000000004000000000C" );
		
		EventItemList eventItemList13 = new EventItemList();
		eventItemList13.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList13.setHex( "30000000000004000000000D" );
		
		EventItemList eventItemList14 = new EventItemList();
		eventItemList14.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList14.setHex( "30000000000004000000000E" );
		
		EventItemList eventItemList15 = new EventItemList();
		eventItemList15.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList15.setHex( "30000000000004000000000F" );

		EventItemList eventItemList16 = new EventItemList();
		eventItemList16.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList16.setHex( "300000000000040000000010" );
		
		EventItemList eventItemList17 = new EventItemList();
		eventItemList17.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList17.setHex( "300000000000040000000011" );
		
		EventItemList eventItemList18 = new EventItemList();
		eventItemList18.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList18.setHex( "300000000000040000000012" );
		
		EventItemList eventItemList19 = new EventItemList();
		eventItemList19.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList19.setHex( "300000000000040000000013" );

		
		EventItemList eventItemList20 = new EventItemList();
		eventItemList20.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList20.setHex( "300456789012345678900001" );
		
		EventItemList eventItemList21 = new EventItemList();
		eventItemList21.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList21.setHex( "300456789012345678900002" );
		
		EventItemList eventItemList22 = new EventItemList();
		eventItemList22.setEpc( "urn:epc:tag:sgtin-96:0.000000000000.0.4" );
		eventItemList22.setHex( "300456789012345678900003" );
		

		HandheldEvent handheldEvent = new HandheldEvent();
		handheldEvent.setKioskName( KioskName.kiosk2.toString() );
		handheldEvent.setEventName( ClothDistributeHandler.EVENT_NAME );
		handheldEvent.setEventTime( Calendar.getInstance().getTime() );

		Set<EventParameter> parameters = new HashSet<EventParameter>();
		parameters.add( eventParameter1 );
		parameters.add( eventParameter2 );

		Set<EventItemList> itemLists = new HashSet<EventItemList>();
		itemLists.add( eventItemList1 );
		itemLists.add( eventItemList2 );
		itemLists.add( eventItemList3 );
		itemLists.add( eventItemList4 );
		itemLists.add( eventItemList5 );
		itemLists.add( eventItemList6 );
		itemLists.add( eventItemList7 );
		itemLists.add( eventItemList8 );
		itemLists.add( eventItemList9 );
		itemLists.add( eventItemList10 );
		itemLists.add( eventItemList11 );
		itemLists.add( eventItemList12 );
		itemLists.add( eventItemList13 );
		itemLists.add( eventItemList14 );
		itemLists.add( eventItemList15 );
		itemLists.add( eventItemList16 );
		itemLists.add( eventItemList17 );
		itemLists.add( eventItemList18 );
		itemLists.add( eventItemList19 );

		
		itemLists.add( eventItemList20 );
		itemLists.add( eventItemList21 );
		itemLists.add( eventItemList22 );
		
		handheldEvent.setParameters( parameters );
		handheldEvent.setItemLists( itemLists );

		List<HandheldEvent> list = new LinkedList<HandheldEvent>();
		list.add(handheldEvent);
		
		Type listOfTestObject = new TypeToken<List<HandheldEvent>>(){}.getType();

		Gson gson = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss").create();
		SimulateHandheldAll.send ( gson.toJson( list, listOfTestObject ) );
	}
}
