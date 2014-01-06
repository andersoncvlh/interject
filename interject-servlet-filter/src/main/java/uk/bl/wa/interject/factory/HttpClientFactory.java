/**
 * 
 */
package uk.bl.wa.interject.factory;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class HttpClientFactory {

	public static CloseableHttpClient createHttpClientOrProxy() {

	    HttpClientBuilder hcBuilder = HttpClients.custom();

	    // Set HTTP proxy, if specified in system properties
	    if( isSet(System.getProperty("http.proxyHost")) ) {
	        int port = 80;
	        if( isSet(System.getProperty("http.proxyPort")) ) {
	            port = Integer.parseInt(System.getProperty("http.proxyPort"));
	        }
	        HttpHost proxy = new HttpHost(System.getProperty("http.proxyHost"), port, "http");
	        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
	        hcBuilder.setRoutePlanner(routePlanner);
	    }

	    CloseableHttpClient httpClient = hcBuilder.build();

	    return httpClient;
	}

	private static boolean isSet(String property) {
		if( property == null ) return false;
		if( property.trim().equals("") ) return false;
		return true;
	}

}
