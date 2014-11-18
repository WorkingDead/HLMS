package utils.xml;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class JaxwsFactory {

	public static <T> T getWebServiceClient(Class<T> clientClass,
			String wsdlUrl, String targetNamespace, String portName, int connectTimeout, int readTimeout ) throws Exception
	{
		URL wsdlURL;
		try {
			wsdlURL = new URL(wsdlUrl);

			HttpURLConnection clone_urlconnection = (HttpURLConnection) wsdlURL.openConnection();

			// TimeOut settings
            clone_urlconnection.setConnectTimeout(connectTimeout);
            clone_urlconnection.setReadTimeout(readTimeout);

            //try connect here to avoid jaxws long timeout
            clone_urlconnection.connect();
		}
		catch (Exception e) {
			throw e;
		}

		QName SERVICE_NAME = new QName(targetNamespace, portName);
    	Service service = Service.create(wsdlURL, SERVICE_NAME);
    	return service.getPort(clientClass);
	}

}
