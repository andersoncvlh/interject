package uk.bl.wa.interject.converter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.formats.png.PngConstants;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.bl.wa.interject.exception.ConverterException;

public enum CommonsImageStrategy implements ImageStrategy {
	
	INSTANCE;

	protected static Logger logger = LogManager.getLogger(CommonsImageStrategy.class);

	private CommonsImageStrategy() {}

	@Override
	public byte[] convertFromUrlToPng(String url, String sourceContentType) throws ConverterException {
	    logger.info("CommonsImageStrategy convert: "+url+" from "+sourceContentType);
		byte[] imageBytes = null;
		CloseableHttpClient httpclient = HttpClients.createSystem();
		try {
			HttpGet httpGet = new HttpGet(url);
			if( sourceContentType != null ) {
				httpGet.addHeader("Accept", sourceContentType);
			}

			CloseableHttpResponse res = httpclient.execute(httpGet);
			InputStream is = res.getEntity().getContent();
	        // read image
			Map<String, Object> readParams = new HashMap<String, Object>();
			readParams.put(ImagingConstants.PARAM_KEY_FILENAME, url);
			// Note that current version assumes TIFFs are not transparent.
	        final BufferedImage image = Imaging.getBufferedImage(is,readParams);
			// Now convert and respond:		        
			Map<String, Object> writeParams = new HashMap<String, Object>();
			writeParams.put(PngConstants.PARAM_KEY_PNG_FORCE_TRUE_COLOR, Boolean.TRUE);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    Imaging.writeImage(image, baos, ImageFormats.PNG, writeParams);
		    baos.flush();
		    imageBytes = baos.toByteArray();
		} catch(Exception e) {
			throw new ConverterException(e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				throw new ConverterException(e);
			}
		}
		return imageBytes;
	}

}
