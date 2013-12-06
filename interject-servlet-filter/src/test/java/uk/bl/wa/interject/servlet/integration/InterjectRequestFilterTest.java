package uk.bl.wa.interject.servlet.integration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tika.io.IOUtils;
import org.junit.Test;

/**
 * 
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class InterjectRequestFilterTest {
	
	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testInterject() throws IOException {
		// Check that URLs like this have NOT been modified:
		this.checkUrlContentType("http://localhost:8989/images/cc.png", "image/png");
		// Check URLs have been interjected correctly:
		this.checkUrlContentType("http://localhost:8989/images/cc.bmp", "image/bmp");
	}

	/**
	 * 
	 * @param url
	 * @param expectedType
	 * @throws IOException
	 */
	private void checkUrlContentType( String url, String expectedType ) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse res = httpclient.execute(httpGet);
		// Check the Content Type:
		assertEquals( expectedType, res.getFirstHeader("Content-Type").getValue() );
		// Download the converted content (to avoid a broken-pipe error in the server:
		byte[] out = IOUtils.toByteArray(res.getEntity().getContent());		
		// Clean up:
		res.close();
		httpclient.close();
	}

}
