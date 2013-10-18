package uk.bl.wa.interject.servlet.integration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
		//this.checkUrlContentType("http://localhost:8989/images/cc.bmp", "image/png");
	}

	/**
	 * 
	 * @param url
	 * @param expectedType
	 * @throws IOException
	 */
	private void checkUrlContentType( String url, String expectedType ) throws IOException {
		URL test = new URL(url);
		URLConnection con = test.openConnection();
		assertEquals( expectedType, con.getContentType() );
	}

}
