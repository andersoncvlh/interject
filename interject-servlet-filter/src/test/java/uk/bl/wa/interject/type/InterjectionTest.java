package uk.bl.wa.interject.type;

import java.io.IOException;
import java.io.InputStream;

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
import uk.bl.wa.nanite.droid.DroidDetector;
import uk.bl.wa.interject.factory.InterjectionFactory;
import uk.gov.nationalarchives.droid.command.action.CommandExecutionException;

public class InterjectionTest {

	protected static Logger logger = LogManager
			.getLogger(InterjectionTest.class);
	private Tika tika = null;

	@Before
	public void setUp() throws Exception {
		tika = new Tika();
	}

	@Test
	public void testTika() {
		Tika tika = new Tika();
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
	public void testSpectrum() {
		DroidDetector detector;
		try {
			detector = new DroidDetector();
			String f = getClass().getResource("/ZZOOM.TAP").getFile();
			File file = new File(f);
			MediaType mediaType = detector.detect(file);
			Assert.assertNotNull(mediaType);
			System.out.println("MediaType : " + mediaType);
		} catch (CommandExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
