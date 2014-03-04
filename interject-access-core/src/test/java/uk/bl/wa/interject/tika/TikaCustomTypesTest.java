/**
 * 
 */
package uk.bl.wa.interject.tika;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class TikaCustomTypesTest {

	private Tika tika;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		tika = new Tika();
	}

	@Test
	public void testCustomTypes() throws IOException {
		this.testCustomTypeMatch("src/test/resources/02fig01.wrl",
				"model/vrml; version=2.0");
		this.testCustomTypeMatch("src/test/resources/02fig01.x3d",
				"model/x3d+xml");
	}

	private void testCustomTypeMatch(String filename, String expectedType)
			throws IOException {
		String type = tika.detect(new File(filename));
		System.out.println("TYPE " + type);
		assertEquals(expectedType, type);
	}

}
