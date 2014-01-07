package uk.bl.wa.interject.converter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.bl.wa.interject.exception.ConverterException;
import uk.bl.wa.interject.factory.HttpClientFactory;

public enum ImageIOStrategy implements ImageStrategy {

	/*
	private HttpClient getHttpClient() {
    	HttpClient httpclient = new DefaultHttpClient();
    	if( System.getProperty("http.proxyHost") != null ) {
    		HttpHost proxy = new HttpHost( System.getProperty("http.proxyHost"), 
    				Integer.parseInt(System.getProperty("http.proxyPort")), "http");
    		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    		log.debug("Proxying via "+proxy);
    	} else {
    		log.debug("No web proxy.");
    	}
    	return httpclient;
    }
    */
	

	INSTANCE;

	protected static Logger logger = LogManager.getLogger(ImageIOStrategy.class);

	private ImageIOStrategy() {}

	@Override
	public byte[] convertFromUrlToPng(String url, String sourceContentType)
			throws ConverterException {
	    logger.info("ImageIOStrategy convert: "+url+" from "+sourceContentType);
		byte[] imageBytes = null;
		CloseableHttpClient httpclient = HttpClientFactory.createHttpClientOrProxy();
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
