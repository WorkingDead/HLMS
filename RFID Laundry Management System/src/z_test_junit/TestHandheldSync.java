package z_test_junit;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestHandheldSync {

	private String test = "[{\"KioskName\":\"kiosk\",\"EventName\":\"collect\",\"EventTime\":\"2007-01-14T06:40:19\",\"Parameters\":[{\"Name\":\"staff_code\",\"Value\":\"SF001\"}],\"ItemLists\":[{\"Hex\":\"303404E9FC6B6C4000000017\",\"Epc\":\"303404E9FC6B6C4000000017\"},{\"Hex\":\"305404E9FC6B6C8000000002\",\"Epc\":\"305404E9FC6B6C8000000002\"},{\"Hex\":\"201203210000000000000001\",\"Epc\":\"201203210000000000000001\"},{\"Hex\":\"305404E9FC6B6C8000000001\",\"Epc\":\"305404E9FC6B6C8000000001\"},{\"Hex\":\"201203210000000000000019\",\"Epc\":\"201203210000000000000019\"}]},{\"KioskName\":\"kiosk\",\"EventName\":\"ironing\",\"EventTime\":\"2007-01-14T06:40:52\",\"Parameters\":[{\"Name\":\"container_code\",\"Value\":\"2\"}],\"ItemLists\":[{\"Hex\":\"201203210000000000000001\",\"Epc\":\"201203210000000000000001\"},{\"Hex\":\"305404E9FC6B6C8000000001\",\"Epc\":\"305404E9FC6B6C8000000001\"},{\"Hex\":\"305404E9FC6B6C8000000002\",\"Epc\":\"305404E9FC6B6C8000000002\"},{\"Hex\":\"303404E9FC6B6C4000000017\",\"Epc\":\"303404E9FC6B6C4000000017\"},{\"Hex\":\"201203210000000000000019\",\"Epc\":\"201203210000000000000019\"}]},{\"KioskName\":\"kiosk\",\"EventName\":\"shelves\",\"EventTime\":\"2007-01-14T06:41:08\",\"Parameters\":[{\"Name\":\"location_code\",\"Value\":\"ssss\"}],\"ItemLists\":[{\"Hex\":\"305404E9FC6B6C8000000002\",\"Epc\":\"2.0080511.110002.2\"},{\"Hex\":\"305404E9FC6B6C8000000001\",\"Epc\":\"2.0080511.110002.1\"},{\"Hex\":\"303404E9FC6B6C4000000017\",\"Epc\":\"1.0080511.110001.23\"}]},{\"KioskName\":\"kiosk\",\"EventName\":\"take_out\",\"EventTime\":\"2007-01-14T06:41:28\",\"Parameters\":[{\"Name\":\"take_staff_code\",\"Value\":\"SF001\"},{\"Name\":\"process_staff_code\",\"Value\":\"SF001\"}],\"ItemLists\":[{\"Hex\":\"305404E9FC6B6C8000000001\",\"Epc\":\"305404E9FC6B6C8000000001\"},{\"Hex\":\"305404E9FC6B6C8000000002\",\"Epc\":\"305404E9FC6B6C8000000002\"},{\"Hex\":\"201203210000000000000019\",\"Epc\":\"201203210000000000000019\"},{\"Hex\":\"303404E9FC6B6C4000000017\",\"Epc\":\"303404E9FC6B6C4000000017\"},{\"Hex\":\"201203210000000000000001\",\"Epc\":\"201203210000000000000001\"}]},{\"KioskName\":\"kiosk\",\"EventName\":\"association\",\"EventTime\":\"2007-01-14T06:41:48\",\"Parameters\":[{\"Name\":\"staff_code\",\"Value\":\"tgg\"},{\"Name\":\"cloth_type\",\"Value\":\"1\"},{\"Name\":\"cloth_size\",\"Value\":\"1\"},{\"Name\":\"cloth_code\",\"Value\":\"gg\"}],\"ItemLists\":[{\"Hex\":\"305404E9FC6B6C8000000001\",\"Epc\":\"305404E9FC6B6C8000000001\"},{\"Hex\":\"305404E9FC6B6C8000000002\",\"Epc\":\"305404E9FC6B6C8000000002\"},{\"Hex\":\"303404E9FC6B6C4000000017\",\"Epc\":\"303404E9FC6B6C4000000017\"},{\"Hex\":\"201203210000000000000019\",\"Epc\":\"201203210000000000000019\"},{\"Hex\":\"201203210000000000000001\",\"Epc\":\"201203210000000000000001\"}]}]";
	
	@Test
	public void test() {
		
		HttpClient httpClient = new DefaultHttpClient();

	    try {
	        HttpPost request = new HttpPost("http://localhost:8080/hlms/handheld/sync-data!sync.action");
	        StringEntity params =new StringEntity(test, "utf-8");
	        request.addHeader("content-type", "application/json");
	        request.setEntity(params);
	        HttpResponse response = httpClient.execute(request);
	        BufferedReader redaer = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	        String test = "";
	        while((test=redaer.readLine()) != null) {
	        	System.out.println(test);
	        }
	        // handle response here...
	    }catch (Exception ex) {
	        // handle exception here
	    } finally {
	        httpClient.getConnectionManager().shutdown();
	    }
	    
	}
	
	@Test
	public void test2() {
		String json = "\"2007-01-17T01:36:06\"";
		Gson gson = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss").create();
		Date cal = gson.fromJson(json, Date.class);
		System.out.println("cal2 = " + cal.toString());
	}
	
	@Test
	public void test3() throws ParseException {
		String date = "2007-01-17T01:36:06";
		SimpleDateFormat f = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss");
		Date cal = f.parse(date);
		System.out.println("cal3 = " + cal.toString());
	}
	
	
}
