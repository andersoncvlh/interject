package uk.bl.wa.interject.converter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ImageIOStrategy implements ImageStrategy {

	INSTANCE;

	protected static Logger logger = LogManager.getLogger(ImageIOStrategy.class);

	private ImageIOStrategy() {}

	@Override
	public byte[] convertFromUrlToPng(String url, String sourceContentType)
			throws Exception {
	    logger.info("ImageIOStrategy convert: "+url+" from "+sourceContentType);
		byte[] imageBytes = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			if( sourceContentType != null ) {
				httpGet.addHeader("Accept", sourceContentType);
			}
			CloseableHttpResponse res = httpclient.execute(httpGet);
			InputStream is = res.getEntity().getContent();
			ImageInputStream iis = ImageIO.createImageInputStream(is);
			BufferedImage image = ImageIO.read(iis);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
		    baos.flush();
		    imageBytes = baos.toByteArray();
		} catch (Exception exp) {
			exp.printStackTrace();
		} finally {
			httpclient.close();
		}
		return imageBytes;
	}

}
