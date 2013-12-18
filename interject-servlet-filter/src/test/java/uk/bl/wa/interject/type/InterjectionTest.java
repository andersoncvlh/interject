package uk.bl.wa.interject.type;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.schlichtherle.io.File;
import uk.bl.wa.interject.factory.InterjectionFactory;

public class InterjectionTest {

	protected static Logger logger = LogManager
			.getLogger(InterjectionTest.class);
	private Tika tika = null;
	private String spectrumResult;

	@Before
	public void setUp() throws Exception {
		tika = new Tika();
		Map<String, String> params = new HashMap<String, String>();
		params.put("version", "basic");
		MediaType spectrumType = new MediaType("application", "x-spectrum-tap", params);
		spectrumResult = spectrumType.toString(); 
	}

	@Test
	public void testTika() {
		String strMime = tika.detect("test.bmp");
		Assert.assertEquals("image/x-ms-bmp", strMime);
		Assert.assertNotSame("Mime doesn't match", "image/x-ms-png", strMime);
	}

	@Test
	public void testContentTypeBmp() {
		try {
			String filename = "/test.bmp";
			InputStream inputStream = getClass().getResourceAsStream(filename);
			Tika tika = new Tika();
			String mimeType = tika.detect(inputStream);
			String expected = MediaType.image("x-ms-bmp").toString();
			Assert.assertEquals(expected, mimeType);
			System.out.println(String.format(
					"detected media type for given file %s: %s", filename,
					mimeType));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testContentTypeTiff() {
		try {
			String filename = "/test.tiff";
			InputStream inputStream = getClass().getResourceAsStream(filename);
			String mimeType = tika.detect(inputStream);
			String expected = MediaType.image("tiff").toString();
			Assert.assertEquals(expected, mimeType);
			System.out.println(String.format(
					"detected media type for given file %s: %s", filename,
					mimeType));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testEnumerationType() throws IOException {
		String filename = "/test.tiff";
		InputStream inputStream = getClass().getResourceAsStream(filename);
		String sourceType = tika.detect(inputStream);
		String problemType = InterjectionFactory.INSTANCE.findProblemType(
				sourceType).getMimeType();
		String expected = MediaType.image("tiff").toString();
		String mimeType = problemType;
		Assert.assertEquals(expected, mimeType);
		System.out.println(String
				.format("detected media type for given file %s: %s", filename,
						mimeType));
	}

	@Test
	public void testSpectrumFile() {
		try {
			Tika tika = new Tika();
			String f = getClass().getResource("/Wheelie.tap").getFile();
			File file = new File(f);
			String mimeType = tika.detect(file);
			Assert.assertNotNull(mimeType);
			Assert.assertEquals(spectrumResult, mimeType);
			System.out.println("Type : " + mimeType);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testSpectrumStream() {
		try {
			Tika tika = new Tika();
			InputStream inputStream = getClass().getResourceAsStream(
					"/ZZOOM.tap");
//			byte[] bytes = IOUtils.toByteArray(inputStream);
//			System.out.println("Result via InputStream: "
//					+ new String(bytes, "UTF8"));
			String mimeType = tika.detect(inputStream, new Metadata());
			Assert.assertNotNull(mimeType);
			Assert.assertEquals(spectrumResult, mimeType);
			System.out.println("Type via InputStream: " + mimeType);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
