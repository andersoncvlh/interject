package uk.bl.wa.interject.type;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.bl.wa.interject.factory.InterjectionFactory;
import uk.bl.wa.interject.util.ProcessRunner;
import uk.bl.wa.interject.util.ProcessRunner.ProcessRunnerException;
import uk.bl.wa.interject.util.ProcessRunnerImpl;

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
	
	@Test
	public void testInputStream() {
		InputStream inputStream = getClass().getResourceAsStream(
				"/penguin3.wrl");
		getBytesFromStream(inputStream);
	}
	
	public byte[] getBytesFromStream(InputStream inputStream) {
		byte[] bytes = null;
		try {
			bytes = IOUtils.toByteArray(inputStream);
			System.out.println("Result via InputStream: "
					+ new String(bytes, "UTF8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bytes;
	}
	
	@Test
	public void testVrmlVersion1() {
		Tika tika = new Tika();
		String filename = "/penguin1.wrl";
		InputStream inputStream = getClass().getResourceAsStream(
				filename);
		try {
			String mimeType = tika.detect(inputStream);
			System.out.println(filename + " mimeType : " + mimeType);
			Assert.assertEquals("model/vrml; version=1.0", mimeType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testVrmlVersion2() {
		Tika tika = new Tika();
		String filename = "/penguin2.wrl";
		InputStream inputStream = getClass().getResourceAsStream(
				filename);
		try {
			String mimeType = tika.detect(inputStream);
			System.out.println(filename + " mimeType : " + mimeType);
			Assert.assertEquals("model/vrml; version=97", mimeType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testProcess() {
		ProcessRunner runner = new ProcessRunnerImpl();
		List<String> commands = new ArrayList<String>();
		try {
	    	String path = "src/test/resources";
	    	commands.add("/usr/local/bin/wine");
			commands.add("external/ivvrml.exe");
			commands.add("-2");
			commands.add(path + "/penguin1.wrl");
			commands.add("-o");
			commands.add(path + "/penguin4.wrl");
			runner.setStartingDir(new File("."));
			runner.setCommand(commands);
			runner.setCollection(true);
			runner.execute();
			System.out.println("output : " + runner.getProcessOutputAsString());
			System.out.println("stderr : " + runner.getProcessErrorAsString());
			System.out.println("Working Directory = " +
		              System.getProperty("user.dir"));
		} catch (ProcessRunnerException e) {
			System.err.println("ERROR: "+e);
			e.printStackTrace();
			System.err.println("Commands: "+commands);
		}
	}
}
