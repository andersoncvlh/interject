package uk.bl.wa.interject.converter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.bl.wa.interject.exception.ConverterException;

public enum ImageIOStrategy implements ImageStrategy {

	INSTANCE;

	protected static Logger logger = LogManager.getLogger(ImageIOStrategy.class);

	private ImageIOStrategy() {}

	@Override
	public byte[] convertFromUrlToPng(String url, String sourceContentType)
			throws ConverterException {
	    logger.info("ImageIOStrategy convert: "+url+" from "+sourceContentType);
		byte[] imageBytes = null;
		CloseableHttpClient httpclient = HttpClients.createSystem();
		try {
			HttpGet httpGet = new HttpGet(url);
			if( sourceContentType != null ) {
				httpGet.addHeader("Accept", sourceContentType);
			}
			// Log headers:
			logger.info("Getting '"+url+"'");
			for( Header h : httpGet.getAllHeaders() ) {
				logger.info("Request header '"+h.getName()+": "+h.getValue()+"'");
			} 
			// Grab the original:
			CloseableHttpResponse res = httpclient.execute(httpGet);
			InputStream is = res.getEntity().getContent();
			logger.info("Got status line: "+res.getStatusLine());
			logger.info("Got content length: "+res.getEntity().getContentLength());
			logger.info("Got content type: "+res.getEntity().getContentType());
			ImageInputStream iis = ImageIO.createImageInputStream(is);
			BufferedImage image = ImageIO.read(iis);
			// Prepare the output:
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
		    baos.flush();
		    imageBytes = baos.toByteArray();
		} catch(Exception e) {
			throw new ConverterException(e);
		}
		finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				throw new ConverterException(e);
			}
		}
		return imageBytes;
	}

}
