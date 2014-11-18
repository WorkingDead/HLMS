package z_test_junit;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value = Suite.class)
@SuiteClasses(value={
		//SimulateHandheldSendToClothCollectionHandler.class,
		//SimulateHandheldSendToIroningDeliveryHandler.class,
		//SimulateHandheldSendToClothRackingHandler.class,
		//SimulateHandheldSendToClothDistributeHandler.class,
		//SimulateHandheldSendToClothAssociationHandler.class,
		})
public class SimulateHandheldAll {

	
	public static void send(String content) {
		
		System.out.println("content = " + content);
		
		HttpClient httpClient = new DefaultHttpClient();

	    try {
	        HttpPost request = new HttpPost("http://localhost:8080/hlms/handheld/sync-data!sync.action");
	        StringEntity params =new StringEntity(content, "utf-8");
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
}