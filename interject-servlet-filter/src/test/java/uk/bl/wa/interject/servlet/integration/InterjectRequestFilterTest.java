package uk.bl.wa.interject.servlet.integration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;

public class InterjectRequestFilterTest {

	@Test
	public void testInterject() throws IOException {
		URL test = new URL("http://localhost:8989/images/cc.bmp");
		URLConnection con = test.openConnection();
		System.out.println("GOT: "+con.getContentType());
		fail("Not yet implemented");
	}

}
